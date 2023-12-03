package com.levinzonr.reflected.compiler.codegen.models

data class DestinationData(
    val name: String,
    val arguments: List<ArgumentData>,
    val packageName: String,
) {
    val qualifiedName = "$packageName.$name"

    val route: String get() = buildString {
        append(name)
        val required = arguments.filterNot { it.isOptional }
        val optionals = arguments.filter { it.isOptional }
        required.forEach {
            append("/${it.templatedName}")
        }
        optionals.forEachIndexed { index, arg ->
            if (index == 0) append("?") else append("&")
            append("${arg.name}=${arg.templatedName}")
        }
    }
}
