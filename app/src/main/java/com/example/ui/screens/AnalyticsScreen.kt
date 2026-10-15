package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.ui.theme.*
import com.example.ui.viewmodel.PennyWiseViewModel

@Composable
fun AnalyticsScreen(
    viewModel: PennyWiseViewModel,
    initialTab: Int = 0,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBackground)
            .testTag("analytics_screen")
    ) {
        // Tab Selector Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .height(44.dp)
                    .testTag("analytics_tab_selector"),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    // Category Breakdown Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(3.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selectedTab == 0) Color.White else Color.Transparent)
                            .clickable { selectedTab = 0 }
                            .testTag("tab_categories"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Categories",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedTab == 0) IncomeGreen else CharcoalSubtext
                        )
                    }

                    // Monthly Report Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(3.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (selectedTab == 1) Color.White else Color.Transparent)
                            .clickable { selectedTab = 1 }
                            .testTag("tab_monthly_report"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Monthly Report",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (selectedTab == 1) IncomeGreen else CharcoalSubtext
                        )
                    }
                }
            }
        }

        // Tab Content
        if (selectedTab == 0) {
            CategoryBreakdownScreen(
                viewModel = viewModel,
                onNavigateBack = { selectedTab = 1 },
                modifier = Modifier.weight(1f)
            )
        } else {
            MonthlyReportScreen(
                viewModel = viewModel,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
