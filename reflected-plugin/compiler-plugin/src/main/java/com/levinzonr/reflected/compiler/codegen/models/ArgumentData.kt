package com.levinzonr.reflected.compiler.codegen.models

import org.jetbrains.kotlin.ir.expressions.IrExpressionBody

data class ArgumentData(
    val name: String,
    val type: ArgumentType = ArgumentType.String,
    val isNullable: Boolean = false,
    val defaultValue: IrExpressionBody? = null,
) {
    val templatedName = "{$name}"

    val isOptional get() = defaultValue != null
}
