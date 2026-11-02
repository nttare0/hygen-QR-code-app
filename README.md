# QuickPay QR

A tactile, neumorphic QR-payment wallet concept for a child/parent banking
scenario: scan a merchant QR, verify the merchant, gate the payment on
biometrics, enforce family spending caps, and keep an on-device ledger.

This repository contains two things:

| Path | What it is | Status |
|---|---|---|
| `app/` | The original Android / Jetpack Compose app | Prototype. See *Known problems* |
| `web/` | **Runnable redesign** of the product as a mobile web app | Live, tested |
| `DESIGN.md` | The design system both implementations share | Source of truth |

## Run the redesign preview

```bash
python3 -m http.server 8080 --bind 0.0.0.0 --directory web
# open http://localhost:8080
```

No build step. In this sandbox the preview is served live on port 8080.

### Behavioural smoke test

```bash
npm i jsdom          # dev-only
python3 -m http.server 8080 --bind 0.0.0.0 --directory web &
node web/test/smoke.js
```

It asserts the product contract, not just rendering: trusted merchants are
payable, unverified merchants fail closed behind a guardian override, a failed
guardrail cannot be overridden, block-listed merchants are never payable, and
a completed payment deducts an integer balance, writes history and persists.

## What is real in `web/` (and was faked in `app/`)

- **QR decoding** — `jsQR` over live camera frames, or over a generated
  on-screen test code that is rasterised and fed through the same decoder.
  `app/` had no decoder at all: its "Scan Code" button emitted a hard-coded
  `School Cafeteria | 15.00` payload.
- **Biometric gate** — WebAuthn platform authenticator where the device has
  one; an explicitly-labelled demo passcode otherwise. `app/` simulated it
  with `delay(900)` while showing "Verifying fingerprint…".
- **Fail-closed safety** — unknown merchants are *unverified* and require an
  explicit guardian override; block-listed merchants are refused with no
  override. `app/` returned `isSafe = true, "Verified Merchant"` whenever its
  API call failed, and never let the verdict block anything.
- **Integer money** — all balances, amounts and limits are halalas.
  `app/` used `Double` for currency.
- **Cumulative daily allowance** — tracked from history, enforced per payment.
  `app/` advertised a "daily spending allowance" it never implemented.
- **Single-flight payments** — the pay button disables while verifying.
  `app/`'s `NeuButton` had no `enabled` parameter, allowing double charges.
- **Persistence** — wallet, caps, lists and history survive reload.
  `app/` reset its wallet to seeded demo data on every process death while
  keeping Room history, so the ledger referenced cards that no longer existed.

## Known problems in `app/` (Android)

Full review is in the session notes; headline items, in priority order:

1. No QR decoding (no ML Kit / ZXing / `ImageAnalysis`); hard-coded scan payload.
2. Biometrics, NFC toggles and the Gemini verdict are decorative; safety fails open.
3. Double-charge possible; currency is `Double`; nothing persists but history.
4. No Gradle wrapper (`gradlew`, `gradle-wrapper.jar` absent) — a fresh clone
   cannot build; no README/CI previously existed.
5. Compose BOM `2024.09.00` against AGP 9.1 / Kotlin 2.2 / SDK 36; release build
   has R8 disabled; `namespace = "com.example"`.
6. 0 `stringResource` calls vs 95 hard-coded strings; no dark theme; muted text
   and success-teal text below WCAG AA; bottom nav clips its labels on device.
7. Dead weight: 4 unused TTFs, ~1.7 MB of JPGs in `res/drawable`, and 19
   committed files under `.aistudio/` + `public/` from a web-project template.

Building `app/` requires the Android SDK and Google Maven, neither of which is
reachable from this sandbox; the Compose port of `DESIGN.md` is the next step.

## Repository notes

- `public/assets/aistudio/*.png` are screenshots of an **older build** (coral
  accents, Playfair serifs, duplicate top navigation, demo chips that no longer
  exist). They misrepresent the current code and should be regenerated or
  removed; they are kept here only as design history.
- `.aistudio/artifacts/**/implementation_plan.md` describes a different product
  (USD, Home/Cards/Activity/Profile, dark mode). It is stale; `DESIGN.md`
  supersedes it for the redesign.
- Vendored browser libraries: `web/vendor/jsQR.js` (MIT) and
  `web/vendor/qrcode.js` (MIT). Fonts `manrope.ttf` / `inter.ttf` are copied
  from `app/src/main/res/font/` so the preview and the app share faces.
