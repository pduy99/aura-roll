<!--firebender-plan
name: onboarding_screen_refinements
overview: Remove the "Learn how it works" CTA from welcome, gate the spectrum CTA on indexing completion, and add a rotating tips carousel (including an offline-first tip) to the indexing screen.
todos:
  - id: welcome
    content: "Remove Learn-how-it-works button + action plumbing from welcome screen, route, contract, viewmodel"
  - id: cta-gate
    content: "Render Enter-the-Spectrum button only when indexingProgress >= 1f"
  - id: tips
    content: "Add rotating tips carousel with 4 tips (incl. offline-first) on IndexingScreen"
-->


## Changes

### 1. Welcome screen — remove "Learn how it works"
- [WelcomeScreen.kt](feature/onboarding/impl/src/main/java/com/helios/auraroll/onboarding/impl/ui/WelcomeScreen.kt): drop the `AuraGhostButton`, the preceding `Spacer`, the `onLearnHowItWorksClick` parameter, the unused import, and the preview's lambda. Simplify the trailing `Column { ... }` to render only the `AuraPrimaryButton` (or inline it).
- [OnboardingRoute.kt](feature/onboarding/impl/src/main/java/com/helios/auraroll/onboarding/impl/ui/OnboardingRoute.kt): stop passing `onLearnHowItWorksClick` to `WelcomeScreen`.
- [OnboardingContract.kt](feature/onboarding/impl/src/main/java/com/helios/auraroll/onboarding/impl/ui/OnboardingContract.kt): remove `OnboardingAction.LearnHowItWorks`.
- [OnboardingViewModel.kt](feature/onboarding/impl/src/main/java/com/helios/auraroll/onboarding/impl/ui/OnboardingViewModel.kt): delete the `LearnHowItWorks` branch in `onAction`.

### 2. Indexing screen — gate the CTA
- [IndexingScreen.kt](feature/onboarding/impl/src/main/java/com/helios/auraroll/onboarding/impl/ui/IndexingScreen.kt): replace the always-rendered `AuraPrimaryButton(... enabled = uiState.indexingProgress >= 1f)` with a conditional render — the button only enters composition once `uiState.indexingProgress >= 1f`. Reserve layout space (e.g. wrap in a fixed-height `Box` or use `AnimatedVisibility` with a fade-in) so the layout doesn't jump when it appears.

### 3. Indexing screen — rotating tips carousel
Replace the single hardcoded tip `Text` with a rotating carousel.

- Define a private `val indexingTips: ImmutableList<String>` of ~4 tips inside `IndexingScreen.kt`. Suggested copy:
  - "Aura Roll indexes everything offline first — your photos never leave the device."
  - "Tip: You can safely close the app; indexing will continue in the background."
  - "Each photo is analyzed for its dominant hues to power the spectrum view."
  - "Once indexing finishes, drag the spectrum slider to filter memories by color."
- Add a `RotatingTip` private `@Composable` that:
  - Holds `var index by remember { mutableIntStateOf(0) }`.
  - In `LaunchedEffect(Unit) { while (true) { delay(5_000); index = (index + 1) % tips.size } }`.
  - Renders the current tip via `AnimatedContent(targetState = index, transitionSpec = { fadeIn(...) togetherWith fadeOut(...) })` showing a `Text` styled identically to the existing tip (`bodySmall`, `onSurfaceVariant`, centered, horizontal padding 16dp).
- Drop in `RotatingTip(tips = indexingTips)` where the existing tip `Text` was, keeping the surrounding `Spacer`s.

No ViewModel/state changes required for tips — purely UI rotation.

## Files touched
- `feature/onboarding/impl/src/main/java/com/helios/auraroll/onboarding/impl/ui/WelcomeScreen.kt`
- `feature/onboarding/impl/src/main/java/com/helios/auraroll/onboarding/impl/ui/IndexingScreen.kt`
- `feature/onboarding/impl/src/main/java/com/helios/auraroll/onboarding/impl/ui/OnboardingRoute.kt`
- `feature/onboarding/impl/src/main/java/com/helios/auraroll/onboarding/impl/ui/OnboardingContract.kt`
- `feature/onboarding/impl/src/main/java/com/helios/auraroll/onboarding/impl/ui/OnboardingViewModel.kt`
