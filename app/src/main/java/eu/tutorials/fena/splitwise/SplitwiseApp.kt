package eu.tutorials.fena.splitwise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState

@Composable
fun SplitwiseApp(currentUser: String, viewModel: SplitwiseViewModel = viewModel()) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var paidBy by remember { mutableStateOf("") }
    val participants = remember { mutableStateListOf<String>() }
    var newUser by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text("Add Expense", style = MaterialTheme.typography.titleLarge)
        }

        item {
            OutlinedTextField(
                value = newUser,
                onValueChange = { newUser = it },
                label = { Text("Add New User") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
        }

        item {
            Button(
                onClick = {
                    if (newUser.isNotBlank()) {
                        val trimmed = newUser.trim()
                        viewModel.addUser(trimmed)
                        if (paidBy.isBlank()) paidBy = trimmed
                        newUser = ""
                    }
                }
            ) {
                Text("Add User")
            }
        }

        item {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }

        item {
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
        }

        item {
            if (viewModel.users.isNotEmpty()) {
                DropdownMenuUserSelector(
                    label = "Paid By",
                    users = viewModel.users,
                    selected = paidBy,
                    onSelect = { paidBy = it }
                )
            } else {
                Text(
                    "Please add users to begin adding expenses.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        item {
            Text("Participants:")
        }

        items(viewModel.users) { user ->
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Checkbox(
                    checked = participants.contains(user),
                    onCheckedChange = {
                        if (it) participants.add(user) else participants.remove(user)
                    }
                )
                Text(user)
            }
        }

        item {
            Button(
                onClick = {
                    if (title.isNotBlank() && amount.toDoubleOrNull() != null && participants.isNotEmpty()) {
                        viewModel.addExpense(
                            title,
                            amount.toDouble(),
                            paidBy,
                            participants.toList()
                        )
                        title = ""
                        amount = ""
                        participants.clear()
                    }
                },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Add Expense")
            }
        }

        item {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Expenses", style = MaterialTheme.typography.titleMedium)
        }

        items(viewModel.expenses) { expense ->
            Text("${expense.title}: ₹${expense.amount} (Paid by ${expense.paidBy})")
        }

        item {
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Balances", style = MaterialTheme.typography.titleMedium)
        }

        items(viewModel.balances.value.entries.toList()) { entry ->
            val user = entry.key
            val balance = entry.value
            if (balance > 0) {
                Text("$user should receive ₹${String.format("%.2f", balance)}")
            } else if (balance < 0) {
                Text("$user owes ₹${String.format("%.2f", -balance)}")
            } else {
                Text("$user has a balanced settlement.")
            }
        }


        item {
            val settlements = viewModel.calculateSettlements()

            if (settlements.isNotEmpty()) {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Text("Who Pays Whom", style = MaterialTheme.typography.titleMedium)

                Column(modifier = Modifier.padding(top = 4.dp)) {
                    settlements.forEach { settlement ->
                        Text(
                            "${settlement.from} pays ₹${String.format("%.2f", settlement.amount)} to ${settlement.to}"
                        )
                    }
                }
            }
        }
    }
}
