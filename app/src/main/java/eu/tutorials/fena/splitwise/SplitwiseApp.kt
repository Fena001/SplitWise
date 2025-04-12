package eu.tutorials.fena.splitwise

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun SplitwiseApp(currentUser: String, viewModel: SplitwiseViewModel = viewModel()){
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var paidBy by remember { mutableStateOf(viewModel.users.first()) }
    val participants = remember { mutableStateListOf<String>() }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Add Expense", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )

        DropdownMenuUserSelector(label = "Paid By", users = viewModel.users, selected = paidBy, onSelect = { paidBy = it })

        Text("Participants:")
        viewModel.users.forEach { user ->
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

        Button(onClick = {
            if (title.isNotBlank() && amount.toDoubleOrNull() != null && participants.isNotEmpty()) {
                viewModel.addExpense(title, amount.toDouble(), paidBy, participants.toList())
                title = ""
                amount = ""
                participants.clear()
            }
        }, modifier = Modifier.padding(vertical = 8.dp)) {
            Text("Add Expense")
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Text("Expenses", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(viewModel.expenses.size) {
                val expense = viewModel.expenses[it]
                Text("${expense.title}: ₹${expense.amount} (Paid by ${expense.paidBy})")
            }
        }

        Divider(modifier = Modifier.padding(vertical = 8.dp))
        Text("Balances", style = MaterialTheme.typography.titleMedium)
        viewModel.balances.value.forEach { (user, balance) ->
            Text("$user: ₹${String.format("%.2f", balance)}")
        }
    }
}
