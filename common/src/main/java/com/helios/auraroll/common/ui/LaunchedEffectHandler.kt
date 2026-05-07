package com.helios.auraroll.common.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * A Composable function that observes a [Flow] of effects and executes a given action
 * when a new effect is emitted. After the effect is processed, it's consumed.
 * This is useful for handling one-time events or side effects in a Compose UI
 * that are triggered from a ViewModel or another business logic layer.
 *
 * @param EFFECT The type of the effect.
 * @param effectFlow The [Flow] of nullable effects to observe. When a non-null effect is emitted,
 *                   `onEffect` will be called.
 * @param onEffect A suspend function that will be called with the non-null effect when it's emitted.
 *                 This is where you handle the side effect.
 * @param onConsumeEffect A function that will be called after `onEffect` has been executed.
 *                        This is typically used to signal that the effect has been processed and
 *                        should not be processed again (e.g., by setting the effect in the ViewModel to null).
 */
@Composable
fun <EFFECT> LaunchedEffectHandler(
    effectFlow: Flow<EFFECT?>,
    onEffect: suspend (EFFECT) -> Unit,
    onConsumeEffect: () -> Unit,
) {
    val effect by effectFlow.collectAsStateWithLifecycle(null)
    val currentOnEffect by rememberUpdatedState(onEffect)
    val currentOnConsumeEffect by rememberUpdatedState(onConsumeEffect)

    LaunchedEffect(effect) {
        effect?.let {
            currentOnEffect(it)
            currentOnConsumeEffect()
        }
    }
}