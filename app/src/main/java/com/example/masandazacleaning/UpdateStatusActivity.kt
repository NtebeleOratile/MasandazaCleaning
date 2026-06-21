package com.example.masandazacleaning

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.masandazacleaning.database.AppDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class UpdateStatusActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_update_status)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val etBookingId = findViewById<EditText>(R.id.etBookingId)
        val spinnerStatus = findViewById<Spinner>(R.id.spinnerStatus)
        val btnUpdateStatus = findViewById<Button>(R.id.btnUpdateStatus)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val db = AppDatabase.getDatabase(this)
        val sessionManager = SessionManager(this)

        val statuses = arrayOf("Pending", "In Progress", "Completed", "Cancelled")
        spinnerStatus.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statuses)

        btnUpdateStatus.setOnClickListener {
            // ... (existing logic)
            val bookingIdStr = etBookingId.text.toString().trim()
            val newStatus = spinnerStatus.selectedItem.toString()

            if (bookingIdStr.isEmpty()) {
                Toast.makeText(this, "Please enter Booking ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bookingId = bookingIdStr.toIntOrNull()
            if (bookingId == null) {
                Toast.makeText(this, "Invalid Booking ID", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val booking = db.bookingDao().getBookingById(bookingId)
                if (booking != null) {
                    val updatedBooking = booking.copy(status = newStatus)
                    db.bookingDao().updateBookingStatus(updatedBooking)
                    Toast.makeText(this@UpdateStatusActivity, "Status updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@UpdateStatusActivity, "Booking not found", Toast.LENGTH_SHORT).show()
                }
            }
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_bookings -> {
                    startActivity(Intent(this, StaffDashboardActivity::class.java))
                    true
                }
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
}