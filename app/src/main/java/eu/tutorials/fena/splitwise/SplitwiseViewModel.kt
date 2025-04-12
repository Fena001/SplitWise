package eu.tutorials.fena.splitwise

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class SplitwiseViewModel : ViewModel(){
    private val _users = mutableStateListOf("Fena", "Zimmy", "Jiya")
    val users: List<String> get() = _users

    private val _expenses = mutableStateListOf<Expense>()
    val expenses: List<Expense> get() = _expenses

    val balances = derivedStateOf {
        val map = mutableMapOf<String, Double>()
        users.forEach { map[it] = 0.0 }

        expenses.forEach { expense ->
            val paidBy = expense.paidBy
            map[paidBy] = map[paidBy]!! + expense.amount
            expense.split.forEach { (user, share) ->
                map[user] = map[user]!! - share
            }
        }

        map
    }

    fun addExpense(title: String, amount: Double, paidBy: String, participants: List<String>) {
        val share = amount / participants.size
        val splitMap = participants.associateWith { share }
        val expense = Expense(title = title, amount = amount, paidBy = paidBy, participants = participants, split = splitMap)
        _expenses.add(expense)
    }
}