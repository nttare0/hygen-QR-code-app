package com.example.data.model

data class BankAccount(
    val id: String,
    val bankName: String,
    val cardType: String,
    val last4: String,
    val balance: Double,
    val currency: String,
    val isPrimary: Boolean = false,
    val cardGradientType: Int = 0,
    val isFrozen: Boolean = false,
    val perTransactionLimit: Double = 1000.0,
    val expiryDate: String = "08/29",
    val cardholderName: String = "ALEXIS R. CHEN"
)
