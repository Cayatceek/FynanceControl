package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)
    @Update
    suspend fun update(expense: Expense)
    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAll(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE category = :category")
    fun getByCategory(category: String): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE description LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<Expense>>

    @Query("SELECT SUM(amount) FROM expenses WHERE date >= :start AND date <= :end")
    suspend fun getSum(start: Long, end: Long): Double?
}
