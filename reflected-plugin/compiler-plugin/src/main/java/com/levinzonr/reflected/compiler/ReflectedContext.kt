package com.levinzonr.reflected.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

class ReflectedContext(
    private val irPluginContext: IrPluginContext,
) : IrPluginContext by irPluginContext {

    val descriptiveDecode: IrSimpleFunctionSymbol by lazy {
        referenceFunctions("com.levinzonr.reflected.navigation.decodeDestination")
            .first { it.owner.valueParameters.isNotEmpty() }
    }

    val descriptiveSavedStateDecode: IrSimpleFunctionSymbol by lazy {
        referenceFunctions("com.levinzonr.reflected.navigation.getDestination")
            .first { it.owner.valueParameters.isNotEmpty() }
    }

    val destinationSymbol: IrClassSymbol by lazy {
        hardReferenceClass("com.levinzonr.reflected.core.Destination")
    }

    val argumentSymbol: IrClassSymbol by lazy {
        hardReferenceClass("com.levinzonr.reflected.core.Argument")
    }

    val destinationDescriptorSymbol: IrClassSymbol by lazy {
        hardReferenceClass("com.levinzonr.reflected.core.DestinationDescriptor")
    }

    val argumentDescriptorSymbol by lazy {
        hardReferenceClass("com.levinzonr.reflected.core.ArgumentDescriptor")
    }

    val argumentTypeSymbol by lazy {
        hardReferenceClass("com.levinzonr.reflected.core.ArgumentType")
    }

    val serializerSymbol by lazy {
        hardReferenceClass("com.levinzonr.reflected.core.DestinationSerializer")
    }

    val serializedDestination by lazy {
        hardReferenceClass("com.levinzonr.reflected.core.SerializedDestination")
    }

    val composableDescriptorFunction by lazy {
        hardReferenceFunction(
            "com.levinzonr.reflected.navigation.composableDescriptor",
        )
    }

    val descriptiveNavigateTo by lazy {
        referenceFunctions("com.levinzonr.reflected.navigation.navigateTo")
            .first { it.owner.valueParameters.size == 3 }
    }

    val descriptivePopupTo by lazy {
        referenceFunctions("com.levinzonr.reflected.navigation.popUpTo")
            .first { it.owner.valueParameters.size == 2 }
    }

    val composableFunction by lazy {
        hardReferenceFunction("androidx.navigation.compose.composable")
    }

    private fun hardReferenceClass(fqName: String): IrClassSymbol {
        val classId = ClassId.topLevel(FqName(fqName))
        return checkNotNull(referenceClass(classId)) {
            "Failed to find $fqName class"
        }
    }

    private fun hardReferenceFunction(fqName: String): IrSimpleFunctionSymbol {
        return referenceFunctions(fqName).first()
    }

    private fun referenceFunctions(fqName: String): Collection<IrSimpleFunctionSymbol> {
        return referenceFunctions(
            CallableId(
                packageName = FqName(fqName.split(".").dropLast(1).joinToString(".")),
                callableName = Name.identifier(fqName.split(".").last()),
            ),
        )
    }
}
