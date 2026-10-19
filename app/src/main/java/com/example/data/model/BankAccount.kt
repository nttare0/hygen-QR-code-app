package com.example.data.model

data class BankAccount(
    val id: String,
    val bankName: String,
    val cardType: String, // "Visa Debit", "Credit"
    val last4: String,
    val balance: Double,
    val currency: String = "SAR",
    val isPrimary: Boolean = false,
    val cardGradientType: Int = 0 // 0: Blue, 1: Coral, 2: Purple
)
