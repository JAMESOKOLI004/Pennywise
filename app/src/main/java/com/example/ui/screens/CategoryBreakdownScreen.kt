package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
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
import com.example.data.model.CategoryRepository
import com.example.data.model.CurrencyRepository
import com.example.ui.components.HorizontalBarChart
import com.example.ui.components.TransactionItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.PennyWiseViewModel
import java.text.DateFormatSymbols
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBreakdownScreen(
    viewModel: PennyWiseViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryBreakdown by viewModel.categoryBreakdown.collectAsStateWithLifecycle()
    val monthSummary by viewModel.currentMonthSummary.collectAsStateWithLifecycle()
    val currency by viewModel.currentCurrency.collectAsStateWithLifecycle()
    val selectedYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val selectedMonth by viewModel.selectedMonth.collectAsStateWithLifecycle()
    val allTransactions by viewModel.allTransactions.collectAsStateWithLifecycle()

    val monthName = DateFormatSymbols().months[selectedMonth]

    var drillDownCategory by remember { mutableStateOf<String?>(null) }

    val drillDownTransactions = remember(drillDownCategory, allTransactions, selectedYear, selectedMonth) {
        val cal = Calendar.getInstance()
        drillDownCategory?.let { cat ->
            allTransactions.filter { t ->
                t.category.equals(cat, ignoreCase = true) &&
                    cal.apply { timeInMillis = t.dateMillis }.get(Calendar.YEAR) == selectedYear &&
                    cal.get(Calendar.MONTH) == selectedMonth
            }
        } ?: emptyList()
    }

    Scaffold(
        modifier = modifier.testTag("category_breakdown_screen"),
        containerColor = SlateBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Category Breakdown",
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("btn_back_category_breakdown")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = CharcoalText
                        )
                    }
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
            contentPadding = PaddingValues(top = 12.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Month Selector Header
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { viewModel.prevMonth() },
                            modifier = Modifier.testTag("btn_prev_month")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "Previous Month",
                                tint = CharcoalText
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$monthName $selectedYear",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText
                            )
                            Text(
                                text = "Total: ${CurrencyRepository.formatAmount(monthSummary.totalExpense, currency.symbol)}",
                                fontSize = 12.sp,
                                color = ExpenseCoral,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        IconButton(
                            onClick = { viewModel.nextMonth() },
                            modifier = Modifier.testTag("btn_next_month")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Next Month",
                                tint = CharcoalText
                            )
                        }
                    }
                }
            }

            // Horizontal Bar Chart
            if (categoryBreakdown.isEmpty()) {
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
                                .padding(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No expenses recorded for $monthName $selectedYear",
                                color = CharcoalSubtext,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                item {
                    Text(
                        text = "Tap any category to view its transactions",
                        fontSize = 12.sp,
                        color = CharcoalSubtext,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                item {
                    Box(modifier = Modifier.widthIn(max = 600.dp)) {
                        HorizontalBarChart(
                            items = categoryBreakdown,
                            currencySymbol = currency.symbol,
                            onCategoryClick = { catName ->
                                drillDownCategory = catName
                            }
                        )
                    }
                }
            }
        }
    }

    // Category Drill-Down Modal Bottom Sheet
    if (drillDownCategory != null) {
        ModalBottomSheet(
            onDismissRequest = { drillDownCategory = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            modifier = Modifier.testTag("sheet_category_drill_down")
        ) {
            val catItem = CategoryRepository.getCategoryByName(drillDownCategory!!)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 32.dp)
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
                                .background(catItem.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = catItem.icon,
                                contentDescription = null,
                                tint = catItem.color,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = drillDownCategory!!,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText
                            )
                            Text(
                                text = "${drillDownTransactions.size} transactions in $monthName",
                                fontSize = 12.sp,
                                color = CharcoalSubtext
                            )
                        }
                    }

                    IconButton(onClick = { drillDownCategory = null }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close sheet",
                            tint = CharcoalSubtext
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (drillDownTransactions.isEmpty()) {
                    Text(
                        text = "No transactions found in this category.",
                        color = CharcoalSubtext,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(vertical = 24.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(drillDownTransactions, key = { it.id }) { t ->
                            TransactionItem(
                                transaction = t,
                                currencySymbol = currency.symbol,
                                onDeleteClick = { viewModel.deleteTransaction(t.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}
