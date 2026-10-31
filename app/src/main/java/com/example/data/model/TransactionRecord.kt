package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val merchantName: String,
    val merchantCategory: String,
    val amount: Double,
    val currency: String,
    val timestamp: Long,
    val paymentCardLast4: String,
    val bankAccountName: String,
    val isVerifiedMerchant: Boolean,
    val safetyStatus: String,
    val safetySummary: String,
    val qrPayload: String,
    val isExpense: Boolean = true
)
