package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.*

enum class TransactionType {
    INCOME,
    EXPENSE
}

data class CategoryItem(
    val id: String,
    val name: String,
    val type: TransactionType,
    val icon: ImageVector,
    val color: Color,
    val defaultBudget: Double = 0.0
)

object CategoryRepository {
    val expenseCategories = listOf(
        CategoryItem("food", "Food & Dining", TransactionType.EXPENSE, Icons.Default.Restaurant, CatAmber, defaultBudget = 500.0),
        CategoryItem("shopping", "Shopping", TransactionType.EXPENSE, Icons.Default.ShoppingBag, CatPurple, defaultBudget = 300.0),
        CategoryItem("transport", "Transport", TransactionType.EXPENSE, Icons.Default.DirectionsCar, CatBlue, defaultBudget = 150.0),
        CategoryItem("bills", "Bills & Utilities", TransactionType.EXPENSE, Icons.Default.ReceiptLong, CatEmerald, defaultBudget = 400.0),
        CategoryItem("entertainment", "Entertainment", TransactionType.EXPENSE, Icons.Default.Movie, CatPink, defaultBudget = 150.0),
        CategoryItem("health", "Health & Fitness", TransactionType.EXPENSE, Icons.Default.FitnessCenter, CatCyan, defaultBudget = 100.0),
        CategoryItem("education", "Education", TransactionType.EXPENSE, Icons.Default.School, CatIndigo, defaultBudget = 200.0),
        CategoryItem("travel", "Travel", TransactionType.EXPENSE, Icons.Default.Flight, CatTeal, defaultBudget = 250.0),
        CategoryItem("other_expense", "General & Other", TransactionType.EXPENSE, Icons.Default.Category, CatSlate, defaultBudget = 100.0)
    )

    val incomeCategories = listOf(
        CategoryItem("salary", "Salary", TransactionType.INCOME, Icons.Default.AccountBalance, IncomeGreen),
        CategoryItem("freelance", "Freelance", TransactionType.INCOME, Icons.Default.LaptopMac, CatIndigo),
        CategoryItem("investments", "Investments", TransactionType.INCOME, Icons.Default.TrendingUp, CatEmerald),
        CategoryItem("gifts", "Gifts", TransactionType.INCOME, Icons.Default.CardGiftcard, CatPink),
        CategoryItem("other_income", "Other Income", TransactionType.INCOME, Icons.Default.Payments, CatTeal)
    )

    val allCategories = expenseCategories + incomeCategories

    fun getCategoryByName(name: String): CategoryItem {
        return allCategories.find { it.name.equals(name, ignoreCase = true) }
            ?: CategoryItem("unknown", name, TransactionType.EXPENSE, Icons.Default.AttachMoney, CatSlate)
    }

    fun getColorForCategory(name: String): Color {
        return getCategoryByName(name).color
    }

    fun getIconForCategory(name: String): ImageVector {
        return getCategoryByName(name).icon
    }
}
