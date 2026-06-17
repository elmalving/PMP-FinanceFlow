package com.example.financeflow.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.financeflow.model.Category
import com.example.financeflow.model.Transaction
import java.time.LocalDate

enum class Screen {
    DASHBOARD,
    LEDGER,
    ANALYTICS,
    SETTINGS
}

class FinanceAppState {
    // Current Active Tab Screen
    var currentScreen by mutableStateOf(Screen.DASHBOARD)

    // User Configured Monthly Budget
    var totalBudget by mutableDoubleStateOf(1500.00)

    // Observable transaction list
    private val _transactions = mutableStateListOf(
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
    val transactions: List<Transaction> get() = _transactions

    // Dialog trigger state
    var showAddDialog by mutableStateOf(false)

    // Helper: Total money spent
    fun getTotalSpent(): Double {
        return _transactions.sumOf { it.amount }
    }

    // Helper: Money spent in a specific category
    fun getSpentByCategory(category: Category): Double {
        return _transactions.filter { it.category == category }.sumOf { it.amount }
    }

    // Helper: Budget utilization percentage (0.0 to 1.0+)
    fun getBudgetUtilization(): Float {
        if (totalBudget <= 0) return 0f
        return (getTotalSpent() / totalBudget).toFloat()
    }

    // Helper: Remaining balance
    fun getRemainingBalance(): Double {
        return totalBudget - getTotalSpent()
    }

    // Helper: Recent Transactions sorted by date desc
    fun getRecentTransactions(limit: Int = 3): List<Transaction> {
        return _transactions.sortedByDescending { it.date }.take(limit)
    }

    // Action: Add a new transaction
    fun addTransaction(title: String, amount: Double, category: Category, date: String, notes: String) {
        val cleanTitle = title.trim().ifEmpty { "Expense" }
        val cleanNotes = notes.trim()
        val cleanDate = date.trim().ifEmpty { LocalDate.now().toString() }
        
        _transactions.add(
            Transaction(
                title = cleanTitle,
                amount = amount,
                category = category,
                date = cleanDate,
                notes = cleanNotes
            )
        )
    }

    // Action: Delete a transaction
    fun deleteTransaction(id: String) {
        _transactions.removeAll { it.id == id }
    }

    // Action: Set custom monthly budget
    fun updateBudget(newBudget: Double) {
        if (newBudget >= 0) {
            totalBudget = newBudget
        }
    }
}
