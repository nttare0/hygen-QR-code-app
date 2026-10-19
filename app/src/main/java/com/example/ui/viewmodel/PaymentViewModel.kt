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

    // Bank Accounts
    private val _bankAccounts = MutableStateFlow(
        listOf(
            BankAccount("acc_1", "TechnoBank Saudi", "Visa Debit", "8091", 804660.00, "SAR", isPrimary = true, cardGradientType = 0),
            BankAccount("acc_2", "Reserve Digital", "Credit", "4719", 25400.00, "SAR", isPrimary = false, cardGradientType = 2),
            BankAccount("acc_3", "Youth Savings", "Debit", "0173", 6820.70, "SAR", isPrimary = false, cardGradientType = 1)
        )
    )
    val bankAccounts: StateFlow<List<BankAccount>> = _bankAccounts.asStateFlow()

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

    // Child Safety Limit (Max SAR per transaction)
    private val _childSpendingLimit = MutableStateFlow(100.0)
    val childSpendingLimit: StateFlow<Double> = _childSpendingLimit.asStateFlow()

    // NFC & Biometric Settings
    private val _isNfcEnabled = MutableStateFlow(true)
    val isNfcEnabled: StateFlow<Boolean> = _isNfcEnabled.asStateFlow()

    private val _isBiometricsRequired = MutableStateFlow(true)
    val isBiometricsRequired: StateFlow<Boolean> = _isBiometricsRequired.asStateFlow()

    init {
        // Pre-populate initial sample history if database is empty
        viewModelScope.launch {
            // Check if initial data is needed
            // We can insert standard onboarding transactions if empty
        }
    }

    fun selectAccount(accountId: String) {
        _selectedAccountId.value = accountId
    }

    fun toggleNfc(enabled: Boolean) {
        _isNfcEnabled.value = enabled
    }

    fun toggleBiometrics(required: Boolean) {
        _isBiometricsRequired.value = required
    }

    fun onQrScanned(merchantName: String, category: String, amount: Double, rawPayload: String) {
        _activeScan.value = ActiveScanPayload(merchantName, category, amount, rawPayload)
        _safetyResult.value = null
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
        _isAuthenticating.value = false
    }

    fun executeBiometricPayment() {
        val scan = _activeScan.value ?: return
        val currentAccount = _bankAccounts.value.find { it.id == _selectedAccountId.value } 
            ?: _bankAccounts.value.first()

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
        val currentId = _selectedAccountId.value
        _bankAccounts.value = _bankAccounts.value.map { acc ->
            if (acc.id == currentId) {
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
                    paymentCardLast4 = "8091",
                    bankAccountName = "Bank Transfer",
                    isVerifiedMerchant = true,
                    safetyStatus = "Deposit Verified",
                    safetySummary = "Funds credited instantly via parent bank account.",
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
