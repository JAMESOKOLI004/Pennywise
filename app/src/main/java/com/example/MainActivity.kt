package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.PennyWiseViewModel

enum class NavDestination(
    val title: String,
    val selectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val unselectedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val testTag: String
) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard, Icons.Outlined.Dashboard, "nav_dashboard"),
    TRANSACTIONS("Transactions", Icons.Default.ReceiptLong, Icons.Outlined.ReceiptLong, "nav_transactions"),
    ANALYTICS("Analytics", Icons.Default.BarChart, Icons.Outlined.BarChart, "nav_analytics"),
    BUDGETS("Budgets", Icons.Default.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet, "nav_budgets"),
    SETTINGS("Settings", Icons.Default.Settings, Icons.Outlined.Settings, "nav_settings")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PennyWiseTheme {
                PennyWiseApp()
            }
        }
    }
}

@Composable
fun PennyWiseApp() {
    val viewModel: PennyWiseViewModel = viewModel()
    var currentDestination by remember { mutableStateOf(NavDestination.DASHBOARD) }
    var isAddingTransaction by remember { mutableStateOf(false) }
    var analyticsInitialTab by remember { mutableIntStateOf(0) }

    BackHandler(enabled = isAddingTransaction || currentDestination != NavDestination.DASHBOARD) {
        if (isAddingTransaction) {
            isAddingTransaction = false
        } else {
            currentDestination = NavDestination.DASHBOARD
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("app_scaffold"),
        containerColor = SlateBackground,
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (!isAddingTransaction) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 6.dp,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.testTag("main_navigation_bar")
                ) {
                    NavDestination.entries.forEach { destination ->
                        val selected = currentDestination == destination
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (destination == NavDestination.ANALYTICS) {
                                    analyticsInitialTab = 0
                                }
                                currentDestination = destination
                            },
                            icon = {
                                Icon(
                                    imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                                    contentDescription = destination.title,
                                    tint = if (selected) IncomeGreen else CharcoalSubtext
                                )
                            },
                            label = {
                                Text(
                                    text = destination.title,
                                    color = if (selected) IncomeGreen else CharcoalSubtext
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = IncomeGreenLight
                            ),
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .testTag(destination.testTag)
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = isAddingTransaction,
                transitionSpec = {
                    if (targetState) {
                        slideInVertically { it } + fadeIn() togetherWith
                            slideOutVertically { -it / 3 } + fadeOut()
                    } else {
                        slideInVertically { -it / 3 } + fadeIn() togetherWith
                            slideOutVertically { it } + fadeOut()
                    }
                },
                label = "add_transaction_transition"
            ) { adding ->
                if (adding) {
                    AddTransactionScreen(
                        viewModel = viewModel,
                        onNavigateBack = { isAddingTransaction = false },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Crossfade(
                        targetState = currentDestination,
                        label = "screen_crossfade"
                    ) { destination ->
                        when (destination) {
                            NavDestination.DASHBOARD -> DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToAdd = { isAddingTransaction = true },
                                onNavigateToTransactions = { currentDestination = NavDestination.TRANSACTIONS },
                                onNavigateToCategoryBreakdown = {
                                    analyticsInitialTab = 0
                                    currentDestination = NavDestination.ANALYTICS
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                            NavDestination.TRANSACTIONS -> TransactionListScreen(
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                            NavDestination.ANALYTICS -> AnalyticsScreen(
                                viewModel = viewModel,
                                initialTab = analyticsInitialTab,
                                modifier = Modifier.fillMaxSize()
                            )
                            NavDestination.BUDGETS -> BudgetScreen(
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                            NavDestination.SETTINGS -> SettingsScreen(
                                viewModel = viewModel,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
