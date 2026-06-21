package com.example.masandazacleaning

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.masandazacleaning.database.AppDatabase
import com.example.masandazacleaning.database.Booking
import com.example.masandazacleaning.database.Payment
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PaymentActivity : AppCompatActivity() {
    private var selectedBooking: Booking? = null
    private var bookingsList: List<Booking> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val spinnerBooking = findViewById<Spinner>(R.id.spinnerBooking)
        val etAmount = findViewById<EditText>(R.id.etPaymentAmount)
        val spinnerMethod = findViewById<Spinner>(R.id.spinnerPaymentMethod)
        val btnPayNow = findViewById<Button>(R.id.btnPayNow)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val sessionManager = SessionManager(this)
        val db = AppDatabase.getDatabase(this)
        val userId = sessionManager.getUserId()

        val paymentMethods = arrayOf("Credit Card", "Debit Card", "EFT", "Cash")
        spinnerMethod.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, paymentMethods)

        lifecycleScope.launch {
            bookingsList = db.bookingDao().getBookingsForUser(userId).filter { it.status != "Completed" }
            if (bookingsList.isEmpty()) {
                Toast.makeText(this@PaymentActivity, "No pending bookings found", Toast.LENGTH_SHORT).show()
                val adapter = ArrayAdapter(this@PaymentActivity, android.R.layout.simple_spinner_dropdown_item, arrayOf("No pending bookings"))
                spinnerBooking.adapter = adapter
            } else {
                val bookingInfo = bookingsList.map { "${it.serviceType} - R${String.format(Locale.getDefault(), "%.2f", it.price)}" }
                val adapter = ArrayAdapter(this@PaymentActivity, android.R.layout.simple_spinner_dropdown_item, bookingInfo)
                spinnerBooking.adapter = adapter
            }
        }

        spinnerBooking.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (bookingsList.isNotEmpty()) {
                    selectedBooking = bookingsList[position]
                    etAmount.setText(selectedBooking?.price.toString())
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        btnPayNow.setOnClickListener {
            val amountStr = etAmount.text.toString().trim()
            val method = spinnerMethod.selectedItem.toString()

            if (selectedBooking == null) {
                Toast.makeText(this, "Please select a booking", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Please enter amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val currentDate = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(Date())
                    val payment = Payment(
                        userId = userId,
                        amount = amount,
                        paymentMethod = method,
                        date = currentDate
                    )
                    db.paymentDao().insertPayment(payment)
                    
                    // Mark booking as complete after payment
                    selectedBooking?.let {
                        val completedBooking = it.copy(status = "Completed")
                        db.bookingDao().updateBookingStatus(completedBooking)
                    }

                    Toast.makeText(this@PaymentActivity, "Payment Successful! Booking Completed ✅", Toast.LENGTH_SHORT).show()
                    finish()
                } catch (e: Exception) {
                    Toast.makeText(this@PaymentActivity, "Payment failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        bottomNavigation.selectedItemId = R.id.nav_payments
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_bookings -> {
                    startActivity(Intent(this, TrackOrderActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_payments -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
