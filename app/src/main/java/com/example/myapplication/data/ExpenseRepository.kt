package com.example.myapplication.data

import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val dao: ExpenseDao) {
    val allExpenses: Flow<List<Expense>> = dao.getAll()
    suspend fun insert(expense: Expense) = dao.insert(expense)
    suspend fun update(expense: Expense) = dao.update(expense)
    suspend fun delete(expense: Expense) = dao.delete(expense)
    fun getByCategory(category: String): Flow<List<Expense>> = dao.getByCategory(category)
    fun search(query: String): Flow<List<Expense>> = dao.search(query)
    suspend fun getSum(start: Long, end: Long): Double = dao.getSum(start, end) ?: 0.0
}
