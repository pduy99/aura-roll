<!--firebender-plan
name: onboarding ui rework
overview: Rework the three onboarding screens (Welcome, Indexing, Complete) to match the new screenshots: tighter copy on Welcome with a combined privacy pill, a tip line on Indexing, and a drastically simplified Complete screen with a check-mark icon and a white "ENTER THE SPECTRUM" CTA.
todos:
  - id: welcome
    content: "Update WelcomeScreen copy + combined privacy pill"
  - id: indexing
    content: "Add background-indexing tip line to IndexingScreen"
  - id: complete
    content: "Rework CompleteScreen to checkmark + 'ENTER THE SPECTRUM' CTA"
-->


## Scope

Visual/copy-only changes inside `feature/onboarding/impl`. No ViewModel / navigation / contract changes. `HeroIllustration` is reused as-is on Welcome.

## 1. WelcomeScreen — [WelcomeScreen.kt](feature/onboarding/impl/src/main/java/com/helios/auraroll/onboarding/impl/ui/WelcomeScreen.kt)

- Change headline to **"Memories in a new light."** (keep gradient `displaySmall`, with the second half — "new light." — rendered in the secondary/purple color to match the screenshot).
- Remove the body paragraph ("Aura Roll re-imagines…").
- Replace the separate `AuraBadge` + subtext with a single combined pill: shield icon, **"PRIVACY FIRST"** in purple, a small dot separator, then **"ON-DEVICE ONLY"** in muted on-surface. Implement inline (Row inside the rounded background) rather than extending `AuraBadge`, since this composition is screen-specific.
- Keep `permissionPreviouslyRevoked` error row, `Grant Permission` (white) and `Learn how it works` (ghost) buttons unchanged.
- Remove `verticalScroll` on this screen if it interferes with `weight(1f)` spacing — keep `weight(1f)` pushing CTAs to bottom (currently scroll + weight is broken; switch to non-scroll Column with weights to match screenshot proportions).

## 2. IndexingScreen — [IndexingScreen.kt](feature/onboarding/impl/src/main/java/com/helios/auraroll/onboarding/impl/ui/IndexingScreen.kt)

- Add new tip text below the system-status card and before the `Next` CTA:
  - **"Tip: You can safely close the app; indexing will continue in the background."**
  - `bodySmall`, `onSurfaceVariant`, centered, horizontal padding ~24dp.
- Keep card layout, progress bar, palette swatches, percent label as-is.
- Keep `Next` button (purple) gated by `indexingProgress >= 1f`.

## 3. CompleteScreen — [CompleteScreen.kt](feature/onboarding/impl/src/main/java/com/helios/auraroll/onboarding/impl/ui/CompleteScreen.kt)

Major simplification to match screenshot 3:

- Replace the swirl-image hero with a **circular check-mark badge**:
  - Outer soft purple radial glow (keep the existing pulsing `glowScale` animation).
  - Inner ~96dp circle: dark surface fill + 1dp purple border (`SecondaryFixed`).
  - Center: `Icons.Outlined.Check` (or `Icons.Rounded.Check`) tinted with the secondary/purple color, ~36dp.
- Headline: **"Ready to Explore."** (white, `displaySmall`, centered, no gradient).
- Body: **"Your memories are indexed and ready."** (`bodyMedium`, `onSurfaceVariant`).
- **Remove** the entire summary card (memories indexed, palette swatches, divider, on-device note) and the `formattedCount` / palette logic — no longer needed on this screen.
- CTA at the bottom: **`AuraPrimaryButton(text = "ENTER THE SPECTRUM", variant = AuraButtonVariant.White)`** — uppercase text passed directly, white background to match screenshot.
- Drop unused imports (`AutoAwesome`, `painterResource`, `R`, `NumberFormat`, `TertiaryFixed`, palette-related code).

## Notes

- No string resources currently used for these screens; keep hardcoded literals consistent with existing style.
- Preview composables updated with the new state shape (palette args remain valid on `OnboardingUiState` but are no longer rendered on Complete).

## Out of scope

- `OnboardingViewModel`, `OnboardingContract`, `OnboardingRoute`, `HeroIllustration`, design-system components.
