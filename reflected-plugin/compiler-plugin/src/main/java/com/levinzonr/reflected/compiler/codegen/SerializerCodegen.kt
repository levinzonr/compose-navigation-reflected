package com.levinzonr.reflected.compiler.codegen

import com.levinzonr.reflected.compiler.ReflectedContext
import com.levinzonr.reflected.compiler.buildObject
import com.levinzonr.reflected.compiler.codegen.models.DestinationData
import com.levinzonr.reflected.compiler.lazyFunctionByName
import com.levinzonr.reflected.compiler.listOfFunction
import com.levinzonr.reflected.compiler.substituteGenericTypeWith
import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.irBoolean
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irIfThenElse
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irVararg
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.getSimpleFunction
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.name.Name

class SerializerCodegen(private val pluginContext: ReflectedContext) {

    fun generate(data: DestinationData, destinationSymbol: IrClassSymbol): IrClass {
        val testInterface = pluginContext.serializerSymbol
        val anonymousObject = pluginContext.buildObject {
            name = Name.special("<anonymous>")
            origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
            visibility = DescriptorVisibilities.LOCAL
        }.apply {
            superTypes += testInterface.substituteGenericTypeWith(destinationSymbol.owner)
        }

        anonymousObject.addSerializeFunction(data, destinationSymbol)
        anonymousObject.addDeserializeFunction(destinationSymbol)

        return anonymousObject
    }

    private fun IrClass.addDeserializeFunction(destinationSymbol: IrClassSymbol) {
        val baseInterface = pluginContext.serializerSymbol
        val function = baseInterface.owner.lazyFunctionByName("deserialize")

        val serializedDestination = pluginContext.serializedDestination

        addFunction(function.name.asString(), destinationSymbol.defaultType, Modality.OPEN).apply {
            overriddenSymbols += function.symbol
            addValueParameter("serializedDestination", serializedDestination.defaultType)

            body = DeclarationIrBuilder(pluginContext, symbol).irBlockBody {
                val destParam =
                    valueParameters.first { it.name.asString() == "serializedDestination" }
                val destClass = destParam.type.getClass()!!

                val symbolConst = destinationSymbol.constructors.first()
                val constMember = symbolConst.owner.valueParameters

                val requiredFuncCall = destClass.getSimpleFunction("arg")!!

                val cal = irCall(symbolConst).apply {
                    constMember.forEachIndexed { index, irValueParameter ->
                        if (irValueParameter.defaultValue == null) {
                            putValueArgument(
                                index,
                                irCall(requiredFuncCall).apply {
                                    dispatchReceiver = irGet(destParam)
                                    putValueArgument(0, irString(irValueParameter.name.asString()))
                                },
                            )
                        } else {
                            val nullableCall = irCall(requiredFuncCall).apply {
                                dispatchReceiver = irGet(destParam)
                                putValueArgument(0, irString(irValueParameter.name.asString()))
                            }

                            val hasArgumentCall =
                                irCall(destClass.getSimpleFunction("hasArgument")!!).apply {
                                    dispatchReceiver = irGet(destParam)
                                    putValueArgument(0, irString(irValueParameter.name.asString()))
                                }

                            putValueArgument(
                                index,
                                irIfThenElse(
                                    irValueParameter.type,
                                    hasArgumentCall,
                                    nullableCall,
                                    irValueParameter.defaultValue!!.expression,
                                ),
                            )
                        }
                    }
                }
                +irReturn(cal)
            }
        }
    }

    private fun IrClass.addSerializeFunction(
        data: DestinationData,
        destinationSymbol: IrClassSymbol,
    ) {
        val baseInterface = pluginContext.serializerSymbol
        val serializedDestinationSymbol = pluginContext.serializedDestination
        val argumentSymbol = pluginContext.argumentSymbol
        val function = baseInterface.owner.lazyFunctionByName("serialize")
        val listFunction = pluginContext.listOfFunction
        addFunction(function.name.asString(), function.returnType, Modality.OPEN).apply {
            overriddenSymbols += function.symbol
            addValueParameter("destination", destinationSymbol.defaultType)

            body = DeclarationIrBuilder(pluginContext, symbol).irBlockBody {
                val destParam = valueParameters.first { it.name.asString() == "destination" }
                val destClass = destParam.type.getClass()!!

                val paramsCalls = data.arguments.map { arg ->
                    irCall(argumentSymbol.constructors.first()).apply {
                        putValueArgument(0, irString(arg.name))
                        putValueArgument(
                            1,
                            irCall(
                                destClass.properties
                                    .first { it.name.asString() == arg.name }.getter!!,
                            ).apply {
                                dispatchReceiver = irGet(destParam)
                            },
                        )
                        putValueArgument(2, irBoolean(arg.isOptional))
                    }
                }

                val listOfCall = irCall(listFunction).apply {
                    putValueArgument(0, irVararg(argumentSymbol.defaultType, paramsCalls))
                }

                val serializedCall =
                    irCall(serializedDestinationSymbol.constructors.first()).apply {
                        putValueArgument(0, irString(data.name))
                        putValueArgument(1, listOfCall)
                    }

                +irReturn(serializedCall)
            }
        }
    }
}
