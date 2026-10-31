package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.gemini.GeminiSafetyService
import com.example.data.gemini.MerchantSafetyResult
import com.example.data.local.QuickPayDatabase
import com.example.data.model.BankAccount
import com.example.data.model.TransactionRecord
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.min
import kotlin.math.roundToInt

data class ActiveScanPayload(
    val merchantName: String,
    val category: String,
    val amount: Double,
    val rawPayload: String
)

data class PaymentSuccessState(
    val merchantName: String,
    val amount: Double,
    val currency: String,
    val transactionId: Long
)

class PaymentViewModel(application: Application) : AndroidViewModel(application) {
    private val database = QuickPayDatabase.getDatabase(application)
    private val transactionDao = database.transactionDao()
    private val geminiSafetyService = GeminiSafetyService()

    // Linked Bank Accounts with readable metadata and per-card transaction limits
    private val _bankAccounts = MutableStateFlow(
        listOf(
            BankAccount(
                id = "acc_1",
                bankName = "TechnoBank Saudi",
                cardType = "Visa Debit",
                last4 = "8091",
                balance = 804660.00,
                currency = "SAR",
                isPrimary = true,
                cardGradientType = 0,
                isFrozen = false,
                perTransactionLimit = 2500.0,
                expiryDate = "09/29"
            ),
            BankAccount(
                id = "acc_2",
                bankName = "Reserve Digital",
                cardType = "Credit",
                last4 = "4719",
                balance = 25400.00,
                currency = "SAR",
                isPrimary = false,
                cardGradientType = 2,
                isFrozen = false,
                perTransactionLimit = 1500.0,
                expiryDate = "11/28"
            ),
            BankAccount(
                id = "acc_3",
                bankName = "Youth Savings",
                cardType = "Debit",
                last4 = "0173",
                balance = 6820.70,
                currency = "SAR",
                isPrimary = false,
                cardGradientType = 1,
                isFrozen = false,
                perTransactionLimit = 250.0,
                expiryDate = "04/30"
            )
        )
    )
    val bankAccounts: StateFlow<List<BankAccount>> = _bankAccounts.asStateFlow()

    private val _defaultAccountId = MutableStateFlow("acc_1")
    val defaultAccountId: StateFlow<String> = _defaultAccountId.asStateFlow()

    private val _selectedAccountId = MutableStateFlow("acc_1")
    val selectedAccountId: StateFlow<String> = _selectedAccountId.asStateFlow()

    // Transactions from Room
    val allTransactions: StateFlow<List<TransactionRecord>> = transactionDao.getAllTransactions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Pending Scan Sheet
    private val _activeScan = MutableStateFlow<ActiveScanPayload?>(null)
    val activeScan: StateFlow<ActiveScanPayload?> = _activeScan.asStateFlow()

    private val _safetyResult = MutableStateFlow<MerchantSafetyResult?>(null)
    val safetyResult: StateFlow<MerchantSafetyResult?> = _safetyResult.asStateFlow()

    private val _isSafetyLoading = MutableStateFlow(false)
    val isSafetyLoading: StateFlow<Boolean> = _isSafetyLoading.asStateFlow()

    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()

    private val _paymentSuccess = MutableStateFlow<PaymentSuccessState?>(null)
    val paymentSuccess: StateFlow<PaymentSuccessState?> = _paymentSuccess.asStateFlow()

    private val _paymentBlockReason = MutableStateFlow<String?>(null)
    val paymentBlockReason: StateFlow<String?> = _paymentBlockReason.asStateFlow()

    // Vault Global Transaction Spending Limit (Max SAR per single transaction)
    private val _childSpendingLimit = MutableStateFlow(500.0)
    val childSpendingLimit: StateFlow<Double> = _childSpendingLimit.asStateFlow()

    // NFC, Biometric & Tactile Haptics Settings
    private val _isNfcEnabled = MutableStateFlow(true)
    val isNfcEnabled: StateFlow<Boolean> = _isNfcEnabled.asStateFlow()

    private val _isBiometricsRequired = MutableStateFlow(true)
    val isBiometricsRequired: StateFlow<Boolean> = _isBiometricsRequired.asStateFlow()

    private val _isHapticsEnabled = MutableStateFlow(true)
    val isHapticsEnabled: StateFlow<Boolean> = _isHapticsEnabled.asStateFlow()

    fun selectAccount(accountId: String) {
        if (_bankAccounts.value.any { it.id == accountId }) {
            _selectedAccountId.value = accountId
            _paymentBlockReason.value = null
        }
    }

    /**
     * Marks [accountId] as the Default Payment Card (isPrimary = true) and selects it for active QR payments.
     */
    fun setDefaultAccount(accountId: String) {
        if (_bankAccounts.value.none { it.id == accountId }) return
        _defaultAccountId.value = accountId
        _selectedAccountId.value = accountId
        _bankAccounts.value = _bankAccounts.value.map { acc ->
            acc.copy(
                isPrimary = acc.id == accountId,
                cardGradientType = if (acc.id == accountId) 0 else if (acc.cardGradientType == 0) 2 else acc.cardGradientType
            )
        }
        _paymentBlockReason.value = null
    }

    /**
     * Links a new bank card into the user's tactile wallet.
     */
    fun linkBankCard(
        bankName: String,
        cardType: String,
        last4: String,
        initialBalance: Double,
        perTransactionLimit: Double,
        expiryDate: String = "12/30",
        makeDefault: Boolean = false
    ): BankAccount {
        val cleanBank = bankName.trim().ifEmpty { "National Digital Bank" }
        val cleanType = cardType.trim().ifEmpty { "Visa Debit" }
        val cleanLast4 = last4.filter { it.isDigit() }.takeLast(4).padStart(4, '4')
        val cleanBalance = initialBalance.coerceAtLeast(0.0)
        val cleanLimit = perTransactionLimit.coerceIn(25.0, 20000.0)
        val newId = "acc_${System.currentTimeMillis()}"

        val newCard = BankAccount(
            id = newId,
            bankName = cleanBank,
            cardType = cleanType,
            last4 = cleanLast4,
            balance = cleanBalance,
            currency = "SAR",
            isPrimary = makeDefault,
            cardGradientType = if (makeDefault) 0 else ((_bankAccounts.value.size % 2) + 1),
            isFrozen = false,
            perTransactionLimit = cleanLimit,
            expiryDate = expiryDate.ifBlank { "12/30" }
        )

        val updatedList = if (makeDefault) {
            _bankAccounts.value.map { acc ->
                acc.copy(
                    isPrimary = false,
                    cardGradientType = if (acc.cardGradientType == 0) 2 else acc.cardGradientType
                )
            } + newCard
        } else {
            _bankAccounts.value + newCard
        }

        _bankAccounts.value = updatedList
        _selectedAccountId.value = newId
        if (makeDefault) {
            _defaultAccountId.value = newId
        }
        return newCard
    }

    /**
     * Freezes or unfreezes a specific card to pause/resume payments.
     */
    fun toggleCardFreeze(accountId: String, frozen: Boolean) {
        _bankAccounts.value = _bankAccounts.value.map { acc ->
            if (acc.id == accountId) acc.copy(isFrozen = frozen) else acc
        }
        _paymentBlockReason.value = null
    }

    /**
     * Updates the single-transaction limit (SAR) for a specific card, snapped to clean 25 SAR steps.
     */
    fun updateCardTransactionLimit(accountId: String, newLimit: Double) {
        val snapped = ((newLimit / 25.0).roundToInt() * 25.0).coerceIn(25.0, 10000.0)
        _bankAccounts.value = _bankAccounts.value.map { acc ->
            if (acc.id == accountId) acc.copy(perTransactionLimit = snapped) else acc
        }
        _paymentBlockReason.value = null
    }

    /**
     * Unlinks/removes a bank card as long as at least one linked card remains.
     */
    fun removeBankCard(accountId: String) {
        val current = _bankAccounts.value
        if (current.size <= 1) return
        val remaining = current.filterNot { it.id == accountId }
        val wasDefault = _defaultAccountId.value == accountId
        val nextDefaultId = if (wasDefault) remaining.first().id else _defaultAccountId.value

        _bankAccounts.value = remaining.map { acc ->
            val isNowDefault = acc.id == nextDefaultId
            acc.copy(
                isPrimary = isNowDefault,
                cardGradientType = if (isNowDefault) 0 else acc.cardGradientType
            )
        }
        _defaultAccountId.value = nextDefaultId
        if (_selectedAccountId.value == accountId) {
            _selectedAccountId.value = nextDefaultId
        }
    }

    /**
     * Updates the global Vault transaction cap (SAR).
     */
    fun updateVaultSpendingLimit(newLimit: Double) {
        val snapped = ((newLimit / 25.0).roundToInt() * 25.0).coerceIn(25.0, 10000.0)
        _childSpendingLimit.value = snapped
        _paymentBlockReason.value = null
    }

    fun toggleNfc(enabled: Boolean) {
        _isNfcEnabled.value = enabled
    }

    fun toggleBiometrics(required: Boolean) {
        _isBiometricsRequired.value = required
    }

    fun toggleHaptics(enabled: Boolean) {
        _isHapticsEnabled.value = enabled
    }

    /**
     * Computes any active guardrail restriction for a payment amount on [account].
     */
    fun evaluatePaymentGuardrail(account: BankAccount, amount: Double): String? {
        if (account.isFrozen) {
            return "${account.cardType} (•••• ${account.last4}) is currently frozen. Unfreeze it in Wallet or switch cards."
        }
        if (amount > account.perTransactionLimit) {
            return "Exceeds card limit of SAR ${"%,.0f".format(account.perTransactionLimit)} per transaction."
        }
        if (amount > _childSpendingLimit.value) {
            return "Exceeds Vault spending cap of SAR ${"%,.0f".format(_childSpendingLimit.value)} per transaction."
        }
        if (amount > account.balance) {
            return "Insufficient balance on ${account.cardType} (•••• ${account.last4})."
        }
        return null
    }

    fun effectiveTransactionLimitFor(account: BankAccount): Double {
        return min(account.perTransactionLimit, _childSpendingLimit.value)
    }

    fun onQrScanned(merchantName: String, category: String, amount: Double, rawPayload: String) {
        // Default QR scans to the user's Default Payment Card if the currently selected card was frozen
        val selected = _bankAccounts.value.find { it.id == _selectedAccountId.value }
        if (selected == null) {
            _selectedAccountId.value = _defaultAccountId.value
        }
        _activeScan.value = ActiveScanPayload(merchantName, category, amount, rawPayload)
        _safetyResult.value = null
        _paymentBlockReason.value = null
        _isSafetyLoading.value = true

        // Call Gemini 3.1 Flash Lite for low-latency safety verification
        viewModelScope.launch {
            val result = geminiSafetyService.verifyMerchantSafety(
                merchantName = merchantName,
                category = category,
                amount = amount,
                currency = "SAR"
            )
            _safetyResult.value = result
            _isSafetyLoading.value = false
        }
    }

    fun dismissPaymentSheet() {
        _activeScan.value = null
        _safetyResult.value = null
        _paymentBlockReason.value = null
        _isAuthenticating.value = false
    }

    fun executeBiometricPayment() {
        val scan = _activeScan.value ?: return
        val currentAccount = _bankAccounts.value.find { it.id == _selectedAccountId.value }
            ?: _bankAccounts.value.first()

        val guardrailIssue = evaluatePaymentGuardrail(currentAccount, scan.amount)
        if (guardrailIssue != null) {
            _paymentBlockReason.value = guardrailIssue
            return
        }

        _paymentBlockReason.value = null
        _isAuthenticating.value = true

        viewModelScope.launch {
            // Realistic tactile biometric delay
            delay(900)

            // Deduct balance from selected account
            _bankAccounts.value = _bankAccounts.value.map { acc ->
                if (acc.id == currentAccount.id) {
                    acc.copy(balance = (acc.balance - scan.amount).coerceAtLeast(0.0))
                } else acc
            }

            // Save transaction to local Room database
            val record = TransactionRecord(
                merchantName = scan.merchantName,
                merchantCategory = scan.category,
                amount = scan.amount,
                currency = "SAR",
                timestamp = System.currentTimeMillis(),
                paymentCardLast4 = currentAccount.last4,
                bankAccountName = currentAccount.cardType,
                isVerifiedMerchant = _safetyResult.value?.isSafe ?: true,
                safetyStatus = _safetyResult.value?.safetyBadge ?: "Verified Safe",
                safetySummary = _safetyResult.value?.summary ?: "Verified purchase.",
                qrPayload = scan.rawPayload,
                isExpense = true
            )

            val insertedId = transactionDao.insertTransaction(record)
            _isAuthenticating.value = false
            _activeScan.value = null

            _paymentSuccess.value = PaymentSuccessState(
                merchantName = scan.merchantName,
                amount = scan.amount,
                currency = "SAR",
                transactionId = insertedId
            )
        }
    }

    fun dismissSuccessMessage() {
        _paymentSuccess.value = null
    }

    fun addPocketMoney(amount: Double) {
        val currentAccount = _bankAccounts.value.find { it.id == _selectedAccountId.value }
            ?: _bankAccounts.value.first()
        _bankAccounts.value = _bankAccounts.value.map { acc ->
            if (acc.id == currentAccount.id) {
                acc.copy(balance = acc.balance + amount)
            } else acc
        }

        viewModelScope.launch {
            transactionDao.insertTransaction(
                TransactionRecord(
                    merchantName = "Pocket Money Top-Up",
                    merchantCategory = "Allowance Deposit",
                    amount = amount,
                    currency = "SAR",
                    timestamp = System.currentTimeMillis(),
                    paymentCardLast4 = currentAccount.last4,
                    bankAccountName = currentAccount.cardType,
                    isVerifiedMerchant = true,
                    safetyStatus = "Deposit Verified",
                    safetySummary = "Funds credited instantly to ${currentAccount.bankName} (•••• ${currentAccount.last4}).",
                    qrPayload = "TOPUP_POCKET_MONEY",
                    isExpense = false
                )
            )
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            transactionDao.deleteTransaction(id)
        }
    }
}
