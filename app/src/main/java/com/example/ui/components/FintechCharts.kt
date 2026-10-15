package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrencyRepository
import com.example.ui.theme.*

@Composable
fun ComparisonBarChart(
    income: Double,
    expense: Double,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val maxVal = maxOf(income, expense, 100.0)
    val incomeHeightRatio = (income / maxVal).toFloat().coerceIn(0.05f, 1f)
    val expenseHeightRatio = (expense / maxVal).toFloat().coerceIn(0.05f, 1f)

    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(income, expense) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, tween(700, easing = FastOutSlowInEasing))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("comparison_bar_chart"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            // Income Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = CurrencyRepository.formatAmount(income, currencySymbol),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = IncomeGreen
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .fillMaxHeight(incomeHeightRatio * animProgress.value)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(IncomeGreen, IncomeGreenDark)
                            )
                        )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Income",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CharcoalText
                )
            }

            // Expense Column
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = CurrencyRepository.formatAmount(expense, currencySymbol),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ExpenseCoral
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .width(44.dp)
                        .fillMaxHeight(expenseHeightRatio * animProgress.value)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(ExpenseCoral, ExpenseCoralDark)
                            )
                        )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Expense",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CharcoalText
                )
            }
        }
    }
}

data class DailySpendPoint(
    val day: Int,
    val amount: Double
)

@Composable
fun SpendingLineChart(
    points: List<DailySpendPoint>,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val animProgress = remember { Animatable(0f) }
    LaunchedEffect(points) {
        animProgress.snapTo(0f)
        animProgress.animateTo(1f, tween(800, easing = FastOutSlowInEasing))
    }

    val maxAmount = (points.maxOfOrNull { it.amount } ?: 100.0).coerceAtLeast(50.0)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("spending_line_chart")
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            val width = size.width
            val height = size.height

            // Horizontal grid lines
            val gridLines = 3
            for (i in 0..gridLines) {
                val y = height * (i.toFloat() / gridLines)
                drawLine(
                    color = Color(0xFFE2E8F0),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            if (points.size >= 2) {
                val stepX = width / (points.size - 1)
                val linePath = Path()
                val fillPath = Path()

                points.forEachIndexed { index, pt ->
                    val x = index * stepX
                    val normalizedVal = (pt.amount / maxAmount).toFloat().coerceIn(0f, 1f)
                    val y = height - (normalizedVal * height * animProgress.value)

                    if (index == 0) {
                        linePath.moveTo(x, y)
                        fillPath.moveTo(x, height)
                        fillPath.lineTo(x, y)
                    } else {
                        // Smooth cubic curve
                        val prevX = (index - 1) * stepX
                        val prevNorm = (points[index - 1].amount / maxAmount).toFloat().coerceIn(0f, 1f)
                        val prevY = height - (prevNorm * height * animProgress.value)

                        val cx1 = prevX + (x - prevX) / 2
                        val cy1 = prevY
                        val cx2 = prevX + (x - prevX) / 2
                        val cy2 = y

                        linePath.cubicTo(cx1, cy1, cx2, cy2, x, y)
                        fillPath.cubicTo(cx1, cy1, cx2, cy2, x, y)
                    }

                    if (index == points.size - 1) {
                        fillPath.lineTo(x, height)
                        fillPath.close()
                    }
                }

                // Draw gradient fill
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            ExpenseCoral.copy(alpha = 0.28f),
                            ExpenseCoral.copy(alpha = 0.02f)
                        ),
                        startY = 0f,
                        endY = height
                    )
                )

                // Draw line stroke
                drawPath(
                    path = linePath,
                    color = ExpenseCoral,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Draw point markers
                points.forEachIndexed { index, pt ->
                    if (pt.amount > 0) {
                        val x = index * stepX
                        val normalizedVal = (pt.amount / maxAmount).toFloat().coerceIn(0f, 1f)
                        val y = height - (normalizedVal * height * animProgress.value)

                        drawCircle(
                            color = Color.White,
                            radius = 5.dp.toPx(),
                            center = Offset(x, y)
                        )
                        drawCircle(
                            color = ExpenseCoral,
                            radius = 3.5.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
            }
        }

        // X-axis Day labels
        if (points.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val labelIndices = listOf(0, points.size / 4, points.size / 2, (3 * points.size) / 4, points.size - 1)
                labelIndices.distinct().forEach { idx ->
                    if (idx < points.size) {
                        Text(
                            text = "Day ${points[idx].day}",
                            fontSize = 11.sp,
                            color = CharcoalSubtext,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
