package com.levinzonr.reflected.compiler.codegen.models

enum class ArgumentType(val reference: kotlin.String) {
    String(References.TYPE_STRING),
    Int(References.TYPE_INT),
    Float(References.TYPE_FLOAT),
    Boolean(References.TYPE_BOOL),
    Long(References.TYPE_LONG),
}
