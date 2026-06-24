package com.example.coffeeapp.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally

object NavTransitions {

    val enterTransition: EnterTransition
        get() = slideInHorizontally(initialOffsetX = { it }) + fadeIn()

    val exitTransition: ExitTransition
        get() = slideOutHorizontally(targetOffsetX = { -it / 3 }) + fadeOut()

    val popEnterTransition: EnterTransition
        get() = slideInHorizontally(initialOffsetX = { -it / 3 }) + fadeIn()

    val popExitTransition: ExitTransition
        get() = slideOutHorizontally(targetOffsetX = { it }) + fadeOut()
}