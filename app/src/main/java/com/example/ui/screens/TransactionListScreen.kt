package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
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
import com.example.ui.components.TransactionItem
import com.example.ui.theme.*
import com.example.ui.viewmodel.PennyWiseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    viewModel: PennyWiseViewModel,
    modifier: Modifier = Modifier
) {
    val groupedTransactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val currency by viewModel.currentCurrency.collectAsStateWithLifecycle()

    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val filterType by viewModel.filterType.collectAsStateWithLifecycle()
    val filterCategory by viewModel.filterCategory.collectAsStateWithLifecycle()
    val filterDateRange by viewModel.filterDateRange.collectAsStateWithLifecycle()

    var showCategoryDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.testTag("transaction_list_screen"),
        containerColor = SlateBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Transactions",
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SlateBackground)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.searchQuery.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .testTag("search_transactions_input"),
                placeholder = { Text("Search by name, category, or note...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = CharcoalSubtext
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = CharcoalSubtext
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = IncomeGreen,
                    unfocusedBorderColor = SlateBorder
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Type Filter: ALL
                item {
                    FilterChip(
                        selected = filterType == "ALL",
                        onClick = { viewModel.filterType.value = "ALL" },
                        label = { Text("All Types") },
                        modifier = Modifier.testTag("filter_type_all")
                    )
                }
                // Type Filter: Expense
                item {
                    FilterChip(
                        selected = filterType == "EXPENSE",
                        onClick = { viewModel.filterType.value = if (filterType == "EXPENSE") "ALL" else "EXPENSE" },
                        label = { Text("Expenses") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ExpenseCoralLight,
                            selectedLabelColor = ExpenseCoralDark
                        ),
                        modifier = Modifier.testTag("filter_type_expense")
                    )
                }
                // Type Filter: Income
                item {
                    FilterChip(
                        selected = filterType == "INCOME",
                        onClick = { viewModel.filterType.value = if (filterType == "INCOME") "ALL" else "INCOME" },
                        label = { Text("Income") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IncomeGreenLight,
                            selectedLabelColor = IncomeGreenDark
                        ),
                        modifier = Modifier.testTag("filter_type_income")
                    )
                }
                // Date Range: This Month
                item {
                    FilterChip(
                        selected = filterDateRange == "THIS_MONTH",
                        onClick = { viewModel.filterDateRange.value = if (filterDateRange == "THIS_MONTH") "ALL" else "THIS_MONTH" },
                        label = { Text("This Month") },
                        modifier = Modifier.testTag("filter_date_this_month")
                    )
                }
                // Category Picker Chip
                item {
                    ElevatedFilterChip(
                        selected = filterCategory != "ALL",
                        onClick = { showCategoryDialog = true },
                        label = { Text(if (filterCategory == "ALL") "Category" else filterCategory) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier.testTag("filter_category_btn")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scrollable List Grouped by Date
            if (groupedTransactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No transactions found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try adjusting your search or filters",
                            fontSize = 13.sp,
                            color = CharcoalSubtext
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 600.dp),
                    contentPadding = PaddingValues(bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    groupedTransactions.forEach { (dateHeader, transactions) ->
                        item(key = "header_$dateHeader") {
                            Text(
                                text = dateHeader,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = CharcoalSubtext,
                                modifier = Modifier.padding(top = 10.dp, bottom = 4.dp, start = 4.dp)
                            )
                        }

                        items(transactions, key = { it.id }) { item ->
                            TransactionItem(
                                transaction = item,
                                currencySymbol = currency.symbol,
                                onDeleteClick = { viewModel.deleteTransaction(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Category Filter Dialog
    if (showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { showCategoryDialog = false },
            title = {
                Text(
                    text = "Filter by Category",
                    fontWeight = FontWeight.Bold,
                    color = CharcoalText
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.filterCategory.value = "ALL"
                                    showCategoryDialog = false
                                },
                            color = if (filterCategory == "ALL") IncomeGreenLight else Color.Transparent
                        ) {
                            Text(
                                text = "All Categories",
                                modifier = Modifier.padding(12.dp),
                                fontWeight = FontWeight.SemiBold,
                                color = if (filterCategory == "ALL") IncomeGreenDark else CharcoalText
                            )
                        }
                    }
                    items(CategoryRepository.allCategories) { cat ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.filterCategory.value = cat.name
                                    showCategoryDialog = false
                                },
                            color = if (filterCategory == cat.name) IncomeGreenLight else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = cat.name,
                                    tint = cat.color,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = cat.name,
                                    color = if (filterCategory == cat.name) IncomeGreenDark else CharcoalText,
                                    fontWeight = if (filterCategory == cat.name) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCategoryDialog = false }) {
                    Text("Close", color = IncomeGreen)
                }
            }
        )
    }
}
