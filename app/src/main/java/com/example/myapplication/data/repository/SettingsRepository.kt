package com.example.myapplication.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    companion object {
        val CURRENCY_KEY = stringPreferencesKey("currency")
        val MONTHLY_LIMIT_KEY = doublePreferencesKey("monthly_limit")
        val DAILY_LIMIT_KEY = doublePreferencesKey("daily_limit")

        const val DEFAULT_CURRENCY = "UZS" // Узбекский сум по умолчанию
        const val DEFAULT_MONTHLY_LIMIT = 1000000.0 // 1 млн сум
        const val DEFAULT_DAILY_LIMIT = 50000.0 // 50 тыс сум
    }

    val currency: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[CURRENCY_KEY] ?: DEFAULT_CURRENCY
        }

    val monthlyLimit: Flow<Double> = context.dataStore.data
        .map { preferences ->
            preferences[MONTHLY_LIMIT_KEY] ?: DEFAULT_MONTHLY_LIMIT
        }

    val dailyLimit: Flow<Double> = context.dataStore.data
        .map { preferences ->
            preferences[DAILY_LIMIT_KEY] ?: DEFAULT_DAILY_LIMIT
        }

    suspend fun setCurrency(currency: String) {
        context.dataStore.edit { preferences ->
            preferences[CURRENCY_KEY] = currency
        }
    }

    suspend fun setMonthlyLimit(limit: Double) {
        context.dataStore.edit { preferences ->
            preferences[MONTHLY_LIMIT_KEY] = limit
        }
    }

    suspend fun setDailyLimit(limit: Double) {
        context.dataStore.edit { preferences ->
            preferences[DAILY_LIMIT_KEY] = limit
        }
    }
}

// Данные о валютах
data class CurrencyInfo(
    val code: String,
    val name: String,
    val symbol: String,
    val flag: String
)

object Currencies {
    val ALL = listOf(
        CurrencyInfo("UZS", "Узбекский сум", "soʻm", "🇺🇿"),
        CurrencyInfo("RUB", "Российский рубль", "₽", "🇷🇺"),
        CurrencyInfo("USD", "Доллар США", "$", "🇺🇸"),
        CurrencyInfo("EUR", "Евро", "€", "🇪🇺"),
        CurrencyInfo("KZT", "Казахстанский тенге", "₸", "🇰🇿"),
        CurrencyInfo("GBP", "Фунт стерлингов", "£", "🇬🇧"),
        CurrencyInfo("JPY", "Японская иена", "¥", "🇯🇵"),
        CurrencyInfo("CNY", "Китайский юань", "¥", "🇨🇳")
    )

    fun getByCode(code: String): CurrencyInfo {
        return ALL.find { it.code == code } ?: ALL.first()
    }
}
