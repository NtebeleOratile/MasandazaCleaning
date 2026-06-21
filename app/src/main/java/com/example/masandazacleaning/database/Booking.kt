package com.example.masandazacleaning.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val serviceType: String,
    val date: String,
    val time: String,
    val status: String = "Pending", // Pending, In Progress, Completed, Cancelled
    val price: Double
)
