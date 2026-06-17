package com.example.financeflow.state

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.financeflow.db.FinanceDatabaseHelper
import com.example.financeflow.model.Category
import com.example.financeflow.model.Transaction
import java.time.LocalDate

enum class Screen {
    DASHBOARD,
    LEDGER,
    ANALYTICS,
    SETTINGS
}

class FinanceAppState(
    private val context: Context,
    private val dbHelper: FinanceDatabaseHelper = FinanceDatabaseHelper(context)
) {
    // Current Active Tab Screen
    var currentScreen by mutableStateOf(Screen.DASHBOARD)

    // User Configured Monthly Budget
    var totalBudget by mutableDoubleStateOf(1500.00)

    // Observable transaction list
    private val _transactions = mutableStateListOf<Transaction>()
    val transactions: List<Transaction> get() = _transactions

    init {
        totalBudget = dbHelper.getBudget(1500.00)
        _transactions.addAll(dbHelper.getAllTransactions())
    }

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
        
        val newTx = Transaction(
            title = cleanTitle,
            amount = amount,
            category = category,
            date = cleanDate,
            notes = cleanNotes
        )

        dbHelper.insertTransaction(newTx)
        _transactions.clear()
        _transactions.addAll(dbHelper.getAllTransactions())
    }

    // Action: Delete a transaction
    fun deleteTransaction(id: String) {
        dbHelper.deleteTransaction(id)
        _transactions.removeAll { it.id == id }
    }

    // Action: Set custom monthly budget
    fun updateBudget(newBudget: Double) {
        if (newBudget >= 0) {
            totalBudget = newBudget
            dbHelper.saveBudget(newBudget)
        }
    }
}
