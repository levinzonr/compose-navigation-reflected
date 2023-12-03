package com.levinzonr.reflected.compiler.codegen.models

data class OptionalData(
    val type: ArgumentType,
    val value: Any?,
    val name: String,
)
