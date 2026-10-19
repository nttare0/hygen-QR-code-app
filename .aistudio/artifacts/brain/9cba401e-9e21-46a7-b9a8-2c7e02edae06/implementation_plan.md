# QuickPay QR - Tactile Neumorphic QR Payments

A minimal, ultra-accessible QR payment app engineered with soft, tactile Neumorphic UI design principles. Designed for effortless simplicity so a 10-year-old child can use it safely, while integrating linked bank accounts, biometric authentication, secure transaction history, and ultra-low-latency Gemini 3.1 Flash Lite safety checks.

---

## User Review & Critical Decisions

> [!IMPORTANT]
> The following product decisions were established from your preferences and reference designs:

- **Brand Name**: **QuickPay QR** — clean, accessible, and friendly.
- **Home Screen Experience**: **Quick Scanner with Instant Payment Confirmation** — opens straight into a neumorphic viewfinder with instant balance visibility and one-touch biometric checkout.
- **AI Integration**: **Gemini 3.1 Flash Lite** (`gemini-3.1-flash-lite-preview`) via direct REST API for low-latency merchant safety verification and smart receipt categorization.
- **Design Aesthetic**: **Soft Neumorphism & Tactile 3D Depth** — extruded soft buttons, inset camera well, floating credit cards, and circular biometric scanner using dual directional light/shadow surfaces (`#EEF2F7`, `#FFFFFF` light elevation, `#CAD4E2` dark drop shadows).
- **Typography Pairing**:
  - *Serif Display* (`Playfair Display`): Currency balance figures and hero headings.
  - *Geometric Sans*: Navigation tabs, labels, actions, and merchant details.
  - *Tabular Monospace*: Card numbers and transaction IDs.

---

## 1. Overview & Core Concept

QuickPay QR strips away complex banking bureaucracy to offer an intuitive, 3-step payment flow:
1. **Scan**: Point the camera at any merchant QR code (or tap sample QR presets for easy testing).
2. **Review**: See merchant name, amount, safety rating generated in milliseconds by Gemini, and linked bank account.
3. **Authenticate**: Tap the soft neumorphic biometric fingerprint sensor to execute the secure transaction with haptic feedback.

---

## 2. User Experience & Visual Design

### Tactile Neumorphic Design System

```
       Light Source (Top-Left 135°)
               \
                v
       ┌───────────────────────────────┐
       │   Soft Highlight (#FFFFFF)    │
       │   ┌───────────────────────┐   │
       │   │  Base Pill (#EEF2F7)  │   │
       │   └───────────────────────┘   │
       │   Soft Shadow (#CAD4E2)       │
       └───────────────────────────────┘
```

- **Base Canvas Color**: `#EEF2F7` (soft, tactile off-white).
- **Extruded (Raised) Components**: Cards, action pills, fingerprint sensor, and bottom navigation items with dual box shadows (`offset(-4.dp, -4.dp)` white highlight + `offset(4.dp, 4.dp)` ambient drop shadow).
- **Debossed (Inset) Components**: Camera viewfinder frame, text input fields, and search bars styled as sculpted impressions in the surface.
- **Accent Palette**:
  - Primary Action Blue: `#3B82F6` (vibrant, modern)
  - Security Green: `#10B981` (verified / success)
  - Warning Orange: `#F59E0B` (safety alerts)
  - Card Gradients: Violet-Blue gradient & Coral-Pink gradient for 3D payment cards.

### Core Screens & Flows

1. **Quick Scan & Pay (Default Home)**:
   - **Header**: Top balance indicator (`SAR 804,660.00` in Playfair Display serif) with bank switch button and status pill.
   - **Central Viewfinder**: Sculpted neumorphic scanner with animated laser scanline, flashlight toggle, gallery picker, and quick test QR buttons (e.g., "School Cafeteria - SAR 15.00", "Bookstore - SAR 32.50", "Toy Shop - SAR 45.00").
   - **Instant Payment Sheet**: When a QR code is detected, a soft raised sheet slides up with merchant details, amount breakdown, Gemini safety badge ("✓ Verified Safe Merchant"), and the large circular tactile fingerprint scanner.
2. **Tactile Wallet & Bank Accounts**:
   - 3D stacked tactile cards (Visa Debit, Credit cards) with realistic chip, card holder name, and masked number.
   - Linked bank balance with quick top-up and account management.
3. **Transaction History**:
   - Filter chips ("Today", "All") matching the uploaded design reference.
   - Clean, sculpted transaction cards showing merchant icon, timestamp, category tag, and formatted amount.
   - Expandable Gemini smart receipt analysis for spending breakdown.
4. **Biometric Security & Settings**:
   - Biometric toggle (Face ID / Fingerprint simulation & Android BiometricPrompt).
   - Spending limit protection for child-safe usage.

---

## 3. Key Product Decisions & Trade-Offs

- **Architecture & Local Persistence**:
  - *Decision*: Room Database with Kotlin Coroutines & StateFlow.
  - *Rationale*: Guarantees instant sub-millisecond launches, works seamlessly offline, and securely keeps transaction history and bank balances on-device without requiring third-party cloud logins.
- **Gemini 3.1 Flash Lite REST API**:
  - *Decision*: Direct low-latency REST calls using `gemini-3.1-flash-lite-preview`.
  - *Rationale*: Minimal latency (<500ms) for real-time merchant safety verification and itemized breakdown, keeping the payment experience snappy and responsive.
- **Simulated & Real Biometrics Support**:
  - *Decision*: Dual-mode biometric support (system `BiometricPrompt` when available on physical devices, plus an interactive tactile neumorphic fingerprint sensor button with realistic press animations for browser streaming emulator testing).
  - *Rationale*: Allows users and evaluators to test the biometric checkout experience instantly in the streaming browser emulator while keeping production-ready Android security hooks.

---

## 4. Technical Architecture & Data Strategy

### Component & System Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                       QuickPay QR UI                        │
├─────────────────────────────────────────────────────────────┤
│  Top Bar & Balance   │  Neumorphic Scanner  │ Bottom Tabs   │
│  (Playfair Display)  │  (Camera + Presets)  │ (Scan/Wallet/ │
│                      │                      │  History)     │
└──────────────┬──────────────────┬───────────────────┬───────┘
               │                  │                   │
               ▼                  ▼                   ▼
┌─────────────────────────────────────────────────────────────┐
│                    PaymentViewModel                         │
│  - cameraState / scanState      - selectedPaymentMethod     │
│  - activePaymentSheet           - biometricAuthState        │
├──────────────────────────────┬──────────────────────────────┤
│  Gemini 3.1 Flash Lite Client│  Room Database & Repository  │
│  - Low-latency Safety Check  │  - BankAccountDao            │
│  - Receipt Breakdown Parser  │  - TransactionHistoryDao     │
└──────────────────────────────┴──────────────────────────────┘
```

### Data Entities
- **BankAccount**: `id`, `bankName`, `accountNumberMasked`, `balance`, `currency`, `cardColor`.
- **TransactionRecord**: `id`, `merchantName`, `merchantCategory`, `amount`, `currency`, `timestamp`, `status`, `qrRawData`, `aiSafetySummary`, `isVerified`.

---

## Next Steps

Upon your approval, we will:
1. Configure dependencies and download custom fonts (`Playfair Display`).
2. Implement custom Neumorphic modifiers (`neumorphicElevation`, `neumorphicInset`, soft lighting effects).
3. Build the Room database layer for bank accounts and transaction history.
4. Integrate Gemini 3.1 Flash Lite service for low-latency merchant safety analysis.
5. Build the Quick Scanner Home, 3D Wallet, Biometric Auth Modal, and History screens.
6. Verify and compile the Android build.
