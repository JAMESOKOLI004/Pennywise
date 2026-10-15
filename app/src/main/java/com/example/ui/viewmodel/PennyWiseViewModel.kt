package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PennyWiseDatabase
import com.example.data.local.TransactionEntity
import com.example.data.model.CategoryItem
import com.example.data.model.CategoryRepository
import com.example.data.model.CurrencyOption
import com.example.data.model.CurrencyRepository
import com.example.data.model.TransactionType
import com.example.data.repository.FinanceRepository
import com.example.ui.components.CategorySpendItem
import com.example.ui.components.DailySpendPoint
import com.example.ui.components.DonutSlice
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class BudgetUiItem(
    val category: String,
    val monthlyLimit: Double,
    val spent: Double,
    val percentage: Float, // 0.0 to 1.0+
    val isOverBudget: Boolean,
    val overAmount: Double,
    val categoryItem: CategoryItem
)

data class MonthSpendSummary(
    val totalIncome: Double,
    val totalExpense: Double,
    val netBalance: Double,
    val savingsRate: Int
)

class PennyWiseViewModel(application: Application) : AndroidViewModel(application) {
    private val db = PennyWiseDatabase.getDatabase(application, viewModelScope)
    private val repository = FinanceRepository(db.transactionDao(), db.budgetDao(), db.preferenceDao())

    // All data streams
    val allTransactions: StateFlow<List<TransactionEntity>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBudgets = repository.allBudgets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val allPreferences = repository.allPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currency preference
    val currentCurrency: StateFlow<CurrencyOption> = allPreferences.map { prefs ->
        val code = prefs.find { it.key == "currency" }?.value ?: "USD"
        CurrencyRepository.getCurrency(code)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CurrencyRepository.supportedCurrencies.first())

    // Notification preferences
    val budgetAlertsEnabled: StateFlow<Boolean> = allPreferences.map { prefs ->
        prefs.find { it.key == "budget_alerts" }?.value != "false"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val dailyRemindersEnabled: StateFlow<Boolean> = allPreferences.map { prefs ->
        prefs.find { it.key == "daily_reminders" }?.value != "false"
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    // Filter states for Transaction List
    val searchQuery = MutableStateFlow("")
    val filterType = MutableStateFlow("ALL") // "ALL", "INCOME", "EXPENSE"
    val filterCategory = MutableStateFlow("ALL")
    val filterDateRange = MutableStateFlow("ALL") // "ALL", "THIS_MONTH", "LAST_MONTH"

    // Selected month for Monthly Report and Category Breakdown (0-indexed month)
    val selectedYear = MutableStateFlow(Calendar.getInstance().get(Calendar.YEAR))
    val selectedMonth = MutableStateFlow(Calendar.getInstance().get(Calendar.MONTH))

    // Overall Balance (Total Income - Total Expense across all time)
    val totalBalance: StateFlow<Double> = allTransactions.map { list ->
        val income = list.filter { it.type == "INCOME" }.sumOf { it.amount }
        val expense = list.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        income - expense
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Current Month Summary
    val currentMonthSummary: StateFlow<MonthSpendSummary> = combine(
        allTransactions,
        selectedYear,
        selectedMonth
    ) { transactions, year, month ->
        val cal = Calendar.getInstance()
        val monthTransactions = transactions.filter { t ->
            cal.timeInMillis = t.dateMillis
            cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == month
        }
        val income = monthTransactions.filter { it.type == "INCOME" }.sumOf { it.amount }
        val expense = monthTransactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
        val savingsRate = if (income > 0) (((income - expense) / income) * 100).toInt().coerceIn(0, 100) else 0

        MonthSpendSummary(
            totalIncome = income,
            totalExpense = expense,
            netBalance = income - expense,
            savingsRate = savingsRate
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MonthSpendSummary(0.0, 0.0, 0.0, 0))

    // Donut Chart Slices (Current month expenses by category)
    val donutSlices: StateFlow<List<DonutSlice>> = combine(
        allTransactions,
        selectedYear,
        selectedMonth
    ) { transactions, year, month ->
        val cal = Calendar.getInstance()
        val monthExpenses = transactions.filter { t ->
            t.type == "EXPENSE" &&
                cal.apply { timeInMillis = t.dateMillis }.get(Calendar.YEAR) == year &&
                cal.get(Calendar.MONTH) == month
        }

        val grouped = monthExpenses.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }

        grouped.map { (cat, amount) ->
            DonutSlice(
                label = cat,
                value = amount,
                color = CategoryRepository.getColorForCategory(cat)
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Category Breakdown Items (for Category Breakdown Screen)
    val categoryBreakdown: StateFlow<List<CategorySpendItem>> = combine(
        allTransactions,
        selectedYear,
        selectedMonth
    ) { transactions, year, month ->
        val cal = Calendar.getInstance()
        val monthExpenses = transactions.filter { t ->
            t.type == "EXPENSE" &&
                cal.apply { timeInMillis = t.dateMillis }.get(Calendar.YEAR) == year &&
                cal.get(Calendar.MONTH) == month
        }
        val totalExpenses = monthExpenses.sumOf { it.amount }

        CategoryRepository.expenseCategories.mapNotNull { catItem ->
            val catTransactions = monthExpenses.filter { it.category.equals(catItem.name, ignoreCase = true) }
            val spent = catTransactions.sumOf { it.amount }
            if (spent > 0) {
                CategorySpendItem(
                    categoryName = catItem.name,
                    totalSpent = spent,
                    percentage = if (totalExpenses > 0) (spent / totalExpenses).toFloat() else 0f,
                    icon = catItem.icon,
                    color = catItem.color,
                    transactionCount = catTransactions.size
                )
            } else null
        }.sortedByDescending { it.totalSpent }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Top 5 Spending Categories for Monthly Report
    val top5SpendingCategories: StateFlow<List<CategorySpendItem>> = categoryBreakdown.map { list ->
        list.take(5)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Daily spending points for Line Chart
    val dailySpendingPoints: StateFlow<List<DailySpendPoint>> = combine(
        allTransactions,
        selectedYear,
        selectedMonth
    ) { transactions, year, month ->
        val cal = Calendar.getInstance()
        cal.set(year, month, 1)
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)

        val monthExpenses = transactions.filter { t ->
            t.type == "EXPENSE" &&
                cal.apply { timeInMillis = t.dateMillis }.get(Calendar.YEAR) == year &&
                cal.get(Calendar.MONTH) == month
        }

        val dayToAmount = mutableMapOf<Int, Double>()
        for (d in 1..maxDays) {
            dayToAmount[d] = 0.0
        }
        monthExpenses.forEach { t ->
            cal.timeInMillis = t.dateMillis
            val day = cal.get(Calendar.DAY_OF_MONTH)
            dayToAmount[day] = (dayToAmount[day] ?: 0.0) + t.amount
        }

        dayToAmount.toSortedMap().map { (day, amount) ->
            DailySpendPoint(day, amount)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Budget UI Items (Calculates spent vs limit, percentages, and red warnings)
    val budgetItems: StateFlow<List<BudgetUiItem>> = combine(
        allBudgets,
        allTransactions,
        selectedYear,
        selectedMonth
    ) { budgets, transactions, year, month ->
        val cal = Calendar.getInstance()
        val monthExpenses = transactions.filter { t ->
            t.type == "EXPENSE" &&
                cal.apply { timeInMillis = t.dateMillis }.get(Calendar.YEAR) == year &&
                cal.get(Calendar.MONTH) == month
        }

        CategoryRepository.expenseCategories.map { catItem ->
            val budget = budgets.find { it.category.equals(catItem.name, ignoreCase = true) }
            val limit = budget?.monthlyLimit ?: catItem.defaultBudget
            val spent = monthExpenses.filter { it.category.equals(catItem.name, ignoreCase = true) }.sumOf { it.amount }
            val ratio = if (limit > 0) (spent / limit).toFloat() else 0f
            val isOver = spent > limit && limit > 0
            val overAmount = if (isOver) spent - limit else 0.0

            BudgetUiItem(
                category = catItem.name,
                monthlyLimit = limit,
                spent = spent,
                percentage = ratio,
                isOverBudget = isOver,
                overAmount = overAmount,
                categoryItem = catItem
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Transactions for Transaction List
    val filteredTransactions: StateFlow<Map<String, List<TransactionEntity>>> = combine(
        allTransactions,
        searchQuery,
        filterType,
        filterCategory,
        filterDateRange
    ) { transactions, query, type, category, dateRange ->
        val cal = Calendar.getInstance()
        val now = System.currentTimeMillis()
        val currentYear = cal.get(Calendar.YEAR)
        val currentMonth = cal.get(Calendar.MONTH)

        val filtered = transactions.filter { t ->
            val matchesQuery = query.isBlank() ||
                t.title.contains(query, ignoreCase = true) ||
                t.note.contains(query, ignoreCase = true) ||
                t.category.contains(query, ignoreCase = true)

            val matchesType = when (type) {
                "INCOME" -> t.type == "INCOME"
                "EXPENSE" -> t.type == "EXPENSE"
                else -> true
            }

            val matchesCategory = category == "ALL" || t.category.equals(category, ignoreCase = true)

            val matchesDate = when (dateRange) {
                "THIS_MONTH" -> {
                    cal.timeInMillis = t.dateMillis
                    cal.get(Calendar.YEAR) == currentYear && cal.get(Calendar.MONTH) == currentMonth
                }
                "LAST_MONTH" -> {
                    cal.timeInMillis = t.dateMillis
                    val prevMonth = if (currentMonth == 0) 11 else currentMonth - 1
                    val prevYear = if (currentMonth == 0) currentYear - 1 else currentYear
                    cal.get(Calendar.YEAR) == prevYear && cal.get(Calendar.MONTH) == prevMonth
                }
                else -> true
            }

            matchesQuery && matchesType && matchesCategory && matchesDate
        }

        // Group by formatted date header (e.g. "Today", "Yesterday", "Oct 12, 2026")
        val headerFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        val todayStr = headerFormat.format(Date(now))
        val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val yesterdayStr = headerFormat.format(yesterdayCal.time)

        filtered.groupBy { t ->
            val dateStr = headerFormat.format(Date(t.dateMillis))
            when (dateStr) {
                todayStr -> "Today"
                yesterdayStr -> "Yesterday"
                else -> dateStr
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Actions
    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType,
        category: String,
        dateMillis: Long,
        note: String = ""
    ) {
        viewModelScope.launch {
            repository.insertTransaction(
                TransactionEntity(
                    title = title.ifBlank { category },
                    amount = amount,
                    type = type.name,
                    category = category,
                    dateMillis = dateMillis,
                    note = note
                )
            )
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    fun updateBudget(category: String, monthlyLimit: Double) {
        viewModelScope.launch {
            repository.setBudget(category, monthlyLimit)
        }
    }

    fun setCurrency(code: String) {
        viewModelScope.launch {
            repository.setPreference("currency", code)
        }
    }

    fun togglePreference(key: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.setPreference(key, enabled.toString())
        }
    }

    fun resetData() {
        viewModelScope.launch {
            repository.resetToSampleData()
        }
    }

    fun getExportCsv(): String {
        return repository.exportAsCsv(allTransactions.value)
    }

    fun getExportJson(): String {
        return repository.exportAsJson(allTransactions.value)
    }

    fun prevMonth() {
        if (selectedMonth.value == 0) {
            selectedMonth.value = 11
            selectedYear.value -= 1
        } else {
            selectedMonth.value -= 1
        }
    }

    fun nextMonth() {
        if (selectedMonth.value == 11) {
            selectedMonth.value = 0
            selectedYear.value += 1
        } else {
            selectedMonth.value += 1
        }
    }
}
