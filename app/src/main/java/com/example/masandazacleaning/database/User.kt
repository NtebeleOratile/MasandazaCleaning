package com.example.masandazacleaning.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val email: String,
    val phone: String,
    val address: String,
    val password: String,
    val role: String = "customer" // "customer" or "staff"
)
