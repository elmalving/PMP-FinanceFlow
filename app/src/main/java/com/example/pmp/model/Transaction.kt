package com.example.pmp.model

import java.util.UUID

enum class Category(
    val displayName: String,
    val emoji: String,
    val colorHex: Long // ARGB color
) {
    FOOD("Food", "🍔", 0xFFFF9800),         // Orange
    TRANSPORT("Transport", "🚗", 0xFF03A9F4),    // Light Blue
    SHOPPING("Shopping", "🛍️", 0xFF9C27B0),     // Purple
    ENTERTAINMENT("Entertainment", "🎬", 0xFFE91E63), // Pink
    BILLS("Bills", "📄", 0xFF607D8B),         // Blue Gray
    OTHERS("Others", "🪙", 0xFF4CAF50)         // Green
}

data class Transaction(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val amount: Double,
    val category: Category,
    val date: String, // format: YYYY-MM-DD for simple sorting/storing
    val notes: String = ""
)
