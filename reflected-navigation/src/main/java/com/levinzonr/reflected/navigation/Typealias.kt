package com.levinzonr.reflected.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavBackStackEntry

typealias AnimatedEnterTransition =
    @JvmSuppressWildcards
    (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition)

typealias AnimatedExitTransition =
    @JvmSuppressWildcards
    (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition)
