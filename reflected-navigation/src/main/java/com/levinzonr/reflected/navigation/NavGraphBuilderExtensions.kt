package com.levinzonr.reflected.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import com.levinzonr.reflected.core.Destination
import com.levinzonr.reflected.core.DestinationDescriptor
import androidx.navigation.compose.composable as coreComposable

context (NavGraphBuilder)
fun <T : Destination> DestinationDescriptor<T>.composable(
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit),
) {
    coreComposable(
        route = route(),
        arguments = navArgs,
        deepLinks = deepLinks,
        content = content,
    )
}
