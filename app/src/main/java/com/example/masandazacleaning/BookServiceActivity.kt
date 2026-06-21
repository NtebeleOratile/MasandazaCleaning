package com.example.masandazacleaning

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.masandazacleaning.database.AppDatabase
import com.example.masandazacleaning.database.Booking
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class BookServiceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_service)

        val spinnerService = findViewById<Spinner>(R.id.spinnerService)
        val etDate = findViewById<EditText>(R.id.etDate)
        val spinnerCollection = findViewById<Spinner>(R.id.spinnerCollection)
        val etAmount = findViewById<EditText>(R.id.etAmount)
        val btnConfirmBooking = findViewById<Button>(R.id.btnConfirmBooking)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val sessionManager = SessionManager(this)
        val db = AppDatabase.getDatabase(this)

        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = String.format(Locale.getDefault(), "%04d/%02d/%02d", selectedYear, selectedMonth + 1, selectedDay)
                etDate.setText(formattedDate)
            }, year, month, day)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()
            datePickerDialog.show()
        }

        val services = arrayOf("Sneaker Cleaning", "Boot Cleaning", "Heel Cleaning", "Suede/Nubuck Care")
        spinnerService.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, services)

        val collectionMethods = arrayOf("Drop-off", "Pick-up & Delivery")
        spinnerCollection.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, collectionMethods)

        btnConfirmBooking.setOnClickListener {
            val service = spinnerService.selectedItem.toString()
            val date = etDate.text.toString()
            val collection = spinnerCollection.selectedItem.toString()
            val amountStr = etAmount.text.toString()

            if (date.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = sessionManager.getUserId()
            if (userId == -1) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val booking = Booking(
                        userId = userId,
                        serviceType = "$service ($collection)",
                        date = date,
                        time = "",
                        price = amount,
                        status = "Pending"
                    )
                    db.bookingDao().insertBooking(booking)
                    Toast.makeText(this@BookServiceActivity, "Booking Confirmed! 📅", Toast.LENGTH_SHORT).show()
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@BookServiceActivity, "Failed to create booking: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        bottomNavigation.selectedItemId = R.id.nav_bookings
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_bookings -> true
                R.id.nav_payments -> {
                    startActivity(Intent(this, PaymentActivity::class.java))
                    finish()
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