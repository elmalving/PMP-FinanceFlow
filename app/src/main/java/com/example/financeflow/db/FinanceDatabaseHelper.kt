package com.example.financeflow.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.financeflow.model.Category
import com.example.financeflow.model.Transaction

class FinanceDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "main.db"
        private const val DATABASE_VERSION = 2 // Support for settings table

        // Transactions Table
        private const val TABLE_TRANSACTIONS = "transactions"
        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_AMOUNT = "amount"
        private const val KEY_CATEGORY = "category"
        private const val KEY_DATE = "date"
        private const val KEY_NOTES = "notes"

        // Settings Table
        private const val TABLE_SETTINGS = "settings"
        private const val KEY_SETTING_KEY = "key"
        private const val KEY_SETTING_VALUE = "value"
        private const val KEY_BUDGET = "total_budget"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTransactionsTable = ("CREATE TABLE " + TABLE_TRANSACTIONS + "("
                + KEY_ID + " TEXT PRIMARY KEY,"
                + KEY_TITLE + " TEXT,"
                + KEY_AMOUNT + " REAL,"
                + KEY_CATEGORY + " TEXT,"
                + KEY_DATE + " TEXT,"
                + KEY_NOTES + " TEXT" + ")")
        db.execSQL(createTransactionsTable)

        val createSettingsTable = ("CREATE TABLE " + TABLE_SETTINGS + "("
                + KEY_SETTING_KEY + " TEXT PRIMARY KEY,"
                + KEY_SETTING_VALUE + " TEXT" + ")")
        db.execSQL(createSettingsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRANSACTIONS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SETTINGS")
        onCreate(db)
    }

    fun getAllTransactions(): List<Transaction> {
        val transactions = mutableListOf<Transaction>()
        val selectQuery = "SELECT * FROM $TABLE_TRANSACTIONS ORDER BY $KEY_DATE DESC"
        val db = this.readableDatabase
        val cursor = db.rawQuery(selectQuery, null)

        if (cursor.moveToFirst()) {
            val idIndex = cursor.getColumnIndex(KEY_ID)
            val titleIndex = cursor.getColumnIndex(KEY_TITLE)
            val amountIndex = cursor.getColumnIndex(KEY_AMOUNT)
            val categoryIndex = cursor.getColumnIndex(KEY_CATEGORY)
            val dateIndex = cursor.getColumnIndex(KEY_DATE)
            val notesIndex = cursor.getColumnIndex(KEY_NOTES)

            do {
                val id = if (idIndex >= 0) cursor.getString(idIndex) else ""
                val title = if (titleIndex >= 0) cursor.getString(titleIndex) else ""
                val amount = if (amountIndex >= 0) cursor.getDouble(amountIndex) else 0.0
                val categoryStr = if (categoryIndex >= 0) cursor.getString(categoryIndex) else "OTHERS"
                val date = if (dateIndex >= 0) cursor.getString(dateIndex) else ""
                val notes = if (notesIndex >= 0) cursor.getString(notesIndex) else ""

                val category = try {
                    Category.valueOf(categoryStr)
                } catch (e: IllegalArgumentException) {
                    Category.OTHERS
                }

                transactions.add(Transaction(id, title, amount, category, date, notes))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return transactions
    }

    fun insertTransaction(transaction: Transaction): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_ID, transaction.id)
            put(KEY_TITLE, transaction.title)
            put(KEY_AMOUNT, transaction.amount)
            put(KEY_CATEGORY, transaction.category.name)
            put(KEY_DATE, transaction.date)
            put(KEY_NOTES, transaction.notes)
        }
        return db.insert(TABLE_TRANSACTIONS, null, contentValues)
    }

    fun deleteTransaction(id: String): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_TRANSACTIONS, "$KEY_ID = ?", arrayOf(id))
    }

    fun getBudget(defaultBudget: Double): Double {
        val db = this.readableDatabase
        var budget = defaultBudget
        try {
            val cursor = db.query(
                TABLE_SETTINGS,
                arrayOf(KEY_SETTING_VALUE),
                "$KEY_SETTING_KEY = ?",
                arrayOf(KEY_BUDGET),
                null, null, null
            )
            if (cursor.moveToFirst()) {
                val valueIndex = cursor.getColumnIndex(KEY_SETTING_VALUE)
                if (valueIndex >= 0) {
                    val valueStr = cursor.getString(valueIndex)
                    budget = valueStr.toDoubleOrNull() ?: defaultBudget
                }
            }
            cursor.close()
        } catch (e: Exception) {
            // Fallback
        }
        return budget
    }

    fun saveBudget(budget: Double) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_SETTING_KEY, KEY_BUDGET)
            put(KEY_SETTING_VALUE, budget.toString())
        }
        db.replace(TABLE_SETTINGS, null, contentValues)
    }
}
