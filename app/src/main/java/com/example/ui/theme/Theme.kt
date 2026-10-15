package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = IncomeGreen,
    onPrimary = Color.White,
    primaryContainer = IncomeGreenLight,
    onPrimaryContainer = IncomeGreenDark,
    secondary = ExpenseCoral,
    onSecondary = Color.White,
    secondaryContainer = ExpenseCoralLight,
    onSecondaryContainer = ExpenseCoralDark,
    tertiary = CatBlue,
    background = SlateBackground,
    onBackground = CharcoalText,
    surface = SlateSurface,
    onSurface = CharcoalText,
    surfaceVariant = SlateSurfaceVariant,
    onSurfaceVariant = CharcoalSubtext,
    outline = SlateBorder,
    outlineVariant = SlateBorder.copy(alpha = 0.5f)
)

private val DarkColorScheme = darkColorScheme(
    primary = IncomeGreen,
    onPrimary = Color.White,
    primaryContainer = IncomeGreenDark,
    onPrimaryContainer = IncomeGreenLight,
    secondary = ExpenseCoral,
    onSecondary = Color.White,
    secondaryContainer = ExpenseCoralDark,
    onSecondaryContainer = ExpenseCoralLight,
    tertiary = CatBlue,
    background = Color(0xFF0F172A),
    onBackground = Color(0xFFF8FAFC),
    surface = Color(0xFF1E293B),
    onSurface = Color(0xFFF8FAFC),
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF475569)
)

@Composable
fun PennyWiseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Preserve custom fintech branding by default
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
