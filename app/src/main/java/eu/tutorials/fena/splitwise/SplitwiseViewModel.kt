package eu.tutorials.fena.splitwise

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class SplitwiseViewModel : ViewModel(){
    private val _users = mutableStateListOf<String>()
    val users: List<String> get() = _users

    private val _expenses = mutableStateListOf<Expense>()
    val expenses: List<Expense> get() = _expenses

    val balances = derivedStateOf {
        val map = mutableMapOf<String, Double>()
        users.forEach { map[it] = 0.0 }

        expenses.forEach { expense ->
            val paidBy = expense.paidBy
            val totalAmount = expense.amount
            val perPersonShare = totalAmount / expense.participants.size

            // Each participant owes perPersonShare
            expense.participants.forEach { participant ->
                if (participant == paidBy) {
                    map[participant] = map[participant]!! + (totalAmount - perPersonShare)
                } else {
                    map[participant] = map[participant]!! - perPersonShare
                }
            }
        }
        println("📊 Balances:")
        map.forEach { println("${it.key}: ₹${String.format("%.2f", it.value)}") }

        map
    }


    fun addExpense(title: String, amount: Double, paidBy: String, participants: List<String>) {
        val share = amount / participants.size
        val splitMap = participants.associateWith { share }
        val expense = Expense(title = title, amount = amount, paidBy = paidBy, participants = participants, split = splitMap)
        _expenses.add(expense)
    }

    fun addUser(user: String) {
        if (user.isNotBlank() && !_users.contains(user)) {
            _users.add(user)
        }
    }

    fun calculateSettlements(): List<Settlement> {
        val balancesMap = balances.value.toMutableMap()
        val settlements = mutableListOf<Settlement>()

        println("📊 Balances:")
        balances.value.forEach { println("${it.key}: ₹${String.format("%.2f", it.value)}") }


        val creditors = balancesMap.filter { it.value > 0 }.toList().toMutableList()
        val debtors = balancesMap.filter { it.value < 0 }.toList().toMutableList()

        var creditorIndex = 0
        var debtorIndex = 0

        while (creditorIndex < creditors.size && debtorIndex < debtors.size) {
            val (creditor, creditAmount) = creditors[creditorIndex]
            val (debtor, debtAmount) = debtors[debtorIndex]

            val settlementAmount = minOf(creditAmount, -debtAmount)

            settlements.add(Settlement(debtor, creditor, settlementAmount))

            balancesMap[creditor] = creditAmount - settlementAmount
            balancesMap[debtor] = debtAmount + settlementAmount

            if (balancesMap[creditor]?.let { it < 0.01 } == true) creditorIndex++
            if (balancesMap[debtor] == 0.0) debtorIndex++
            else debtors[debtorIndex] = debtor to balancesMap[debtor]!!
        }

        return settlements
    }


    fun getUserTransactions(currentUser: String): Pair<List<Settlement>, List<Settlement>> {
        val settlements = calculateSettlements()
        val receiveList = settlements.filter { it.to == currentUser }
        val oweList = settlements.filter { it.from == currentUser }
        return Pair(receiveList, oweList)
    }

}