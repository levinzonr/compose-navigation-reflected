package com.levinzonr.reflected.core

data class ArgumentDescriptor(
    val name: String,
    val type: ArgumentType,
    val defaultValue: Any?,
    val isOptional: Boolean,
    val isNullable: Boolean,
)
