package com.example.financeflow.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.financeflow.model.Category
import com.example.financeflow.state.FinanceAppState

@Composable
fun AnalyticsScreen(
    appState: FinanceAppState,
    modifier: Modifier = Modifier
) {
    val totalSpent = appState.getTotalSpent()

    // Calculate details for each category
    val categoryDetailsList = remember(appState.transactions, totalSpent) {
        Category.entries.map { category ->
            val spent = appState.getSpentByCategory(category)
            val percentage = if (totalSpent > 0) (spent / totalSpent).toFloat() else 0f
            CategorySpendingDetails(category, spent, percentage)
        }.sortedByDescending { it.spentAmount }
    }

    // Determine highest spending category for custom AI insights
    val highestCategory = remember(categoryDetailsList) {
        categoryDetailsList.firstOrNull { it.spentAmount > 0 }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            // Screen Header
            Text(
                text = "Spending Analytics",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Detailed breakdown of where your funds go",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }

        // Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Cumulative Expenses",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        Text(
                            text = String.format("$%.2f", totalSpent),
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }

        // Dynamic Smart Insights Card (Acts like an AI Assistant)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = "Insight Icon",
                            tint = Color(0xFFFFA000), // Golden Amber
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Smart Financial Insight",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val (insightTitle, insightText) = when {
                        totalSpent <= 0 -> {
                            Pair(
                                "No spending recorded yet!",
                                "Log your first expense today. Once you add transaction entries, our smart engine will analyze your patterns and give you customized saving tips!"
                            )
                        }
                        highestCategory == null -> {
                            Pair("Looking good!", "No recorded expenses. Your budget is 100% intact.")
                        }
                        highestCategory.category == Category.FOOD -> {
                            Pair(
                                "Dining & Food is your top expense",
                                "You spent $${String.format("%.2f", highestCategory.spentAmount)} on Food (${String.format("%.1f", highestCategory.percentage * 100)}% of total). Meal-planning or limiting food deliveries this week could save you up to 30%!"
                            )
                        }
                        highestCategory.category == Category.SHOPPING -> {
                            Pair(
                                "Shopping expenses are elevated",
                                "Shopping accounts for ${String.format("%.1f", highestCategory.percentage * 100)}% of your outgoings. Try implementing the '24-Hour Rule': wait 24 hours before checking out to avoid impulse buys!"
                            )
                        }
                        highestCategory.category == Category.ENTERTAINMENT -> {
                            Pair(
                                "Entertainment & Fun budget note",
                                "You're investing actively in experiences. If you're looking to trim down, check for unused video, music, or gaming subscriptions which silently drain your wallet."
                            )
                        }
                        highestCategory.category == Category.BILLS -> {
                            Pair(
                                "Fixed Bills are your main driver",
                                "Bills make up a substantial share of spending. While mostly fixed, see if you can shop around for cheaper utility providers, insurance rates, or mobile plans to save in the long-term."
                            )
                        }
                        else -> {
                            Pair(
                                "Excellent budget distribution!",
                                "Your spending is balanced. Continue tracking your daily expenses, and try to tuck away at least 15% of your monthly remaining balance into a high-yield savings account."
                            )
                        }
                    }

                    Text(
                        text = insightTitle,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = insightText,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                    )
                }
            }
        }

        // Section Title: Category Breakdown
        item {
            Text(
                text = "Category Distribution",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Category bar list
        if (totalSpent <= 0) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No analytics data. Log some transactions first!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        } else {
            items(categoryDetailsList) { details ->
                CategoryBarRow(details = details)
            }
        }
    }
}

data class CategorySpendingDetails(
    val category: Category,
    val spentAmount: Double,
    val percentage: Float
)

@Composable
fun CategoryBarRow(
    details: CategorySpendingDetails,
    modifier: Modifier = Modifier
) {
    val categoryColor = Color(details.category.colorHex)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Name & Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(categoryColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = details.category.emoji, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = details.category.displayName,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Spent Amount & Percentage
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = String.format("$%.2f", details.spentAmount),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = String.format("%.1f%%", details.percentage * 100),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Custom Procedural Distribution Bar
            LinearProgressIndicator(
                progress = { details.percentage },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = categoryColor,
                trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
            )
        }
    }
}
