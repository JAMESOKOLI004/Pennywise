package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CurrencyRepository
import com.example.ui.components.DonutChart
import com.example.ui.components.TransactionItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.PennyWiseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: PennyWiseViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToCategoryBreakdown: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalBalance by viewModel.totalBalance.collectAsStateWithLifecycle()
    val monthSummary by viewModel.currentMonthSummary.collectAsStateWithLifecycle()
    val donutSlices by viewModel.donutSlices.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val currency by viewModel.currentCurrency.collectAsStateWithLifecycle()

    var showExpenseSummary by remember { mutableStateOf(false) }

    val recentTransactions = remember(allTransactions) {
        allTransactions.take(5)
    }

    Scaffold(
        modifier = modifier.testTag("dashboard_screen"),
        containerColor = SlateBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = IncomeGreen,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("fab_add_transaction")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction",
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Top Header with Current Balance
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .testTag("card_current_balance"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(22.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Balance",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = CharcoalSubtext
                            )
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = IncomeGreenLight
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = IncomeGreenDark,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${monthSummary.savingsRate}% Saved",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IncomeGreenDark
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = CurrencyRepository.formatAmount(totalBalance, currency.symbol),
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = CharcoalText,
                            modifier = Modifier.testTag("text_total_balance")
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        // Income vs Expense Summary Cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Income Card
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { showExpenseSummary = false }
                                    .testTag("card_summary_income"),
                                color = if (!showExpenseSummary) IncomeGreenLight.copy(alpha = 0.6f) else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(IncomeGreen),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowUpward,
                                            contentDescription = "Income",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Income",
                                            fontSize = 11.sp,
                                            color = CharcoalSubtext,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = CurrencyRepository.formatAmount(monthSummary.totalIncome, currency.symbol),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = IncomeGreen
                                        )
                                    }
                                }
                            }

                            // Expense Card
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { showExpenseSummary = true }
                                    .testTag("card_summary_expense"),
                                color = if (showExpenseSummary) ExpenseCoralLight.copy(alpha = 0.6f) else Color(0xFFF8FAFC),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(ExpenseCoral),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ArrowDownward,
                                            contentDescription = "Expense",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "Expense",
                                            fontSize = 11.sp,
                                            color = CharcoalSubtext,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = CurrencyRepository.formatAmount(monthSummary.totalExpense, currency.symbol),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = ExpenseCoral
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                // This Month's Spending Donut Chart
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .testTag("card_spending_donut"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "This Month's Spending",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText
                            )
                            TextButton(
                                onClick = onNavigateToCategoryBreakdown,
                                modifier = Modifier.testTag("btn_view_category_breakdown")
                            ) {
                                Text(
                                    text = "Details",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = IncomeGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        DonutChart(
                            slices = donutSlices,
                            totalAmount = monthSummary.totalExpense,
                            currencySymbol = currency.symbol,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            item {
                // Recent Transactions Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Transactions",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText
                    )
                    TextButton(
                        onClick = onNavigateToTransactions,
                        modifier = Modifier.testTag("btn_see_all_transactions")
                    ) {
                        Text(
                            text = "See All",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = IncomeGreen
                        )
                    }
                }
            }

            if (recentTransactions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 600.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No transactions yet. Tap + to add one!",
                                color = CharcoalSubtext,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(recentTransactions, key = { it.id }) { transaction ->
                    Box(modifier = Modifier.widthIn(max = 600.dp)) {
                        TransactionItem(
                            transaction = transaction,
                            currencySymbol = currency.symbol,
                            onDeleteClick = { viewModel.deleteTransaction(transaction.id) }
                        )
                    }
                }
            }
        }
    }
}
