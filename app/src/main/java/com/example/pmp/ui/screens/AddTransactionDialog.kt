package com.example.pmp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.pmp.model.Category
import com.example.pmp.state.FinanceAppState
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    appState: FinanceAppState,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountString by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(Category.FOOD) }
    var notes by remember { mutableStateOf("") }
    var dateString by remember { mutableStateOf(LocalDate.now().toString()) }

    var titleError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false // Enables full-screen / custom width sizing
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Log New Expense",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close Dialog")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input Field: Title/Vendor
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = it.trim().isEmpty()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Vendor / Description") },
                    placeholder = { Text("e.g., Starbucks Coffee") },
                    isError = titleError,
                    supportingText = {
                        if (titleError) {
                            Text("Description is required")
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Input Field: Amount
                OutlinedTextField(
                    value = amountString,
                    onValueChange = {
                        amountString = it
                        val parsed = it.toDoubleOrNull()
                        amountError = parsed == null || parsed <= 0
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Amount ($)") },
                    placeholder = { Text("0.00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amountError,
                    supportingText = {
                        if (amountError) {
                            Text("Please enter a valid amount greater than $0")
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section Label: Category Selector
                Text(
                    text = "Select Category",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Category Chips Selector
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(Category.values()) { category ->
                        val isSelected = selectedCategory == category
                        val categoryColor = Color(category.colorHex)
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) categoryColor.copy(alpha = 0.25f)
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                                )
                                .clickable { selectedCategory = category }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = category.emoji, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = category.displayName,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) categoryColor else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Input Field: Notes/Memos (Optional)
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Optional Notes") },
                    placeholder = { Text("e.g., lunch with team members") },
                    maxLines = 2,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Cancel
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }

                    // Save Expense
                    Button(
                        onClick = {
                            val cleanTitle = title.trim()
                            val parsedAmount = amountString.toDoubleOrNull()

                            val hasTitleError = cleanTitle.isEmpty()
                            val hasAmountError = parsedAmount == null || parsedAmount <= 0

                            titleError = hasTitleError
                            amountError = hasAmountError

                            if (!hasTitleError && !hasAmountError && parsedAmount != null) {
                                appState.addTransaction(
                                    title = cleanTitle,
                                    amount = parsedAmount,
                                    category = selectedCategory,
                                    date = dateString,
                                    notes = notes
                                )
                                onDismiss()
                            }
                        },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Log Expense", style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp))
                    }
                }
            }
        }
    }
}
