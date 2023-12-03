package com.levinzonr.reflected.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.backend.js.utils.asString
import org.jetbrains.kotlin.ir.backend.js.utils.typeArguments
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

class ReflectedExpressionsExtension(
    private val collector: MessageCollector,
) : IrGenerationExtension {

    enum class SupportedCalls(val value: String) {
        Descriptor(
            "com.levinzonr.reflected.core.DestinationDescriptorFactoryKt.destinationDescriptor",
        ),
        NavigateTo("com.levinzonr.reflected.navigation.NavControllerExtensionKt.navigateTo"),
        PopUpTo("com.levinzonr.reflected.navigation.NavControllerExtensionKt.popUpTo"),
        DecodeDestination(
            "com.levinzonr.reflected.navigation.NavBackStackEntryExtensionsKt.decodeDestination",
        ),
        GetDestination(
            "com.levinzonr.reflected.navigation.SavedStateHandleExtensionsKt.getDestination",
        ),
        Unknown("NOT_DEFINED"),
    }

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val context = ReflectedContext(pluginContext)
        moduleFragment.transformCalls { call ->
            val name = call.symbol.owner.fqNameWhenAvailable?.asString()
            collector.report(CompilerMessageSeverity.WARNING, "---$name")
            val type = SupportedCalls.entries.find { it.value == name } ?: SupportedCalls.Unknown
            when (type) {
                SupportedCalls.Descriptor -> {
                    val descriptor = call.typeArguments.first()!!
                    descriptor.descriptorExpression()
                }

                SupportedCalls.NavigateTo -> {
                    call.transformNonDescriptiveCall(context, context.descriptiveNavigateTo) {
                        putValueArgument(0, call.valueArguments.first())
                        putValueArgument(1, call.typeArguments.first()!!.descriptorExpression())
                        putValueArgument(2, call.valueArguments.last())
                    }
                }

                SupportedCalls.PopUpTo -> {
                    call.transformNonDescriptiveCall(context, context.descriptivePopupTo) {
                        putValueArgument(0, call.valueArguments.first())
                        putValueArgument(1, call.typeArguments.first()!!.descriptorExpression())
                        putValueArgument(2, call.valueArguments.last())
                    }
                }

                SupportedCalls.DecodeDestination -> {
                    call.transformNonDescriptiveCall(context, context.descriptiveDecode) {
                        putValueArgument(0, call.typeArguments.first()!!.descriptorExpression())
                    }
                }

                SupportedCalls.GetDestination -> {
                    call.transformNonDescriptiveCall(
                        context,
                        context.descriptiveSavedStateDecode,
                    ) {
                        putValueArgument(0, call.typeArguments.first()!!.descriptorExpression())
                    }
                }

                SupportedCalls.Unknown -> call
            }
        }
    }

    private fun IrElement.transformCalls(transformer: (IrCall) -> IrExpression) {
        transformChildrenVoid(object : IrElementTransformerVoid() {
            override fun visitCall(expression: IrCall): IrExpression {
                super.visitCall(expression)
                return transformer(expression)
            }
        })
    }

    private fun IrCall.transformNonDescriptiveCall(
        context: ReflectedContext,
        descriptiveCall: IrSimpleFunctionSymbol,
        builder: IrCallImpl.() -> Unit,
    ): IrExpression {
        val original = this
        if (hasDescriptorInParameters(context)) {
            return this
        } else {
            return IrCallImpl(
                startOffset,
                endOffset,
                original.type,
                descriptiveCall,
                original.typeArgumentsCount,
                descriptiveCall.owner.valueParameters.size,
                original.origin,
            ).apply {
                extensionReceiver = original.extensionReceiver
                dispatchReceiver = original.dispatchReceiver
                putTypeArgument(0, original.typeArguments.first())
                builder()
            }
        }
    }

    private fun IrCall.hasDescriptorInParameters(context: ReflectedContext): Boolean {
        val descriptorType = context.destinationDescriptorSymbol.defaultType
        return symbol.owner.valueParameters.any { it.type.asString() == descriptorType.asString() }
    }

    private fun IrType.descriptorExpression(): IrExpression {
        val dest = requireNotNull(getClass())
        val symbol = dest.declarations
            .filterIsInstance<IrClass>()
            .filter { it.isCompanion }
            .first { it.name.asString() == "Descriptor" }

        return IrGetObjectValueImpl(
            startOffset = UNDEFINED_OFFSET,
            endOffset = UNDEFINED_OFFSET,
            type = symbol.defaultType,
            symbol = symbol.symbol,
        )
    }
}
