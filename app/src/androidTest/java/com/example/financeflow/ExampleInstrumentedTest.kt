package com.example.financeflow

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.financeflow.db.FinanceDatabaseHelper
import com.example.financeflow.model.Category
import com.example.financeflow.model.Transaction
import com.example.financeflow.state.FinanceAppState
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    private lateinit var dbHelper: FinanceDatabaseHelper
    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        // Clean database table to ensure deterministic test state
        dbHelper = FinanceDatabaseHelper(appContext)
        val db = dbHelper.writableDatabase
        db.delete("transactions", null, null)
        db.delete("settings", null, null)
    }

    @After
    fun tearDown() {
        dbHelper.close()
        appContext.deleteDatabase("finance_flow.db")
    }

    @Test
    fun useAppContext() {
        // Context of the app under test.
        assertEquals("com.example.financeflow", appContext.packageName)
    }

    @Test
    fun testDatabaseHelperCRUD() {
        // Ensure database is empty initially
        assertTrue(dbHelper.getAllTransactions().isEmpty())

        // Test insert
        val transaction = Transaction(
            title = "Test Purchase",
            amount = 42.50,
            category = Category.SHOPPING,
            date = "2026-06-15",
            notes = "Test Note"
        )
        val insertId = dbHelper.insertTransaction(transaction)
        assertTrue(insertId != -1L)

        // Test read
        val list = dbHelper.getAllTransactions()
        assertEquals(1, list.size)
        val saved = list[0]
        assertEquals(transaction.id, saved.id)
        assertEquals("Test Purchase", saved.title)
        assertEquals(42.50, saved.amount, 0.001)
        assertEquals(Category.SHOPPING, saved.category)
        assertEquals("2026-06-15", saved.date)
        assertEquals("Test Note", saved.notes)

        // Test budget save and read
        dbHelper.saveBudget(1750.00)
        val savedBudget = dbHelper.getBudget(1500.00)
        assertEquals(1750.00, savedBudget, 0.001)

        // Test delete
        val deleteCount = dbHelper.deleteTransaction(transaction.id)
        assertEquals(1, deleteCount)
        assertTrue(dbHelper.getAllTransactions().isEmpty())
    }

    @Test
    fun testAppStateWithSQLite() {
        // Given a clean database setup, instantiate AppState
        dbHelper.saveBudget(2500.0)
        val transaction = Transaction(
            title = "Whole Foods Grocery",
            amount = 80.0,
            category = Category.FOOD,
            date = "2026-06-16",
            notes = "Weekly grocery"
        )
        dbHelper.insertTransaction(transaction)

        val appState = FinanceAppState(appContext)

        // Assert loads from database correctly
        assertEquals(2500.0, appState.totalBudget, 0.001)
        assertEquals(1, appState.transactions.size)
        assertEquals("Whole Foods Grocery", appState.transactions[0].title)

        // Test addTransaction updates SQLite
        appState.addTransaction(
            title = "Local Coffee Shop",
            amount = 5.50,
            category = Category.FOOD,
            date = "2026-06-17",
            notes = "Espresso"
        )

        assertEquals(2, appState.transactions.size)
        // Verify from database helper directly
        val dbList = dbHelper.getAllTransactions()
        assertEquals(2, dbList.size)
        assertTrue(dbList.any { it.title == "Local Coffee Shop" })

        // Test deleteTransaction updates SQLite
        val toDelete = appState.transactions.first { it.title == "Whole Foods Grocery" }
        appState.deleteTransaction(toDelete.id)

        assertEquals(1, appState.transactions.size)
        assertEquals("Local Coffee Shop", appState.transactions[0].title)
        assertEquals(1, dbHelper.getAllTransactions().size)

        // Test updateBudget updates SQLite
        appState.updateBudget(3000.0)
        assertEquals(3000.0, appState.totalBudget, 0.001)
        assertEquals(3000.0, dbHelper.getBudget(1500.0), 0.001)
    }
}
