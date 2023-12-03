package com.levinzonr.reflected.core

data class SerializedDestination(
    val name: String,
    val arguments: List<Argument>,
) {

    val direction: String = buildString {
        append(name)
        val optionals = arguments.filter { it.isOptional }
        val required = arguments.filterNot { it.isOptional }
        required.forEach {
            append("/${it.value}")
        }

        optionals.forEachIndexed { index, argument ->
            if (index == 0) append("?") else append("&")
            append("${argument.name}=${argument.value}")
        }
    }

    fun <T> arg(name: String): T {
        return arguments.first { it.name == name }.value as T
    }

    fun hasArgument(name: String): Boolean {
        return arguments.any { it.name == name }
    }
}
