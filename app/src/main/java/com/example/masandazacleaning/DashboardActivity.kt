package com.example.masandazacleaning

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.lifecycle.lifecycleScope
import com.example.masandazacleaning.database.AppDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val btnLogout = findViewById<TextView>(R.id.btnLogout)
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        val cardBookService = findViewById<CardView>(R.id.cardBookService)
        val cardTrackOrder = findViewById<CardView>(R.id.cardTrackOrder)
        val cardPayments = findViewById<CardView>(R.id.cardPayments)
        val cardProfile = findViewById<CardView>(R.id.cardProfile)
        val cardStaffDashboard = findViewById<CardView>(R.id.cardStaffDashboard)
        val cardReports = findViewById<CardView>(R.id.cardReports)
        val cardUpdateStatus = findViewById<CardView>(R.id.cardUpdateStatus)
        val tvActiveBookingsCount = findViewById<TextView>(R.id.tvActiveBookingsCount)
        val tvPendingPaymentsCount = findViewById<TextView>(R.id.tvPendingPaymentsCount)
        val llStaffSection = findViewById<LinearLayout>(R.id.llStaffSection)
        val llServicesSection = findViewById<LinearLayout>(R.id.llServicesSection)
        val cardStats = findViewById<CardView>(R.id.cardStats)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val sessionManager = SessionManager(this)
        val db = AppDatabase.getDatabase(this)

        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        val userName = sessionManager.getUserName() ?: "User"
        tvWelcome.text = "Welcome Back, $userName! 👋"

        val userId = sessionManager.getUserId()
        val userRole = sessionManager.getUserRole()

        // Hide/Show sections based on role
        if (userRole == "staff") {
            llStaffSection.visibility = View.VISIBLE
            llServicesSection.visibility = View.GONE
            cardStats.visibility = View.GONE // Stats are for customers
        } else {
            llStaffSection.visibility = View.GONE
            llServicesSection.visibility = View.VISIBLE
            cardStats.visibility = View.VISIBLE
            
            // Load stats only for customers
            lifecycleScope.launch {
                try {
                    val userBookings = db.bookingDao().getBookingsForUser(userId)
                    val activeBookings = userBookings.count {
                        it.status != "Completed" && it.status != "Cancelled"
                    }
                    val userPayments = db.paymentDao().getPaymentsForUser(userId)
                    val paidBookingIds = userPayments.map { it.userId }.toSet()
                    val pendingPayments = userBookings.count { it.userId !in paidBookingIds }

                    tvActiveBookingsCount.text = activeBookings.toString()
                    tvPendingPaymentsCount.text = pendingPayments.toString()
                } catch (e: Exception) {
                    tvActiveBookingsCount.text = "0"
                    tvPendingPaymentsCount.text = "0"
                }
            }
        }

        // Navigation setup
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_bookings -> {
                    if (userRole == "staff") {
                        startActivity(Intent(this, StaffDashboardActivity::class.java))
                    } else {
                        startActivity(Intent(this, TrackOrderActivity::class.java))
                    }
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

        // Logout listener
        btnLogout.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Tile click listeners
        cardBookService.setOnClickListener {
            startActivity(Intent(this, BookServiceActivity::class.java))
        }

        cardTrackOrder.setOnClickListener {
            startActivity(Intent(this, TrackOrderActivity::class.java))
        }

        cardPayments.setOnClickListener {
            startActivity(Intent(this, PaymentActivity::class.java))
        }

        cardProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        cardStaffDashboard.setOnClickListener {
            startActivity(Intent(this, StaffDashboardActivity::class.java))
        }

        cardReports.setOnClickListener {
            startActivity(Intent(this, ReportsActivity::class.java))
        }

        cardUpdateStatus.setOnClickListener {
            startActivity(Intent(this, UpdateStatusActivity::class.java))
        }
    }
}
