package com.example.data.local

import com.example.data.model.CategoryRepository
import java.util.Calendar

object SampleData {
    fun getInitialBudgets(): List<BudgetEntity> {
        return CategoryRepository.expenseCategories.map {
            BudgetEntity(
                category = it.name,
                monthlyLimit = it.defaultBudget
            )
        }
    }

    fun getInitialTransactions(): List<TransactionEntity> {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        fun createDate(day: Int, hour: Int, minute: Int): Long {
            val cal = Calendar.getInstance()
            cal.set(Calendar.YEAR, currentYear)
            cal.set(Calendar.MONTH, currentMonth)
            cal.set(Calendar.DAY_OF_MONTH, day.coerceIn(1, 28))
            cal.set(Calendar.HOUR_OF_DAY, hour)
            cal.set(Calendar.MINUTE, minute)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }

        return listOf(
            // Income
            TransactionEntity(
                title = "Monthly Salary",
                amount = 4500.0,
                type = "INCOME",
                category = "Salary",
                dateMillis = createDate(1, 9, 0),
                note = "Tech Corp Payroll"
            ),
            TransactionEntity(
                title = "Freelance UI Project",
                amount = 750.0,
                type = "INCOME",
                category = "Freelance",
                dateMillis = createDate(8, 14, 30),
                note = "Design system consultation"
            ),
            TransactionEntity(
                title = "Dividend Payout",
                amount = 120.50,
                type = "INCOME",
                category = "Investments",
                dateMillis = createDate(12, 10, 0),
                note = "Quarterly ETF dividend"
            ),

            // Expenses
            TransactionEntity(
                title = "Whole Foods Market",
                amount = 135.40,
                type = "EXPENSE",
                category = "Food & Dining",
                dateMillis = createDate(2, 17, 45),
                note = "Weekly organic groceries"
            ),
            TransactionEntity(
                title = "Electric & Water Bill",
                amount = 145.20,
                type = "EXPENSE",
                category = "Bills & Utilities",
                dateMillis = createDate(3, 11, 15),
                note = "City utility services"
            ),
            TransactionEntity(
                title = "Ride to Airport",
                amount = 42.50,
                type = "EXPENSE",
                category = "Transport",
                dateMillis = createDate(4, 8, 30),
                note = "Uber ride"
            ),
            TransactionEntity(
                title = "Apple Store Online",
                amount = 189.00,
                type = "EXPENSE",
                category = "Shopping",
                dateMillis = createDate(5, 15, 20),
                note = "AirPods Pro replacement"
            ),
            TransactionEntity(
                title = "Italian Bistro Dinner",
                amount = 88.00,
                type = "EXPENSE",
                category = "Food & Dining",
                dateMillis = createDate(6, 19, 30),
                note = "Dinner with Sarah"
            ),
            TransactionEntity(
                title = "High-Speed Internet",
                amount = 79.99,
                type = "EXPENSE",
                category = "Bills & Utilities",
                dateMillis = createDate(7, 10, 0),
                note = "Fiber broadband"
            ),
            TransactionEntity(
                title = "Gym Membership",
                amount = 65.00,
                type = "EXPENSE",
                category = "Health & Fitness",
                dateMillis = createDate(9, 7, 0),
                note = "Monthly fitness club"
            ),
            TransactionEntity(
                title = "Trader Joe's",
                amount = 92.30,
                type = "EXPENSE",
                category = "Food & Dining",
                dateMillis = createDate(10, 16, 15),
                note = "Snacks and pantry restock"
            ),
            TransactionEntity(
                title = "Cinema Tickets & Popcorn",
                amount = 38.50,
                type = "EXPENSE",
                category = "Entertainment",
                dateMillis = createDate(11, 20, 0),
                note = "Sci-fi movie night"
            ),
            TransactionEntity(
                title = "Fuel Station",
                amount = 55.00,
                type = "EXPENSE",
                category = "Transport",
                dateMillis = createDate(12, 13, 10),
                note = "Full tank gasoline"
            ),
            TransactionEntity(
                title = "Coffee & Bakery",
                amount = 16.75,
                type = "EXPENSE",
                category = "Food & Dining",
                dateMillis = createDate(13, 9, 20),
                note = "Latte & croissant"
            ),
            TransactionEntity(
                title = "Online Course Subscription",
                amount = 49.00,
                type = "EXPENSE",
                category = "Education",
                dateMillis = createDate(14, 18, 0),
                note = "Advanced Kotlin certification"
            ),
            TransactionEntity(
                title = "Weekend Getaway Fuel",
                amount = 62.00,
                type = "EXPENSE",
                category = "Travel",
                dateMillis = createDate(14, 21, 0),
                note = "Mountain cabin trip"
            )
        )
    }
}
