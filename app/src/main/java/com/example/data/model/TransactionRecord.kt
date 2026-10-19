package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val merchantName: String,
    val merchantCategory: String,
    val amount: Double,
    val currency: String = "SAR",
    val timestamp: Long = System.currentTimeMillis(),
    val paymentCardLast4: String = "8091",
    val bankAccountName: String = "Visa Debit",
    val isVerifiedMerchant: Boolean = true,
    val safetyStatus: String = "Verified Safe",
    val safetySummary: String = "Trusted merchant with active security token.",
    val qrPayload: String = "",
    val isExpense: Boolean = true
)
