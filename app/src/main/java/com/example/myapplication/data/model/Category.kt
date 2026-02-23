package com.example.myapplication.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.myapplication.ui.theme.*

enum class ExpenseCategory(
    val displayName: String,
    val icon: ImageVector,
    val color: Color
) {
    FOOD("Еда", Icons.Default.Restaurant, FoodColor),
    TRANSPORT("Транспорт", Icons.Default.DirectionsCar, TransportColor),
    ENTERTAINMENT("Развлечения", Icons.Default.Movie, EntertainmentColor),
    SHOPPING("Покупки", Icons.Default.ShoppingCart, ShoppingColor),
    HEALTH("Здоровье", Icons.Default.LocalHospital, HealthColor),
    BILLS("Счета", Icons.Default.Receipt, BillsColor),
    OTHER("Другое", Icons.Default.MoreHoriz, OtherColor);

    companion object {
        fun fromString(value: String): ExpenseCategory {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.displayName == value }
                ?: OTHER
        }

        fun getAll(): List<ExpenseCategory> = entries.toList()
    }
}
