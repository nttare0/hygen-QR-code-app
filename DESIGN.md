# QuickPay QR — Design System ("Soft UI, grown up")

Source of truth for the redesign. The runnable expression lives in `web/`
(live preview); the Android/Compose port maps 1:1 onto the tokens below.

## Direction

Keep the tactile neumorphic identity — it is the product's soul and it is well
executed — but fix the four ways it was working against a payments product:

1. **Legibility.** Every text token clears WCAG AA (4.5:1) on the surface it is
   actually used on, in both themes. The old muted text (3.2:1) and the teal
   used for "verified" text (2.9:1) were the worst offenders.
2. **Affordance.** Raised = actionable, sunk = container/well, flat = text.
   The old build used the same pale slab for all three.
3. **Tokens, not conventions.** One radius scale, one elevation scale, one
   shadow recipe per theme. The old build had 12+ radius literals per screen.
4. **Honesty.** The UI may only show a state the system actually has
   (see *Honesty contract*).

## Colour tokens

| Token | Light | Dark | Use |
|---|---|---|---|
| `bg` | `#EDF2FA` | `#141924` | page canvas |
| `surface` | `#FFFFFF` | `#1B2230` | raised cards, buttons |
| `sunk` / `sunk-deep` | `#E3EAF5` / `#D9E2F0` | `#10151F` / `#0B0F17` | wells, inputs |
| `ink` | `#101B2D` (15.4:1) | `#E9EFF8` (14.6:1) | primary text |
| `ink-2` | `#44536B` (7.3:1) | `#AEBACD` (7.9:1) | secondary text |
| `ink-3` | `#5A6B85` (4.8:1) | `#8C99AD` (5.6:1) | smallest text, still AA |
| `accent` | `#5B5CE2` | `#6E6FEE` | fills, large text |
| `accent-ink` | `#4344B8` (7.0:1) | `#A8A9F8` (7.4:1) | small action text |
| `ok-ink` | `#0B6E62` (6.2:1) | `#63D9C9` (8.6:1) | success/verified **text** |
| `ok` | `#18A999` | `#2BBFAE` | success fills only |
| `danger-ink` / `danger` | `#B4232A` / `#D93036` | `#FF9BA0` / `#F0656B` | errors, blocks |
| `warn-ink` | `#925E00` (5.2:1) | `#E8B45C` | unverified / needs action |

Rules: indigo = actions, teal = success/verified, neutrals = everything else.
No fourth hue. Decorative fills may use the saturated value; **text always uses
the `-ink` variant.**

## Shadow recipes (the neumorphic half that must not be inverted naively)

- Light: light shadow `rgba(255,255,255,.92)` top-left, dark
  `rgba(160,178,205,.44)` bottom-right.
- Dark: light shadow `rgba(40,50,68,.80)` top-left, near-black
  `rgba(0,0,0,.62)` bottom-right; sunk wells invert to inner black + inner
  slate. Dark neumorphism is a *different recipe*, not inverted hexes.

Elevation: `raise-1` (controls), `raise-2` (cards, tab bar), `raise-3`
(modals, device frame); `sunk-1` (inputs, chips), `sunk-2` (scanner well).

## Radius / space / type

- Radius: `10 / 14 / 18 / 24 / 30 / pill`. Nothing else.
- Space: `4 / 8 / 12 / 16 / 24 / 32`.
- Type: Manrope (display + all numerics, `tnum` tabular) / Inter (body).
  Scale `12 / 14 / 16 / 20 / 26 / 38`. Balances and amounts are always
  tabular; never proportional figures for money.
- Ship **two** font files. The old build shipped six and used two.

## Components

- **Button** — one component with real states: default, hover, pressed
  (sunk), **disabled** (sunk + 45% opacity, non-interactive), focus ring
  2.5px. The old `NeuButton` had no `enabled` parameter, which is how the
  double-charge bug existed.
- **Switch** — sunk track, raised thumb, `role="switch"`, focus ring on track.
- **Tab bar** — intrinsic height, `padding-bottom: safe-area`, labels always
  rendered and never clipped (the old fixed 74dp bar clipped them on device).
  Selected = accent-soft fill + sunk shadow.
- **Scanner well** — keep the four-level carved construction (raised bezel →
  sunk → raised lip → deep sunk well). It is the best screen in the product.
- **Bank cards** — vector gradients only (`g0/g1/g2`). No photographic card
  renders; photo + flat vector in one list reads as two products.
- **Icons** — one stroke-based SVG set, tintable. No emoji, no raster emblems.

## Motion

- Ambient loops only where they mean something (scan beam while decoding).
- `prefers-reduced-motion: reduce` disables all infinite loops and collapses
  transitions. The old build ran three permanent loops with no check.

## Honesty contract

The UI may render a status only if the system can produce it.

| State shown | System backing it |
|---|---|
| "Camera live — decoding frames" | camera stream + decoder running |
| "Decoder ready / idle" | no stream, decoder available |
| "Camera unavailable (…reason)" | getUserMedia rejection, reason surfaced |
| "Trusted merchant" | on the parent-approved list |
| "Unverified — guardian approval required" | not on any list; payment gated |
| "Blocked merchant" | on block list; payment impossible, no override |
| "Verifying…" | authenticator call in flight; pay button disabled |
| "Verified with device biometrics" | WebAuthn / BiometricPrompt success |

Never show "Verified", "Scanner active" or "Verifying fingerprint" without the
corresponding capability actually running. The previous build showed all three
while running none.

## Compose port map

| Web token | Compose target |
|---|---|
| colour tokens | `ui/theme/Color.kt` — replace aliases (`NeuAccentOrange`, `CardGradientCoral*`) with real tokens or delete |
| shadow recipes | `ui/neumorphic/NeumorphicModifiers.kt` — expose `raise1/2/3`, `sunk1/2` modifiers; delete per-call radii |
| radius/space/type | new `ui/theme/Tokens.kt` |
| button states | `NeuButton` gains `enabled`, `role`, `contentDescription` |
| tab bar | replace hand-rolled `Row` with intrinsic height + `WindowInsets.navigationBars` |
| dark recipe | `Theme.kt` adds `darkColorScheme` + dark shadow tokens |
| honesty contract | `PaymentViewModel` exposes real scan/biometric/verification state; delete `delay(900)` biometrics |
