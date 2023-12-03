package com.levinzonr.reflected.compiler

import com.levinzonr.reflected.compiler.codegen.models.ArgumentData
import com.levinzonr.reflected.compiler.codegen.models.ArgumentType
import com.levinzonr.reflected.compiler.codegen.models.DestinationData
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.isBoolean
import org.jetbrains.kotlin.ir.types.isFloat
import org.jetbrains.kotlin.ir.types.isInt
import org.jetbrains.kotlin.ir.types.isLong
import org.jetbrains.kotlin.ir.types.isNullable
import org.jetbrains.kotlin.ir.types.isNullableString
import org.jetbrains.kotlin.ir.types.isString
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.packageFqName

object IrClassToDestinationDataMapper {

    fun map(declaration: IrClass): DestinationData {
        val constructor = declaration.constructors.first()
        return DestinationData(
            name = declaration.name.toString(),
            packageName = declaration.packageFqName?.asString()!!,
            arguments = constructor.valueParameters.map { param ->
                ArgumentData(
                    name = param.name.toString(),
                    type = param.type.argumentType,
                    isNullable = param.type.isNullable(),
                    defaultValue = param.defaultValue,
                )
            },
        )
    }

    private val IrType.argumentType: ArgumentType
        get() {
            return when {
                isInt() -> ArgumentType.Int
                isString() -> ArgumentType.String
                isNullableString() -> ArgumentType.String
                isFloat() -> ArgumentType.Float
                isBoolean() -> ArgumentType.Boolean
                isLong() -> ArgumentType.Long
                else -> throw IllegalStateException("Argument type $this is not supported")
            }
        }
}
