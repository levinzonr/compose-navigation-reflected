package com.levinzonr.reflected.navigation

import androidx.lifecycle.SavedStateHandle
import com.levinzonr.reflected.core.Argument
import com.levinzonr.reflected.core.ArgumentDescriptor
import com.levinzonr.reflected.core.ArgumentType
import com.levinzonr.reflected.core.Destination
import com.levinzonr.reflected.core.DestinationDescriptor
import com.levinzonr.reflected.core.SerializedDestination
import com.levinzonr.reflected.core.destinationDescriptor

fun <D : Destination> SavedStateHandle.getDestination() = getDestination<D>(destinationDescriptor())

fun <D : Destination> SavedStateHandle.getDestination(descriptor: DestinationDescriptor<D>): D {
    val expectedArguments = descriptor.arguments()
    val providedArguments = expectedArguments.map { get(it) }
    return descriptor.serializer()
        .deserialize(SerializedDestination(descriptor.route(), providedArguments))
}

internal fun SavedStateHandle.get(descriptor: ArgumentDescriptor): Argument {
    val value = when (descriptor.type) {
        ArgumentType.String -> get<String>(descriptor.name)
        ArgumentType.Int -> get<Int>(descriptor.name)
        ArgumentType.Float -> get<Float>(descriptor.name)
        ArgumentType.Boolean -> get<Boolean>(descriptor.name)
        ArgumentType.Long -> get<Long>(descriptor.name)
    }

    if (value == null) {
        check(descriptor.isNullable) {
            "Argument ${descriptor.name} is marked as not nullable yet not argument was provided"
        }
    }

    return Argument(
        name = descriptor.name,
        value = value,
        isOptional = descriptor.isOptional,
    )
}
