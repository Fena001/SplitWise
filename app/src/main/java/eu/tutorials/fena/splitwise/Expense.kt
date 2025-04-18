package eu.tutorials.fena.splitwise

import java.util.UUID

data class Expense(val id: String = UUID.randomUUID().toString(),
                   val title: String,
                   val amount: Double,
                   val paidBy: String,
                   val participants: List<String>,
                   val split: Map<String, Double>)

data class User(val name: String)
data class Settlement(val from: String, val to: String, val amount: Double)