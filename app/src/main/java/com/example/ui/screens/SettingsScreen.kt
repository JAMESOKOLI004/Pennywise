package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CurrencyOption
import com.example.data.model.CurrencyRepository
import com.example.ui.theme.*
import com.example.ui.viewmodel.PennyWiseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: PennyWiseViewModel,
    modifier: Modifier = Modifier
) {
    val currency by viewModel.currentCurrency.collectAsStateWithLifecycle()
    val budgetAlerts by viewModel.budgetAlertsEnabled.collectAsStateWithLifecycle()
    val dailyReminders by viewModel.dailyRemindersEnabled.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showExportSuccessMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier.testTag("settings_screen"),
        containerColor = SlateBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // General Preferences Section
            item {
                Text(
                    text = "Preferences",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalSubtext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .padding(start = 4.dp, bottom = 4.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Currency Selector Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCurrencyDialog = true }
                                .padding(16.dp)
                                .testTag("btn_select_currency"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(IncomeGreenLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AttachMoney,
                                        contentDescription = "Currency",
                                        tint = IncomeGreenDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Currency Symbol",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CharcoalText
                                    )
                                    Text(
                                        text = "${currency.name} (${currency.symbol})",
                                        fontSize = 12.sp,
                                        color = CharcoalSubtext
                                    )
                                }
                            }

                            Text(
                                text = currency.symbol,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = IncomeGreen
                            )
                        }
                    }
                }
            }

            // Notifications Section
            item {
                Text(
                    text = "Notifications",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalSubtext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .padding(start = 4.dp, bottom = 4.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Budget Alerts Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(ExpenseCoralLight),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.NotificationsActive,
                                        contentDescription = null,
                                        tint = ExpenseCoralDark,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Budget Limit Warnings",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CharcoalText
                                    )
                                    Text(
                                        text = "Warn when spending exceeds 80% or 100%",
                                        fontSize = 12.sp,
                                        color = CharcoalSubtext
                                    )
                                }
                            }
                            Switch(
                                checked = budgetAlerts,
                                onCheckedChange = { viewModel.togglePreference("budget_alerts", it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = IncomeGreen, checkedTrackColor = IncomeGreenLight),
                                modifier = Modifier.testTag("switch_budget_alerts")
                            )
                        }

                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        // Daily Reminder Toggle
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(CatBlue.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = null,
                                        tint = CatBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Daily Expense Reminder",
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CharcoalText
                                    )
                                    Text(
                                        text = "Gentle evening reminder to log expenses",
                                        fontSize = 12.sp,
                                        color = CharcoalSubtext
                                    )
                                }
                            }
                            Switch(
                                checked = dailyReminders,
                                onCheckedChange = { viewModel.togglePreference("daily_reminders", it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = IncomeGreen, checkedTrackColor = IncomeGreenLight),
                                modifier = Modifier.testTag("switch_daily_reminders")
                            )
                        }
                    }
                }
            }

            // Data Management (Export & Reset)
            item {
                Text(
                    text = "Data Management",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalSubtext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp)
                        .padding(start = 4.dp, bottom = 4.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Export CSV
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val csvContent = viewModel.getExportCsv()
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, csvContent)
                                        putExtra(Intent.EXTRA_TITLE, "PennyWise_Transactions.csv")
                                        type = "text/csv"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Export Transactions CSV"))
                                    showExportSuccessMessage = "Transactions exported to CSV"
                                }
                                .padding(16.dp)
                                .testTag("btn_export_csv"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(CatEmerald.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FileDownload,
                                    contentDescription = null,
                                    tint = CatEmerald,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Export Transactions (CSV)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CharcoalText
                                )
                                Text(
                                    text = "Share or save as spreadsheet file",
                                    fontSize = 12.sp,
                                    color = CharcoalSubtext
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        // Export JSON
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val jsonContent = viewModel.getExportJson()
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, jsonContent)
                                        putExtra(Intent.EXTRA_TITLE, "PennyWise_Backup.json")
                                        type = "application/json"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Export Backup JSON"))
                                    showExportSuccessMessage = "Transactions backup exported to JSON"
                                }
                                .padding(16.dp)
                                .testTag("btn_export_json"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(CatIndigo.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Backup,
                                    contentDescription = null,
                                    tint = CatIndigo,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Export Backup (JSON)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CharcoalText
                                )
                                Text(
                                    text = "Complete data backup format",
                                    fontSize = 12.sp,
                                    color = CharcoalSubtext
                                )
                            }
                        }

                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        // Reset / Reseed Data
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showResetDialog = true }
                                .padding(16.dp)
                                .testTag("btn_reset_sample_data"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(ExpenseCoralLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RestartAlt,
                                    contentDescription = null,
                                    tint = ExpenseCoralDark,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Reset to Sample Data",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ExpenseCoralDark
                                )
                                Text(
                                    text = "Re-populate dashboard with demo transactions",
                                    fontSize = 12.sp,
                                    color = CharcoalSubtext
                                )
                            }
                        }
                    }
                }
            }

            // About Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 600.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "PennyWise • v1.0.0",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Mobile-first personal finance with local privacy",
                            fontSize = 11.sp,
                            color = CharcoalSubtext
                        )
                    }
                }
            }
        }
    }

    // Currency Selector Dialog
    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = {
                Text(
                    text = "Select Default Currency",
                    fontWeight = FontWeight.Bold,
                    color = CharcoalText
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 350.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(CurrencyRepository.supportedCurrencies) { opt ->
                        val isSelected = opt.code == currency.code
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.setCurrency(opt.code)
                                    showCurrencyDialog = false
                                }
                                .testTag("currency_option_${opt.code}"),
                            color = if (isSelected) IncomeGreenLight else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = opt.name,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) IncomeGreenDark else CharcoalText
                                )
                                Text(
                                    text = opt.symbol,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) IncomeGreenDark else CharcoalSubtext
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text("Close", color = IncomeGreen)
                }
            }
        )
    }

    // Reset Data Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "Reset to Sample Data?",
                    fontWeight = FontWeight.Bold,
                    color = CharcoalText
                )
            },
            text = {
                Text(
                    text = "This will replace all current transactions with rich sample data so you can test all screens and charts.",
                    color = CharcoalSubtext,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetData()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseCoral),
                    modifier = Modifier.testTag("btn_confirm_reset_data")
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = CharcoalSubtext)
                }
            }
        )
    }
}
