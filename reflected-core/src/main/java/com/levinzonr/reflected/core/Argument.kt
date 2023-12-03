package com.levinzonr.reflected.core

data class Argument(
    val name: String,
    val value: Any?,
    val isOptional: Boolean,
)
