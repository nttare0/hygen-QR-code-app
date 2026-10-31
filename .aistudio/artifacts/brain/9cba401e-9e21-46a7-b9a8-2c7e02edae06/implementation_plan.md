# Calm, Friendly, Confident Banking Experience

A complete visual and functional makeover transforming the app into a calm, warm, modern everyday banking experience built around clarity, generous whitespace, deep teal and mint color tokens, interactive spending insights, and effortless 3-step money transfers—with zero duplicate navigation controls.

## User Review & Critical Decisions

> [!IMPORTANT]
> The decisions below incorporate your confirmed answers from our design check-in alongside the full visual and interaction requirements from your brief.

- **Full Aesthetic & Functional Makeover**: Transition completely from the previous Soft UI QR scanner layout to the new **Calm, Friendly, Confident** personal banking experience (`Home`, `Cards`, `Activity`, `Profile`) with a single clean bottom navigation bar and **zero duplicate top/shortcut navigation buttons**.
- **Primary Currency**: Display all balances, card limits, spending breakdowns, and transfers in **US Dollars (`$ USD`)** formatted via locale-aware `NumberFormat` with tabular numerals (`tnum`).
- **In-Memory Session State**: Maintain state cleanly in memory (`ViewModel` + `StateFlow`) pre-populated with realistic sample contacts, cards, and categorized transactions—updating balances, ring charts, and activity history live as you send, request, or top up money, and resetting cleanly on reload.
- **Reduced Motion & System Theme Awareness**: Automatically detect system dark/light preference on launch while providing an instant manual **Dark mode** switch in `Profile` (and a theme toggle icon in `Profile`), and respect system animation scale / reduced-motion preferences across count-up, spring press, and celebration animations.

---

## 1. Overview & Core Concept

- **What It Does**: An intuitive everyday mobile banking app where users check their live balance with privacy masking, understand monthly spending through an interactive category ring chart, send money in three calm steps via a bottom sheet, manage virtual cards with tap-to-flip details and spending limits, search date-grouped transactions, and customize security and theme preferences.
- **Target Audience / Persona**: Everyday consumers looking for a stress-free, jargon-free financial companion that feels trustworthy, warm, and immediately legible at a glance.
- **Key Value**:Eliminates visual clutter and duplicate buttons in favor of one clear purpose per view, big tabular numbers (`40sp`), warm plain-English microcopy (e.g., *"Money sent to Amina 🎉"*), and responsive tactile feedback.

---

## 2. User Experience & Visual Design

### Key User Flows
1. **First Load & Home Overview**:
   - Displays a brief **800ms shimmer skeleton loader** (never harsh spinners) on initial load before revealing content smoothly.
   - Shows a warm sentence-case greeting (*"Good morning, Jordan"*), a prominent **Balance Card** (`40sp` bold tabular numerals) with an **Eye hide/show privacy toggle** (`••••••`), and **4 Quick Action pills** (`Send`, `Request`, `Pay`, `Top up`)—with no redundant tab jump buttons.
   - Features an animated **Monthly Spending Ring Chart** broken down by category (`Groceries`, `Dining`, `Bills`, `Transport`). Tapping any ring segment highlights that category and filters the **Recent Transactions** list directly below; tapping again clears the filter.
2. **Send Money (3-Step Bottom Sheet)**:
   - **Step 1 — Choose recipient**: Horizontal row of friendly custom-drawn vector/initials avatars (`Amina`, `Liam`, `Sofia`, ` Marcus`, ` Maya`) plus a clean search field (2 elements max).
   - **Step 2 — Enter amount**: Large tabular `$0.00` display, live available balance subtitle, optional short note pill, and a **custom rounded numeric keypad** (`1–9`, `.`, `0`, backspace). Validates inline in warm plain language if amount is `$0` or exceeds available balance (*"That's a bit more than your $4,820.50 balance."*).
   - **Step 3 — Review & confirm**: Clean summary card showing recipient, amount, and zero-fee instant delivery note, paired with a primary `Send money` button.
   - **Success Celebration**: Smoothly draws an animated vector checkmark inside a mint circle with a light Canvas **confetti burst**, displaying *"Money sent to Amina 🎉"* and a `Done` button that updates the Home balance and prepends the new transaction.
   - *(Quick actions `Request`, `Pay`, and `Top up` also open streamlined, working sheets so every button on Home performs a real balance/activity action without dead ends.)*
3. **Cards (Swipeable & Flip-Enabled Virtual Cards)**:
   - Horizontal pager of virtual cards (`Everyday Debit`, `Travel Vault`) rendered with vector geometry and soft teal/slate styling.
   - **Tap to Flip**: Smooth 3D Y-axis card flip (`280ms`) revealing card number, expiry, CVV, and one-tap copy feedback on the back.
   - **Card Controls**: Single **Freeze card** toggle (dims/frosts the card with a calm status badge when frozen) and an interactive **Monthly spending limit slider** (`$500` – `$10,000`) updating live in tabular USD.
4. **Activity (Searchable, Date-Grouped Ledger)**:
   - Search bar at top + fully rounded filter chips (`All`, `In`, `Out`, `Bills`) with sentence-case labels.
   - Transactions grouped cleanly under date headers (`Today`, `Yesterday`, `Earlier this month`), color-coding money in with mint (`#34D399` / accessible dark-mint text) and expenses with subtle amber (`#F59E0B`) category badges.
   - **Friendly Empty State**: Custom vector illustration + *"No matching activity yet"* + a clear `Clear filters` button whenever a search or filter returns zero items.
5. **Profile (Theme & Security)**:
   - User profile header card, **Appearance** control (`Auto / Light / Dark` + quick Dark Mode switch), **Security** switches (`Face / fingerprint unlock`, `Transaction alerts`, `Hide balance on launch`), and a **Reduced motion** accessibility toggle.

### Visual Identity & Theme
- **Aesthetic Direction**: *"Calm, friendly, confident."* Generous breathing room on an `8dp` spatial grid, `16dp–24dp` card radii, fully rounded pill buttons/chips (`CircleShape` / `50%`), and soft ambient teal-tinted elevation (`rgba(15, 118, 110, 0.08)`).
- **Color Palette & 60 / 30 / 10 Balance**:
  - **Light Mode**: Background `#F7F9F8` (60% neutral canvas) • Surface `#FFFFFF` • Text `#0F1A17` (16.8:1 contrast) • Muted Text `#4B5E58` (>6.2:1 contrast) • Primary Deep Teal `#0F766E` (30% structure/brand) • Accent Mint `#34D399` (10% positive actions & money-in badges) • Warning/Expense `#F59E0B` • Error `#EF4444` (strictly for validation/errors).
  - **Dark Mode**: Background `#0B1412` • Surface `#13201D` • Text `#E6F0ED` (14.9:1 contrast) • Muted Text `#9BB3AC` (>6.5:1 contrast) • Primary `#14B8A6` / `#0F766E` container • Accent Mint `#34D399`.
- **Typography & Strict Scale**:
  - **Plus Jakarta Sans** for headings and all numeric/currency figures (`fontFeatureSettings = "tnum"`).
  - **Inter** for body copy, inputs, chips, and navigation labels.
  - **Strict Type Scale**: `12sp` (captions/timestamps) • `14sp` (minimum body/secondary UI) • `16sp` (primary body & list titles) • `20sp` (section headers) • `28sp` (sheet amounts & screen titles) • `40sp` (`FontWeight.Bold` tabular balance).
  - **Sentence Case Rule**: Sentence case enforced across every heading, chip, badge, and button—zero `ALL CAPS`.

### Interactive Feedback & Motion
- **Balance Count-Up**: Animates smoothly from `$0.00` (or previous balance) to the current USD balance over `300ms` (`FastOutSlowInEasing`), skipped automatically if reduced motion is enabled.
- **Spring Press (`0.97f`)**: Interactive buttons and quick-action pills scale to `0.97f` on press using a crisp spring spec, with `>= 48dp` touch targets and visible focus outlines (`2dp` `#0F766E` / `#34D399`) for keyboard/D-pad navigation.
- **No Duplicate Navigation**: Only the single bottom thumb-zone tab bar (`Home`, `Cards`, `Activity`, `Profile`) switches top-level screens—removing all redundant top-bar back/scanner/shortcut pills from previous iterations.

---

## 3. Key Product Decisions & Trade-Offs

- **Decision 1: Single Thumb-Zone Navigation & Zero Duplicate Buttons**
  - *Chosen Approach*: Consolidate all screen switching exclusively into the 4-tab bottom bar (`Home`, `Cards`, `Activity`, `Profile`), and dedicate Home's 4 action buttons strictly to financial actions (`Send`, `Request`, `Pay`, `Top up`).
  - *Why*: Directly resolves the duplicate-button clutter from the earlier build where top shortcut pills, header buttons, and bottom tabs competed for attention.
  - *Alternatives Considered*: Keeping secondary header navigation pills on sub-screens—rejected because it duplicates the persistent bottom bar and crowds the layout.
- **Decision 2: In-Memory Reactive Store (`BankingViewModel`)**
  - *Chosen Approach*: Hold balances, virtual cards, recipients, theme preference, and transaction history in a clean Kotlin `StateFlow` store seeded with realistic USD financial records, while trimming unused heavy camera/database dependencies from active runtime code.
  - *Why*: Matches your explicit preference for fast, predictable client-side session state that resets cleanly on reload with zero sign-in friction.
- **Decision 3: Pure Vector & Canvas Visuals (Zero External Images)**
  - *Chosen Approach*: Render all rounded Lucide-style icons, recipient avatars, spending ring segments, virtual card art, empty-state illustrations, checkmark draw paths, and confetti particles using Jetpack Compose `Canvas` and rounded `ImageVector` paths.
  - *Why*: Guarantees crisp resolution at every screen density, instantaneous rendering, seamless light/dark theming, and strict adherence to the no-external-images quality bar.

---

## 4. Technical Architecture & Data Strategy *(Technical Reference)*

### Architecture & Component Diagram
```
┌───────────────────────────────────────────────────────────────────────────┐
│                         BankingViewModel (In-Memory)                      │
│  • UiState: isInitialLoading (800ms skeleton), themeMode, reduceMotion    │
│  • FinancialState: balanceUsd, isBalanceHidden, selectedRingCategory      │
│  • Collections: transactions, virtualCards, recipients                    │
│  • SheetState: SendMoneyFlow (Step 1 Recipient → Step 2 Keypad → Step 3)  │
└─────────────────────────────────────┬─────────────────────────────────────┘
                                      │ StateFlow
                                      ▼
┌───────────────────────────────────────────────────────────────────────────┐
│                   CalmBankTheme (Light #F7F9F8 / Dark #0B1412)            │
│          Plus Jakarta Sans (Headings & Tabular Nums) + Inter (Body)       │
└─────────────────────────────────────┬─────────────────────────────────────┘
                                      │
        ┌─────────────────────────────┼─────────────────────────────┐
        ▼                             ▼                             ▼
┌───────────────────┐       ┌───────────────────┐       ┌───────────────────┐
│   Screen Router   │       │  Modal Sheets &   │       │  Thumb-Zone Tab   │
│  (AnimatedContent)│       │   Celebrations    │       │  Bar (4 Tabs)     │
├───────────────────┤       ├───────────────────┤       ├───────────────────┤
│ 1. HomeScreen     │       │ • SendMoneySheet  │       │ • Home            │
│  - Skeleton 800ms │       │   Step 1: People  │       │ • Cards           │
│  - Balance + Eye  │       │   Step 2: Keypad  │       │ • Activity        │
│  - 4 Quick Pills  │       │   Step 3: Review  │       │ • Profile         │
│  - Spending Ring  │       │ • Checkmark +     │       │ (Single nav bar,  │
│  - Recent List    │       │   Confetti Canvas │       │  no duplicates)   │
│ 2. CardsScreen    │       │ • TopUp / Request │       └───────────────────┘
│  - Flip Card Pager│       │   & Bill Pay      │
│  - Freeze & Slider│       │   Quick Sheets    │
│ 3. ActivityScreen │       └───────────────────┘
│  - Search & Chips │
│  - Date Grouping  │
│ 4. ProfileScreen  │
│  - Theme & Locks  │
└───────────────────┘
```

### Data Model & State
- **`BankTransaction`**: `id`, `title`, `counterparty`, `amountUsd`, `type` (`IN`, `OUT`, `BILL`), `category` (`Groceries`, `Dining`, `Bills`, `Transport`, `Transfer`, `Income`), `timestamp`, and `note`.
- **`VirtualCard`**: `id`, `label`, `holderName`, `cardNumber`, `expiry`, `cvv`, `isFrozen`, `monthlyLimitUsd`, `spentThisMonthUsd`, and `accentVariant`.
- **`Recipient`**: `id`, `name`, `handle`, `initials`, and `avatarTint`.
- **`SendMoneyDraft`**: `step` (`RECIPIENT`, `AMOUNT`, `REVIEW`, `SUCCESS`), `recipientSearch`, `selectedRecipient`, `rawAmountInput`, `note`, and `validationError`.

### Interactive Component & State Mapping
- **Home Balance & Ring Filtering**: Toggling the eye icon flips `isBalanceHidden` with animated crossfade; tapping a segment on `SpendingRingChart` toggles `selectedRingCategory`, filtering Home's recent transactions in place with a clear *"Showing Dining • Reset"* pill.
- **Send Money Validation & Keypad**: Custom keypad appends digits/decimal up to 2 fractional places; pressing `Continue` checks `amount > 0` and `amount <= balanceUsd`, surfacing inline `#EF4444` plain-language guidance if invalid or advancing to `REVIEW`. Confirming deducts `amountUsd` from `balanceUsd`, inserts a new `Today` transaction, and triggers the `SUCCESS` checkmark + confetti animation.
- **Virtual Card Flip & Controls**: Tapping the card animates `rotationY` (`0f ↔ 180f`) with camera distance adjustment; toggling `Freeze card` updates `isFrozen` and disables card spend actions; dragging the limit slider updates `monthlyLimitUsd` in `$100` increments.
- **Activity Search & Date Grouping**: Combining `searchQuery` and `ActivityFilter` (`All`, `In`, `Out`, `Bills`) dynamically computes grouped sections (`Today`, `Yesterday`, `Earlier this month`), rendering the custom vector empty state with a working `Reset filters` action when empty.
