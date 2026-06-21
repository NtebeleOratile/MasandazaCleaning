package com.example.masandazacleaning

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.masandazacleaning.database.AppDatabase
import com.example.masandazacleaning.database.Booking
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class StaffDashboardActivity : AppCompatActivity() {
    private lateinit var adapter: StaffBookingAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_dashboard)

        db = AppDatabase.getDatabase(this)
        val rvStaffBookings = findViewById<RecyclerView>(R.id.rvStaffBookings)
        rvStaffBookings.layoutManager = LinearLayoutManager(this)

        loadBookings()

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val sessionManager = SessionManager(this)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_bookings -> true
                R.id.nav_payments -> {
                    startActivity(Intent(this, PaymentActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun loadBookings() {
        lifecycleScope.launch {
            val bookings = db.bookingDao().getAllBookings()
            val rvStaffBookings = findViewById<RecyclerView>(R.id.rvStaffBookings)
            val tvEmptyHint = findViewById<TextView>(R.id.tvEmptyHint)

            if (bookings.isEmpty()) {
                rvStaffBookings.visibility = View.GONE
                tvEmptyHint.visibility = View.VISIBLE
            } else {
                rvStaffBookings.visibility = View.VISIBLE
                tvEmptyHint.visibility = View.GONE
                adapter = StaffBookingAdapter(bookings) { booking ->
                    showUpdateStatusDialog(booking)
                }
                rvStaffBookings.adapter = adapter
            }
        }
    }

    private fun showUpdateStatusDialog(booking: Booking) {
        val statuses = arrayOf("Pending", "In Progress", "Completed", "Cancelled")
        AlertDialog.Builder(this)
            .setTitle("Update Status")
            .setItems(statuses) { _, which ->
                val newStatus = statuses[which]
                updateBookingStatus(booking, newStatus)
            }
            .show()
    }

    private fun updateBookingStatus(booking: Booking, newStatus: String) {
        lifecycleScope.launch {
            val updatedBooking = booking.copy(status = newStatus)
            db.bookingDao().updateBookingStatus(updatedBooking)
            Toast.makeText(this@StaffDashboardActivity, "Status updated to $newStatus", Toast.LENGTH_SHORT).show()
            loadBookings()
        }
    }
}
