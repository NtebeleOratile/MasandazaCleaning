package com.example.masandazacleaning.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PaymentDao {
    @Insert
    suspend fun insertPayment(payment: Payment)

    @Query("SELECT * FROM payments WHERE userId = :userId")
    suspend fun getPaymentsForUser(userId: Int): List<Payment>
}
