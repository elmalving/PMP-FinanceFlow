package com.example.financeflow

import android.content.Context
import com.example.financeflow.db.FinanceDatabaseHelper
import com.example.financeflow.model.Category
import com.example.financeflow.model.Transaction
import com.example.financeflow.state.FinanceAppState
import org.junit.Assert.*
import org.junit.Test
import org.mockito.ArgumentMatchers.anyDouble
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever

class ExampleUnitTest {

    private fun createMockAppState(): FinanceAppState {
        val context = mock(Context::class.java)
        val dbHelper = mock(FinanceDatabaseHelper::class.java)
        
        val transactions = mutableListOf(
            Transaction(
                title = "Whole Foods Grocery",
                amount = 84.50,
                category = Category.FOOD,
                date = "2026-06-12",
                notes = "Weekly fresh grocery shopping"
            ),
            Transaction(
                title = "Shell Gas Station",
                amount = 45.00,
                category = Category.TRANSPORT,
                date = "2026-06-11",
                notes = "Refueled the sedan"
            ),
            Transaction(
                title = "Netflix Subscription",
                amount = 15.99,
                category = Category.ENTERTAINMENT,
                date = "2026-06-10",
                notes = "Standard HD monthly renewal"
            ),
            Transaction(
                title = "Power & Grid Co.",
                amount = 120.00,
                category = Category.BILLS,
                date = "2026-06-08",
                notes = "May electricity bill"
            ),
            Transaction(
                title = "Nike Shoes",
                amount = 110.00,
                category = Category.SHOPPING,
                date = "2026-06-05",
                notes = "Air Max running shoes on sale"
            ),
            Transaction(
                title = "Local Coffee Shop",
                amount = 7.50,
                category = Category.FOOD,
                date = "2026-06-14",
                notes = "Espresso and croissant"
            )
        )
        
        var budget = 1500.00
        
        whenever(dbHelper.getBudget(anyDouble())).thenAnswer {
            budget
        }
        whenever(dbHelper.getAllTransactions()).thenAnswer {
            transactions.sortedByDescending { it.date }
        }
        whenever(dbHelper.insertTransaction(anyOrNull())).thenAnswer { invocation ->
            val tx = invocation.getArgument<Transaction>(0)
            transactions.add(tx)
            1L
        }
        whenever(dbHelper.deleteTransaction(anyString())).thenAnswer { invocation ->
            val id = invocation.getArgument<String>(0)
            val removed = transactions.removeAll { it.id == id }
            if (removed) 1 else 0
        }
        whenever(dbHelper.saveBudget(anyDouble())).thenAnswer { invocation ->
            budget = invocation.getArgument<Double>(0)
            Unit
        }
        
        return FinanceAppState(context, dbHelper)
    }
    
    @Test
    fun testInitialAppState() {
        val appState = createMockAppState()
        
        // Assert initial pre-populated transactions exist
        assertTrue(appState.transactions.isNotEmpty())
        assertEquals(1500.00, appState.totalBudget, 0.001)
        
        // Verify default transactions total sum
        val expectedInitialSum = appState.transactions.sumOf { it.amount }
        assertEquals(expectedInitialSum, appState.getTotalSpent(), 0.001)
    }

    @Test
    fun testAddTransaction() {
        val appState = createMockAppState()
        val initialCount = appState.transactions.size
        val initialSpent = appState.getTotalSpent()
        
        // Add a new food transaction
        appState.addTransaction(
            title = "Test Coffee Shop",
            amount = 12.50,
            category = Category.FOOD,
            date = "2026-06-14",
            notes = "Test Note"
        )
        
        assertEquals(initialCount + 1, appState.transactions.size)
        assertEquals(initialSpent + 12.50, appState.getTotalSpent(), 0.001)
        
        // Check that the new transaction values are correct
        val added = appState.transactions.last()
        assertEquals("Test Coffee Shop", added.title)
        assertEquals(12.50, added.amount, 0.001)
        assertEquals(Category.FOOD, added.category)
        assertEquals("2026-06-14", added.date)
        assertEquals("Test Note", added.notes)
    }

    @Test
    fun testDeleteTransaction() {
        val appState = createMockAppState()
        
        // Add one unique transaction to delete
        appState.addTransaction(
            title = "Disposable Item",
            amount = 100.00,
            category = Category.SHOPPING,
            date = "2026-06-14",
            notes = "Will delete"
        )
        
        val addedTransaction = appState.transactions.last()
        val totalSpentWithAdded = appState.getTotalSpent()
        val countWithAdded = appState.transactions.size
        
        // Delete it
        appState.deleteTransaction(addedTransaction.id)
        
        assertEquals(countWithAdded - 1, appState.transactions.size)
        assertEquals(totalSpentWithAdded - 100.00, appState.getTotalSpent(), 0.001)
        assertFalse(appState.transactions.any { it.id == addedTransaction.id })
    }

    @Test
    fun testUpdateBudget() {
        val appState = createMockAppState()
        
        // Change budget to $2000
        appState.updateBudget(2000.00)
        assertEquals(2000.00, appState.totalBudget, 0.001)
        
        // Check remaining balance recalculation
        val expectedRemaining = 2000.00 - appState.getTotalSpent()
        assertEquals(expectedRemaining, appState.getRemainingBalance(), 0.001)
        
        // Check progress percentage calculations
        val expectedProgress = (appState.getTotalSpent() / 2000.00).toFloat()
        assertEquals(expectedProgress, appState.getBudgetUtilization(), 0.001f)
    }

    @Test
    fun testNegativeBudgetIgnored() {
        val appState = createMockAppState()
        val originalBudget = appState.totalBudget
        
        // Try setting negative budget
        appState.updateBudget(-50.00)
        
        // Budget should remain unchanged
        assertEquals(originalBudget, appState.totalBudget, 0.001)
    }

    @Test
    fun testSpentByCategoryAggregation() {
        val appState = createMockAppState()
        
        // Clear all (by deleting) to test pure category summation
        val ids = appState.transactions.map { it.id }
        ids.forEach { appState.deleteTransaction(it) }
        assertEquals(0.00, appState.getTotalSpent(), 0.001)
        
        // Add specific category items
        appState.addTransaction("Lunch A", 15.00, Category.FOOD, "2026-06-14", "")
        appState.addTransaction("Lunch B", 25.00, Category.FOOD, "2026-06-14", "")
        appState.addTransaction("Train Ticket", 4.50, Category.TRANSPORT, "2026-06-14", "")
        
        assertEquals(40.00, appState.getSpentByCategory(Category.FOOD), 0.001)
        assertEquals(4.50, appState.getSpentByCategory(Category.TRANSPORT), 0.001)
        assertEquals(0.00, appState.getSpentByCategory(Category.SHOPPING), 0.001)
    }
}
