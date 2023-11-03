package com.example.easy_image.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EnterAnimation(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visibleState = MutableTransitionState(
            initialState = false
        ).apply { targetState = true },
        modifier = Modifier,
        enter = scaleIn(),
        exit = scaleOut(
        ),
    ) {
        content()
    }
}