package com.levinzonr.reflected.compiler.codegen

import com.levinzonr.reflected.compiler.ReflectedContext
import com.levinzonr.reflected.compiler.buildObject
import com.levinzonr.reflected.compiler.codegen.models.ArgumentData
import com.levinzonr.reflected.compiler.codegen.models.DestinationData
import com.levinzonr.reflected.compiler.lazyEnumEntries
import com.levinzonr.reflected.compiler.lazyFunctionByName
import com.levinzonr.reflected.compiler.lazyFunctions
import com.levinzonr.reflected.compiler.listOfFunction
import com.levinzonr.reflected.compiler.substituteGenericTypeWith
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irBoolean
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irNull
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.addMember
import org.jetbrains.kotlin.ir.declarations.lazy.IrLazyEnumEntryImpl
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrGetEnumValueImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.hasEqualFqName
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class DescriptorCodegen(
    private val context: ReflectedContext,
    private val collector: MessageCollector,
) {

    fun generate(newParent: IrClass, destinationData: DestinationData): IrClass {
        return context.buildObject {
            name = Name.identifier("Descriptor")
            isCompanion = true
        }.apply {
            parent = newParent
            superTypes += context.destinationDescriptorSymbol.substituteGenericTypeWith(newParent)
            addRouteFunction(destinationData.route)
            addArgumentsFunction(destinationData.arguments)
            addSerializer(newParent, destinationData)
            addSerializer(newParent, destinationData)
        }
    }

    private fun IrClass.addSerializer(parent: IrClass, destinationData: DestinationData) {
        val ref = context.destinationDescriptorSymbol
        val baseSerializeFun = ref.owner.lazyFunctionByName("serializer")
        val obj = SerializerCodegen(context).generate(destinationData, parent.symbol)
        obj.parent = this
        addMember(obj)
        addFunction(
            baseSerializeFun.name.asString(),
            baseSerializeFun.returnType,
            Modality.OPEN,
        ).apply {
            overriddenSymbols += baseSerializeFun.symbol
            body = DeclarationIrBuilder(context, symbol).irBlockBody {
                +irReturn(irCall(obj.constructors.first()))
            }
        }
    }

    private fun IrClass.addArgumentsFunction(arguments: List<ArgumentData>) {
        val argumentsDeclaration = context.destinationDescriptorSymbol
            .owner
            .lazyFunctions.first { it.name.asString() == "arguments" }

        val argumentRef = context.argumentDescriptorSymbol.owner
        val typeRef = context.argumentTypeSymbol.owner
        collector.report(
            CompilerMessageSeverity.WARNING,
            typeRef.lazyEnumEntries.joinToString("\n"),
        )
        val constructor = argumentRef.constructors.first()

        val listOfFun = context.listOfFunction

        /**
         * val name: String,
         *     val type: ArgumentType,
         *     val value: Any?,
         *     val isOptional: Boolean,
         *     val isNullable: Boolean
         */

        addFunction("arguments", argumentsDeclaration.returnType, Modality.OPEN).apply {
            overriddenSymbols += argumentsDeclaration.symbol
            body = DeclarationIrBuilder(context, symbol, startOffset, endOffset).irBlockBody {
                val argumentCalls = arguments.map { arg ->
                    val enumEntry = typeRef.lazyEnumEntries
                        .first { it.hasEqualFqName(FqName(arg.type.reference)) }

                    irCall(constructor).apply {
                        putValueArgument(0, irString(arg.name))
                        putValueArgument(1, irGetEnumValue(typeRef.defaultType, enumEntry))
                        putValueArgument(
                            2,
                            arg.defaultValue?.expression
                                ?: irNull(context.irBuiltIns.anyType),
                        )
                        putValueArgument(3, irBoolean(arg.isOptional))
                        putValueArgument(4, irBoolean(arg.isNullable))
                    }
                }

                val argumentsList = irCall(listOfFun).apply {
                    putValueArgument(0, irVararg(argumentRef.defaultType, argumentCalls))
                }

                +irReturn(argumentsList)
            }
        }
    }

    private fun irGetEnumValue(type: IrType, entryImpl: IrLazyEnumEntryImpl): IrExpression {
        return IrGetEnumValueImpl(UNDEFINED_OFFSET, UNDEFINED_OFFSET, type, entryImpl.symbol)
    }

    private fun IrClass.addRouteFunction(value: String) {
        val ref = context.destinationDescriptorSymbol
        addFunction("route", context.irBuiltIns.stringType).apply {
            modality = Modality.OPEN
            body = DeclarationIrBuilder(context, symbol, startOffset, endOffset).irBlockBody {
                +irReturn(irString(value))
                overriddenSymbols += ref.owner
                    .lazyFunctions
                    .first { it.name.asString() == "route" }
                    .symbol
            }
        }
    }
}
