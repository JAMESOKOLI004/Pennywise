package com.example.data.model

data class CurrencyOption(
    val code: String,
    val symbol: String,
    val name: String
)

object CurrencyRepository {
    val supportedCurrencies = listOf(
        CurrencyOption("USD", "$", "US Dollar ($)"),
        CurrencyOption("EUR", "€", "Euro (€)"),
        CurrencyOption("GBP", "£", "British Pound (£)"),
        CurrencyOption("CAD", "CA$", "Canadian Dollar (CA$)"),
        CurrencyOption("AUD", "AU$", "Australian Dollar (AU$)"),
        CurrencyOption("JPY", "¥", "Japanese Yen (¥)"),
        CurrencyOption("INR", "₹", "Indian Rupee (₹)"),
        CurrencyOption("NGN", "₦", "Nigerian Naira (₦)"),
        CurrencyOption("BRL", "R$", "Brazilian Real (R$)")
    )

    fun getCurrency(code: String): CurrencyOption {
        return supportedCurrencies.find { it.code == code } ?: supportedCurrencies.first()
    }

    fun formatAmount(amount: Double, symbol: String): String {
        return String.format("%s%,.2f", symbol, amount)
    }
}
