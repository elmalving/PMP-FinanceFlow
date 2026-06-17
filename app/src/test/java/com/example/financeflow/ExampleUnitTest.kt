package com.example.financeflow

import com.example.financeflow.model.Category
import com.example.financeflow.state.FinanceAppState
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    
    @Test
    fun testInitialAppState() {
        val appState = FinanceAppState()
        
        // Assert initial pre-populated transactions exist
        assertTrue(appState.transactions.isNotEmpty())
        assertEquals(1500.00, appState.totalBudget, 0.001)
        
        // Verify default transactions total sum
        val expectedInitialSum = appState.transactions.sumOf { it.amount }
        assertEquals(expectedInitialSum, appState.getTotalSpent(), 0.001)
    }

    @Test
    fun testAddTransaction() {
        val appState = FinanceAppState()
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
        val appState = FinanceAppState()
        
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
        val appState = FinanceAppState()
        
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
        val appState = FinanceAppState()
        val originalBudget = appState.totalBudget
        
        // Try setting negative budget
        appState.updateBudget(-50.00)
        
        // Budget should remain unchanged
        assertEquals(originalBudget, appState.totalBudget, 0.001)
    }

    @Test
    fun testSpentByCategoryAggregation() {
        val appState = FinanceAppState()
        
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
