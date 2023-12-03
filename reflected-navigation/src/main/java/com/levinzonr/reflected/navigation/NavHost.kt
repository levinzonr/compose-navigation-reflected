package com.levinzonr.reflected.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.levinzonr.reflected.core.Destination
import com.levinzonr.reflected.core.DestinationDescriptor

@Composable
inline fun <reified T : Destination> NavHost(
    startDescriptor: DestinationDescriptor<T>,
    navController: NavHostController = rememberNavController(),
    noinline defaultEnterTransition: AnimatedEnterTransition = {
        fadeIn(
            animationSpec = tween(700),
        )
    },
    noinline defaultExitTransition: AnimatedExitTransition = {
        fadeOut(
            animationSpec = tween(700),
        )
    },
    noinline defaultPopEnterTransition: AnimatedEnterTransition = defaultEnterTransition,
    noinline defaultPopExitTransition: AnimatedExitTransition = defaultExitTransition,
    noinline builder: NavGraphBuilder.() -> Unit,
) {
    androidx.navigation.compose.NavHost(
        navController = navController,
        startDestination = startDescriptor.route(),
        builder = builder,
        popExitTransition = defaultPopExitTransition,
        popEnterTransition = defaultPopEnterTransition,
        enterTransition = defaultEnterTransition,
        exitTransition = defaultExitTransition,
    )
}
