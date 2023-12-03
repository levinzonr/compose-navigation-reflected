package com.levinzonr.reflected.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.PopUpToBuilder
import com.levinzonr.reflected.core.Destination
import com.levinzonr.reflected.core.DestinationDescriptor
import com.levinzonr.reflected.core.destinationDescriptor

public inline fun <reified T : Destination> NavController.navigateTo(
    destination: T,
    noinline builder: NavOptionsBuilder.() -> Unit,
) = navigateTo(destination, destinationDescriptor(), builder)

public inline fun <reified T : Destination> NavController.navigateTo(
    destination: T,
    descriptor: DestinationDescriptor<T>,
    noinline builder: NavOptionsBuilder.() -> Unit,
) = navigate(
    route = descriptor.serializeDestination(destination),
    builder = builder,
)

inline fun <reified T : Destination> NavOptionsBuilder.popUpTo(
    descriptor: DestinationDescriptor<T>,
    noinline popUpToBuilder: PopUpToBuilder.() -> Unit,
) = popUpTo(descriptor.route(), popUpToBuilder = popUpToBuilder)

inline fun <reified T : Destination> NavOptionsBuilder.popUpTo(
    noinline popUpToBuilder: PopUpToBuilder.() -> Unit,
) = popUpTo(destinationDescriptor<T>(), popUpToBuilder)
