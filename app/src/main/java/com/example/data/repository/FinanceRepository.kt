package com.example.data.repository

import com.example.data.local.BudgetDao
import com.example.data.local.BudgetEntity
import com.example.data.local.PreferenceDao
import com.example.data.local.PreferenceEntity
import com.example.data.local.SampleData
import com.example.data.local.TransactionDao
import com.example.data.local.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao,
    private val preferenceDao: PreferenceDao
) {
    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allBudgets: Flow<List<BudgetEntity>> = budgetDao.getAllBudgets()
    val allPreferences: Flow<List<PreferenceEntity>> = preferenceDao.getAllPreferences()

    fun getRecentTransactions(limit: Int = 10): Flow<List<TransactionEntity>> {
        return transactionDao.getRecentTransactions(limit)
    }

    suspend fun insertTransaction(transaction: TransactionEntity): Long = withContext(Dispatchers.IO) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: TransactionEntity) = withContext(Dispatchers.IO) {
        transactionDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(id: Long) = withContext(Dispatchers.IO) {
        transactionDao.deleteById(id)
    }

    suspend fun setBudget(category: String, monthlyLimit: Double) = withContext(Dispatchers.IO) {
        budgetDao.setBudget(BudgetEntity(category, monthlyLimit))
    }

    suspend fun setPreference(key: String, value: String) = withContext(Dispatchers.IO) {
        preferenceDao.setPreference(PreferenceEntity(key, value))
    }

    suspend fun resetToSampleData() = withContext(Dispatchers.IO) {
        transactionDao.deleteAllTransactions()
        budgetDao.deleteAllBudgets()
        budgetDao.insertAll(SampleData.getInitialBudgets())
        transactionDao.insertAll(SampleData.getInitialTransactions())
    }

    fun exportAsCsv(transactions: List<TransactionEntity>): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val sb = StringBuilder()
        sb.append("ID,Date,Title,Type,Category,Amount,Note\n")
        transactions.forEach { t ->
            val dateStr = dateFormat.format(Date(t.dateMillis))
            val safeTitle = t.title.replace("\"", "\"\"")
            val safeNote = t.note.replace("\"", "\"\"")
            sb.append("${t.id},\"$dateStr\",\"$safeTitle\",${t.type},\"${t.category}\",${t.amount},\"$safeNote\"\n")
        }
        return sb.toString()
    }

    fun exportAsJson(transactions: List<TransactionEntity>): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val sb = StringBuilder()
        sb.append("[\n")
        transactions.forEachIndexed { index, t ->
            val dateStr = dateFormat.format(Date(t.dateMillis))
            val safeTitle = t.title.replace("\"", "\\\"")
            val safeNote = t.note.replace("\"", "\\\"")
            sb.append("  {\n")
            sb.append("    \"id\": ${t.id},\n")
            sb.append("    \"date\": \"$dateStr\",\n")
            sb.append("    \"title\": \"$safeTitle\",\n")
            sb.append("    \"type\": \"${t.type}\",\n")
            sb.append("    \"category\": \"${t.category}\",\n")
            sb.append("    \"amount\": ${t.amount},\n")
            sb.append("    \"note\": \"$safeNote\"\n")
            sb.append("  }${if (index < transactions.size - 1) "," else ""}\n")
        }
        sb.append("]")
        return sb.toString()
    }
}
