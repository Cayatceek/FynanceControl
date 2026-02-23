package com.example.myapplication.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.entity.Expense
import com.example.myapplication.data.repository.Currencies
import com.example.myapplication.data.repository.ExpenseRepository
import com.example.myapplication.data.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Calendar

class ExpenseViewModel(
    private val repo: ExpenseRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    val expenses: Flow<List<Expense>> = repo.allExpenses

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    private val _selectedCategory = MutableStateFlow<String?>(null)
    val selectedCategory: StateFlow<String?> = _selectedCategory

    val filteredExpenses: Flow<List<Expense>> = combine(
        expenses,
        _searchQuery,
        _selectedCategory
    ) { expenseList, query, category ->
        expenseList.filter { expense ->
            val matchesQuery = query.isEmpty() ||
                    expense.description.contains(query, ignoreCase = true) ||
                    expense.category.contains(query, ignoreCase = true)
            val matchesCategory = category == null || expense.category == category
            matchesQuery && matchesCategory
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    fun addExpense(expense: Expense, onLimitExceeded: ((String) -> Unit)? = null) = viewModelScope.launch {
        repo.insert(expense)
        onLimitExceeded?.let {
            checkLimit(it)
        }
    }

    fun editExpense(expense: Expense, onLimitExceeded: ((String) -> Unit)? = null) = viewModelScope.launch {
        repo.update(expense)
        onLimitExceeded?.let {
            checkLimit(it)
        }
    }

    fun deleteExpense(expense: Expense) = viewModelScope.launch { repo.delete(expense) }

    suspend fun getDailyStats(): Double {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val end = cal.timeInMillis
        return repo.getSum(start, end)
    }

    suspend fun getMonthlyStats(): Double {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val end = cal.timeInMillis - 1
        return repo.getSum(start, end)
    }

    // Settings
    val currency = settingsRepository.currency
    val monthlyLimit = settingsRepository.monthlyLimit
    val dailyLimit = settingsRepository.dailyLimit
    val currentCurrencyInfo = currency.map { Currencies.getByCode(it) }

    fun setCurrency(currency: String) = viewModelScope.launch {
        settingsRepository.setCurrency(currency)
    }

    fun setMonthlyLimit(limit: Double) = viewModelScope.launch {
        settingsRepository.setMonthlyLimit(limit)
    }

    fun setDailyLimit(limit: Double) = viewModelScope.launch {
        settingsRepository.setDailyLimit(limit)
    }

    private suspend fun checkLimit(sendNotification: (String) -> Unit) {
        val monthlySum = getMonthlyStats()
        val limit = monthlyLimit.first()
        val currencyInfo = currentCurrencyInfo.first()
        if (monthlySum > limit) {
            sendNotification("Превышен месячный лимит расходов! Сумма: ${monthlySum.toInt()} ${currencyInfo.symbol}, Лимит: ${limit.toInt()} ${currencyInfo.symbol}")
        }
    }

    fun getCategoryStats(): Flow<Map<String, Double>> = repo.allExpenses.map { expenses ->
        expenses.groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amount } }
    }

    // Получение статистики по дням для графика
    fun getDailyExpensesForChart(): Flow<Map<String, Double>> = repo.allExpenses.map { expenses ->
        val calendar = Calendar.getInstance()
        val dateFormat = java.text.SimpleDateFormat("dd.MM", java.util.Locale.getDefault())

        expenses.groupBy { expense ->
            calendar.time = expense.date
            dateFormat.format(calendar.time)
        }.mapValues { (_, list) -> list.sumOf { it.amount } }
    }

    // Получение статистики по категориям для графика
    fun getCategoryExpensesForChart(): Flow<Map<String, Double>> = repo.allExpenses.map { expenses ->
        expenses.groupBy { it.category }
            .mapValues { (_, list) -> list.sumOf { it.amount } }
    }
}
