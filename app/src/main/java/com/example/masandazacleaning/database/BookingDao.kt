package com.example.masandazacleaning.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface BookingDao {
    @Insert
    suspend fun insertBooking(booking: Booking)

    @Query("SELECT * FROM bookings WHERE userId = :userId")
    suspend fun getBookingsForUser(userId: Int): List<Booking>

    @Query("SELECT * FROM bookings")
    suspend fun getAllBookings(): List<Booking>

    @Query("SELECT * FROM bookings WHERE id = :id")
    suspend fun getBookingById(id: Int): Booking?

    @Update
    suspend fun updateBookingStatus(booking: Booking)
}
