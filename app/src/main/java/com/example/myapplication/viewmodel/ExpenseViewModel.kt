package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Expense
import com.example.myapplication.data.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.Calendar

class ExpenseViewModel(private val repo: ExpenseRepository) : ViewModel() {
    val expenses: Flow<List<Expense>> = repo.allExpenses

    fun addExpense(expense: Expense) = viewModelScope.launch { repo.insert(expense) }
    fun editExpense(expense: Expense) = viewModelScope.launch { repo.update(expense) }
    fun deleteExpense(expense: Expense) = viewModelScope.launch { repo.delete(expense) }

    suspend fun getDailyStats(): Double {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        val start = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        val end = cal.timeInMillis
        return repo.getSum(start, end)
    }

    suspend fun getMonthlyStats(): Double {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        val end = cal.timeInMillis - 1
        return repo.getSum(start, end)
    }

    suspend fun checkLimit(monthlyLimit: Double, sendNotification: (String) -> Unit) {
        val monthlySum = getMonthlyStats()
        if (monthlySum > monthlyLimit) {
            sendNotification("Превышен месячный лимит расходов!")
        }
    }
}
