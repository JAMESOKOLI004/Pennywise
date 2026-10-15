package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CurrencyRepository
import com.example.ui.theme.*
import com.example.ui.viewmodel.BudgetUiItem
import com.example.ui.viewmodel.PennyWiseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    viewModel: PennyWiseViewModel,
    modifier: Modifier = Modifier
) {
    val budgetItems by viewModel.budgetItems.collectAsStateWithLifecycle()
    val currency by viewModel.currentCurrency.collectAsStateWithLifecycle()

    var editingBudget by remember { mutableStateOf<BudgetUiItem?>(null) }
    var editAmountString by remember { mutableStateOf("") }

    val totalBudgetLimit = remember(budgetItems) { budgetItems.sumOf { it.monthlyLimit } }
    val totalBudgetSpent = remember(budgetItems) { budgetItems.sumOf { it.spent } }
    val overBudgetCount = remember(budgetItems) { budgetItems.count { it.isOverBudget } }

    Scaffold(
        modifier = modifier.testTag("budget_screen"),
        containerColor = SlateBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Monthly Budgets",
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SlateBackground)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Overall Budget Summary Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .testTag("card_overall_budget"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Budget Overview",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText
                            )

                            if (overBudgetCount > 0) {
                                Surface(
                                    color = ExpenseCoralLight,
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = ExpenseCoralDark,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "$overBudgetCount Over Budget",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ExpenseCoralDark
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "Spent this month",
                                    fontSize = 12.sp,
                                    color = CharcoalSubtext
                                )
                                Text(
                                    text = CurrencyRepository.formatAmount(totalBudgetSpent, currency.symbol),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (totalBudgetSpent > totalBudgetLimit) ExpenseCoral else CharcoalText
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Total Limit",
                                    fontSize = 12.sp,
                                    color = CharcoalSubtext
                                )
                                Text(
                                    text = CurrencyRepository.formatAmount(totalBudgetLimit, currency.symbol),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val overallRatio = if (totalBudgetLimit > 0) (totalBudgetSpent / totalBudgetLimit).toFloat().coerceIn(0f, 1f) else 0f
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFF1F5F9))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(overallRatio)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (totalBudgetSpent > totalBudgetLimit) ExpenseCoral else IncomeGreen)
                            )
                        }
                    }
                }
            }

            // Category Budgets Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Category Limits",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText
                    )
                    Text(
                        text = "Tap pencil to set limit",
                        fontSize = 12.sp,
                        color = CharcoalSubtext
                    )
                }
            }

            // Category Budget Cards
            items(budgetItems, key = { it.category }) { item ->
                val progressRatio = item.percentage.coerceIn(0f, 1f)
                val animatedProgress by animateFloatAsState(
                    targetValue = progressRatio,
                    animationSpec = tween(500),
                    label = "budget_progress"
                )

                val progressColor = when {
                    item.isOverBudget -> ExpenseCoral
                    item.percentage >= 0.75f -> CatAmber
                    else -> IncomeGreen
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .then(
                            if (item.isOverBudget) {
                                Modifier.border(1.5.dp, ExpenseCoral.copy(alpha = 0.8f), RoundedCornerShape(18.dp))
                            } else Modifier
                        )
                        .testTag("budget_card_${item.category.replace(" ", "_")}"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(18.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(item.categoryItem.color.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = item.categoryItem.icon,
                                        contentDescription = item.category,
                                        tint = item.categoryItem.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = item.category,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CharcoalText
                                    )
                                    Text(
                                        text = "${(item.percentage * 100).toInt()}% used",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = progressColor
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = CurrencyRepository.formatAmount(item.spent, currency.symbol),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (item.isOverBudget) ExpenseCoral else CharcoalText
                                    )
                                    Text(
                                        text = "Limit: ${CurrencyRepository.formatAmount(item.monthlyLimit, currency.symbol)}",
                                        fontSize = 12.sp,
                                        color = CharcoalSubtext
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                IconButton(
                                    onClick = {
                                        editingBudget = item
                                        editAmountString = item.monthlyLimit.toInt().toString()
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .testTag("btn_edit_budget_${item.category.replace(" ", "_")}")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = "Edit budget limit",
                                        tint = CharcoalSubtext,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Progress Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFF1F5F9))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(animatedProgress)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(progressColor)
                            )
                        }

                        // Over Budget Warning Banner
                        if (item.isOverBudget) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Surface(
                                color = ExpenseCoralLight,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Warning,
                                        contentDescription = "Warning",
                                        tint = ExpenseCoralDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Over budget by ${CurrencyRepository.formatAmount(item.overAmount, currency.symbol)}!",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ExpenseCoralDark
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Edit Budget Limit Dialog
    editingBudget?.let { item ->
        AlertDialog(
            onDismissRequest = { editingBudget = null },
            title = {
                Text(
                    text = "Edit ${item.category} Budget",
                    fontWeight = FontWeight.Bold,
                    color = CharcoalText
                )
            },
            text = {
                Column {
                    Text(
                        text = "Enter new monthly spending limit:",
                        fontSize = 13.sp,
                        color = CharcoalSubtext
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = editAmountString,
                        onValueChange = { editAmountString = it },
                        prefix = { Text(currency.symbol) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_edit_budget_limit"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newLimit = editAmountString.toDoubleOrNull()
                        if (newLimit != null && newLimit >= 0) {
                            viewModel.updateBudget(item.category, newLimit)
                        }
                        editingBudget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IncomeGreen),
                    modifier = Modifier.testTag("btn_confirm_edit_budget")
                ) {
                    Text("Save Limit")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingBudget = null }) {
                    Text("Cancel", color = CharcoalSubtext)
                }
            }
        )
    }
}
