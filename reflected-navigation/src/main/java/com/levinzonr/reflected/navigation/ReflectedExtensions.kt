package com.levinzonr.reflected.navigation

import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.levinzonr.reflected.core.ArgumentType
import com.levinzonr.reflected.core.Destination
import com.levinzonr.reflected.core.DestinationDescriptor

val DestinationDescriptor<*>.navArgs get() = arguments().map {
    navArgument(it.name) {
        type = it.type.navType
        nullable = it.isNullable
        if (it.isOptional) {
            defaultValue = it.defaultValue
        }
    }
}

inline fun <reified T : Destination> DestinationDescriptor<T>.serializeDestination(
    destination: T,
): String {
    return serializer().serialize(destination).direction
}

private val ArgumentType.navType: NavType<*>
    get() {
        return when (this) {
            ArgumentType.String -> NavType.StringType
            ArgumentType.Int -> NavType.IntType
            ArgumentType.Float -> NavType.FloatType
            ArgumentType.Boolean -> NavType.BoolType
            ArgumentType.Long -> NavType.LongType
        }
    }
