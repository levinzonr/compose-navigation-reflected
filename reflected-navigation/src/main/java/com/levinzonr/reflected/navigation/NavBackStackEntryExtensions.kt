package com.levinzonr.reflected.navigation

import android.os.Bundle
import androidx.navigation.NavBackStackEntry
import com.levinzonr.reflected.core.Argument
import com.levinzonr.reflected.core.ArgumentDescriptor
import com.levinzonr.reflected.core.ArgumentType
import com.levinzonr.reflected.core.Destination
import com.levinzonr.reflected.core.DestinationDescriptor
import com.levinzonr.reflected.core.SerializedDestination
import com.levinzonr.reflected.core.destinationDescriptor

fun <D : Destination> NavBackStackEntry.decodeDestination(): D {
    return decodeDestination(destinationDescriptor())
}

fun <D : Destination> NavBackStackEntry.decodeDestination(descriptor: DestinationDescriptor<D>): D {
    val expectedArguments = descriptor.arguments()
    val bundle = arguments ?: Bundle.EMPTY
    val providedArguments = expectedArguments.map { bundle.get(it) }
    return descriptor.serializer()
        .deserialize(SerializedDestination(descriptor.route(), providedArguments))
}

internal fun Bundle.get(descriptor: ArgumentDescriptor): Argument {
    val value = when (descriptor.type) {
        ArgumentType.String -> getString(descriptor.name)
        ArgumentType.Int -> getInt(descriptor.name)
        ArgumentType.Float -> getFloat(descriptor.name)
        ArgumentType.Boolean -> getBoolean(descriptor.name)
        ArgumentType.Long -> getLong(descriptor.name)
    }

    if (value == null) {
        require(descriptor.isNullable) {
            "Argument ${descriptor.name}is marked as not nullable, but no value was provide"
        }
    }

    return Argument(
        name = descriptor.name,
        value = value,
        isOptional = descriptor.isOptional,
    )
}
