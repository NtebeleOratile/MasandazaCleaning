package com.example.masandazacleaning

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.masandazacleaning.database.AppDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class TrackOrderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_order)

        val rvOrders = findViewById<RecyclerView>(R.id.rvOrders)
        rvOrders.layoutManager = LinearLayoutManager(this)

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val sessionManager = SessionManager(this)
        val db = AppDatabase.getDatabase(this)
        val userId = sessionManager.getUserId()

        lifecycleScope.launch {
            try {
                val bookings = db.bookingDao().getBookingsForUser(userId)
                val tvEmptyHint = findViewById<TextView>(R.id.tvEmptyHint)
                
                if (bookings.isEmpty()) {
                    rvOrders.visibility = View.GONE
                    tvEmptyHint.visibility = View.VISIBLE
                } else {
                    rvOrders.visibility = View.VISIBLE
                    tvEmptyHint.visibility = View.GONE
                    rvOrders.adapter = BookingAdapter(bookings)
                }
            } catch (e: Exception) {
                Toast.makeText(this@TrackOrderActivity, "Error loading bookings", Toast.LENGTH_SHORT).show()
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