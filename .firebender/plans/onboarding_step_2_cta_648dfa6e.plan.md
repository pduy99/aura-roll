<!--firebender-plan
name: onboarding step 2 cta
overview: Replace the "Next" button on the indexing screen with the existing "ENTER THE SPECTRUM" CTA (enabled only at 100%), which navigates directly to Home, and remove the now-unused step 3 (CompleteScreen) entirely.
-->


## Changes

### 1. `IndexingScreen.kt`
- Replace the `AuraPrimaryButton(text = "Next", variant = Purple, ...)` at the bottom with `AuraPrimaryButton(text = "ENTER THE SPECTRUM", variant = AuraButtonVariant.White, enabled = uiState.indexingProgress >= 1f, onClick = onEnterSpectrumClick)`.
- Rename the screen's `onNextClick` lambda parameter to `onEnterSpectrumClick` for clarity (single callsite).
- Update preview accordingly.

### 2. `OnboardingContract.kt`
- Remove `OnboardingPage.COMPLETE` from the enum.
- Remove `OnboardingAction.NextAfterIndexing` (no longer needed; indexing screen now triggers `ExploreGallery` directly).

### 3. `OnboardingViewModel.kt`
- Remove the `OnboardingAction.NextAfterIndexing` branch in `onAction`.
- Indexing screen will dispatch `OnboardingAction.ExploreGallery`, which already sets onboarding completed and emits `OnboardingEffect.NavigateToHome`.

### 4. `OnboardingRoute.kt`
- Remove the `OnboardingPage.COMPLETE -> CompleteScreen(...)` branch from the `when`.
- Wire `IndexingScreen`'s callback to dispatch `OnboardingAction.ExploreGallery` instead of `NextAfterIndexing`.

### 5. Delete `CompleteScreen.kt`
- File is no longer referenced; delete it entirely.

## Result
Two-step onboarding: Welcome -> Indexing. The "ENTER THE SPECTRUM" button appears on the indexing screen, becomes enabled at 100%, and navigates to Home on click (with onboarding marked complete via existing `ExploreGallery` action path).
