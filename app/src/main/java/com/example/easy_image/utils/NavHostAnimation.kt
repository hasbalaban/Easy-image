package com.example.easy_image.utils

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset


@Composable
fun EnterAnimation(content: @Composable () -> Unit) {
    AnimatedVisibility(
        visibleState = MutableTransitionState(
            initialState = false
        ).apply { targetState = true },
        modifier = Modifier,
        enter = slideInHorizontally(
            animationSpec = spring(
                stiffness = Spring.DampingRatioLowBouncy,
                visibilityThreshold = IntOffset.VisibilityThreshold
            )
        ) + slideInVertically(),
        exit = slideOutHorizontally() + shrinkVertically() ,
    ) {
        content()
    }
}