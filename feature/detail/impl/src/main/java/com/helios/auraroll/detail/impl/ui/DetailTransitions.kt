package com.helios.auraroll.detail.impl.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.ui.graphics.TransformOrigin
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay

private const val ZOOM_DURATION_MS = 320
private const val FADE_DURATION_MS = 220

// Scale the incoming Detail screen up from 92 % so the zoom is felt without
// being aggressive. The outgoing Home gallery scales slightly past 1 so it
// reads as if the camera is pushing through it toward the photo.
private const val ENTER_INITIAL_SCALE = 0.92f
private const val POP_TARGET_SCALE = 0.92f
private const val OUTGOING_OVERSHOOT_SCALE = 1.06f

/**
 * Forward navigation into Detail: the new screen zooms in from 92 % while
 * fading in; the gallery underneath subtly scales past 1 and fades out so the
 * transition reads as a camera push toward the tapped photo.
 */
private val enterDetailTransition: AnimatedContentTransitionScope<Scene<*>>.() -> ContentTransform = {
    val enter = scaleIn(
        initialScale = ENTER_INITIAL_SCALE,
        transformOrigin = TransformOrigin.Center,
        animationSpec = tween(durationMillis = ZOOM_DURATION_MS),
    ) + fadeIn(animationSpec = tween(FADE_DURATION_MS))

    val exit = scaleOut(
        targetScale = OUTGOING_OVERSHOOT_SCALE,
        transformOrigin = TransformOrigin.Center,
        animationSpec = tween(durationMillis = ZOOM_DURATION_MS),
    ) + fadeOut(animationSpec = tween(FADE_DURATION_MS))

    enter togetherWith exit
}

/**
 * Pop navigation back to Home: the Detail screen zooms out to 92 % and fades
 * away while the gallery zooms back from a slight overshoot — the inverse of
 * the enter transition.
 */
private val exitDetailTransition: AnimatedContentTransitionScope<Scene<*>>.() -> ContentTransform = {
    val enter = scaleIn(
        initialScale = OUTGOING_OVERSHOOT_SCALE,
        transformOrigin = TransformOrigin.Center,
        animationSpec = tween(durationMillis = ZOOM_DURATION_MS),
    ) + fadeIn(animationSpec = tween(FADE_DURATION_MS))

    val exit = scaleOut(
        targetScale = POP_TARGET_SCALE,
        transformOrigin = TransformOrigin.Center,
        animationSpec = tween(durationMillis = ZOOM_DURATION_MS),
    ) + fadeOut(animationSpec = tween(FADE_DURATION_MS))

    enter togetherWith exit
}

/**
 * Metadata bundle attached to the Detail nav entry so [NavDisplay] uses the
 * zoomIn / zoomOut transitions on push, pop, and predictive back.
 */
internal val DetailTransitionMetadata: Map<String, Any> =
    NavDisplay.transitionSpec(enterDetailTransition) +
        NavDisplay.popTransitionSpec(exitDetailTransition) +
        NavDisplay.predictivePopTransitionSpec { _ -> exitDetailTransition() }
