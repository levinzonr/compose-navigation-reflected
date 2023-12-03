package com.levinzonr.reflected.compiler

import com.levinzonr.reflected.compiler.codegen.DescriptorCodegen
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.addMember
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetObjectValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrVarargImpl
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.visitors.IrElementTransformerVoid
import org.jetbrains.kotlin.ir.visitors.transformChildrenVoid

class ReflectDescriptorIrExtension(
    private val collector: MessageCollector,
) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        collector.report(CompilerMessageSeverity.WARNING, moduleFragment.dump())
        val context = ReflectedContext(pluginContext)
        val destinations = mutableListOf<IrClass>()
        moduleFragment.transformChildrenVoid(object : IrElementTransformerVoid() {
            override fun visitClass(declaration: IrClass): IrStatement {
                if (declaration.superTypes.contains(context.destinationSymbol.defaultType)) {
                    collector.report(CompilerMessageSeverity.WARNING, "Visit Class")
                    val destinationData = IrClassToDestinationDataMapper.map(declaration)
                    val newObject =
                        DescriptorCodegen(ReflectedContext(pluginContext), collector).generate(
                            declaration,
                            destinationData,
                        )
                    declaration.addMember(newObject)
                    destinations.add(declaration)
                }
                return declaration
            }
        })

        moduleFragment.transformChildrenVoid(object : IrElementTransformerVoid() {
            override fun visitProperty(declaration: IrProperty): IrStatement {
                if (declaration.name.asString() == "descriptors") {
                    return declaration.apply {
                        val listOfFun = pluginContext.listOfFunction
                        val descriptor = context.destinationDescriptorSymbol
                        val calls = destinations.map {
                            val symbol = it.declarations
                                .filterIsInstance<IrClass>()
                                .filter { it.isCompanion }
                                .first { it.name.asString() == "Descriptor" }
                            IrGetObjectValueImpl(
                                UNDEFINED_OFFSET,
                                UNDEFINED_OFFSET,
                                symbol.defaultType,
                                symbol.symbol,
                            )
                        }

                        declaration.backingField?.initializer =
                            pluginContext.irFactory.createExpressionBody(
                                UNDEFINED_OFFSET, UNDEFINED_OFFSET,
                                IrCallImpl(
                                    UNDEFINED_OFFSET,
                                    UNDEFINED_OFFSET,
                                    type = pluginContext.irBuiltIns
                                        .listClass
                                        .typeWith(descriptor.defaultType),
                                    symbol = listOfFun,
                                    typeArgumentsCount = 1,
                                    valueArgumentsCount = 1,
                                ).apply {
                                    putTypeArgument(0, descriptor.defaultType)
                                    putValueArgument(
                                        0,
                                        IrVarargImpl(
                                            UNDEFINED_OFFSET,
                                            UNDEFINED_OFFSET,
                                            type,
                                            descriptor.defaultType,
                                            calls,
                                        ),
                                    )
                                },
                            )
                    }
                } else {
                    return declaration
                }
            }
        })
    }
}
