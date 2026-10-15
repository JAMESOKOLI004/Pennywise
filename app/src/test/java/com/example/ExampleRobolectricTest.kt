package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.CategoryRepository
import com.example.data.model.CurrencyRepository
import com.example.data.model.TransactionType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("PennyWise", appName)
    }

    @Test
    fun `currency format test`() {
        val formatted = CurrencyRepository.formatAmount(1250.50, "$")
        assertEquals("$1,250.50", formatted)
    }

    @Test
    fun `category lookup returns correct items`() {
        val foodCat = CategoryRepository.getCategoryByName("Food & Dining")
        assertEquals(TransactionType.EXPENSE, foodCat.type)
        assertEquals("Food & Dining", foodCat.name)

        val salaryCat = CategoryRepository.getCategoryByName("Salary")
        assertEquals(TransactionType.INCOME, salaryCat.type)
    }
}
