package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val type: String, // "INCOME" or "EXPENSE"
    val category: String,
    val dateMillis: Long,
    val note: String = ""
) {
    fun toTransactionType(): TransactionType {
        return if (type.equals("INCOME", ignoreCase = true)) TransactionType.INCOME else TransactionType.EXPENSE
    }
}

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey
    val category: String,
    val monthlyLimit: Double
)

@Entity(tableName = "preferences")
data class PreferenceEntity(
    @PrimaryKey
    val key: String,
    val value: String
)
