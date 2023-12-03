package com.levinzonr.reflected.compiler

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.builders.declarations.IrClassBuilder
import org.jetbrains.kotlin.ir.builders.declarations.addConstructor
import org.jetbrains.kotlin.ir.builders.declarations.buildClass
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationContainer
import org.jetbrains.kotlin.ir.declarations.lazy.IrLazyEnumEntryImpl
import org.jetbrains.kotlin.ir.declarations.lazy.IrLazyFunction
import org.jetbrains.kotlin.ir.declarations.lazy.IrLazyProperty
import org.jetbrains.kotlin.ir.expressions.impl.IrBlockBodyImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.util.addSimpleDelegatingConstructor
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.createParameterDeclarations
import org.jetbrains.kotlin.ir.util.defaultType
import org.jetbrains.kotlin.ir.util.isVararg
import org.jetbrains.kotlin.ir.util.substitute
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

val IrDeclarationContainer.lazyFunctions: List<IrLazyFunction>
    get() = declarations
        .filterIsInstance<IrLazyFunction>()

fun IrClassSymbol.substituteGenericTypeWith(irClass: IrClass): IrType {
    val typeParam = owner.typeParameters.first()
    val newTypeParam = irClass.defaultType
    return defaultType.substitute(mapOf(typeParam.symbol to newTypeParam))
}

fun IrDeclarationContainer.lazyFunctionByName(name: String): IrLazyFunction {
    return lazyFunctions.first { it.name.asString() == name }
}

val IrDeclarationContainer.lazyProperties: List<IrLazyProperty>
    get() = declarations
        .filterIsInstance<IrLazyProperty>()

val IrDeclarationContainer.lazyEnumEntries: List<IrLazyEnumEntryImpl>
    get() = declarations
        .filterIsInstance<IrLazyEnumEntryImpl>()

val IrPluginContext.listOfFunction
    get() = referenceFunctions(CallableId(FqName("kotlin.collections"), Name.identifier("listOf")))
        .single {
            it.owner.valueParameters.size == 1 && it.owner.valueParameters[0].isVararg
        }

fun IrPluginContext.buildObject(builder: IrClassBuilder.() -> Unit): IrClass {
    return irFactory.buildClass {
        kind = ClassKind.OBJECT
        builder()
    }.apply {
        createParameterDeclarations()
        addSimpleDelegatingConstructor(
            irBuiltIns.anyClass.constructors.first().owner,
            irBuiltIns,
            true,
        )
        addConstructor {
            isPrimary = false
            visibility = DescriptorVisibilities.PRIVATE
            returnType = defaultType
        }.apply {
            body = IrBlockBodyImpl(UNDEFINED_OFFSET, UNDEFINED_OFFSET)
        }
    }
}
