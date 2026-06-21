package com.example.masandazacleaning

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.masandazacleaning.database.AppDatabase
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.TextAlignment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

/**
 * ReportsActivity provides business insights and PDF generation capabilities.
 * Staff members can view system-wide stats and export them to a PDF file.
 */
class ReportsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable transparent system bars
        setContentView(R.layout.activity_reports)
        
        // Handle window insets for EdgeToEdge display
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize UI components
        val tvTotalBookings = findViewById<TextView>(R.id.tvTotalBookings)
        val tvCompletedOrders = findViewById<TextView>(R.id.tvCompletedOrders)
        val tvPendingOrders = findViewById<TextView>(R.id.tvPendingOrders)
        val tvTotalRevenue = findViewById<TextView>(R.id.tvTotalRevenue)
        val btnGeneratePdf = findViewById<Button>(R.id.btnGeneratePdf)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val db = AppDatabase.getDatabase(this)

        // Load aggregate statistics from the database
        lifecycleScope.launch {
            val allBookings = db.bookingDao().getAllBookings()
            val totalBookings = allBookings.size
            val completedOrders = allBookings.count { it.status == "Completed" }
            val pendingOrders = allBookings.count { it.status == "Pending" }
            val totalRevenue = allBookings.sumOf { it.price }

            tvTotalBookings.text = totalBookings.toString()
            tvCompletedOrders.text = completedOrders.toString()
            tvPendingOrders.text = pendingOrders.toString()
            tvTotalRevenue.text = String.format(Locale.getDefault(), "R%.2f", totalRevenue)
        }

        // Click listener for PDF Export
        btnGeneratePdf.setOnClickListener {
            lifecycleScope.launch {
                generatePdfReport()
            }
        }

        // Navigation Menu Handling
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

    /**
     * Generates a detailed PDF business report containing a summary and a transaction table.
     * Uses iText7 library for PDF structure and writing.
     */
    private suspend fun generatePdfReport() {
        val db = AppDatabase.getDatabase(this)
        val bookings = withContext(Dispatchers.IO) { db.bookingDao().getAllBookings() }

        if (bookings.isEmpty()) {
            Toast.makeText(this, "No data to generate report", Toast.LENGTH_SHORT).show()
            return
        }

        // Perform file I/O operations on the IO thread
        withContext(Dispatchers.IO) {
            try {
                // Create unique filename based on current timestamp
                val fileName = "Masandaza_Report_${System.currentTimeMillis()}.pdf"
                val filePath = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
                val outputStream = FileOutputStream(filePath)

                // Initialize PDF writer and document structure
                val writer = PdfWriter(outputStream)
                val pdf = PdfDocument(writer)
                val document = Document(pdf)

                // ADD CONTENT TO PDF
                // Title
                document.add(Paragraph("Masandaza Cleaning Services - Business Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(20f)
                    .setBold())

                // Metadata and Summary paragraph
                document.add(Paragraph("Generated on: ${java.util.Date()}"))
                document.add(Paragraph("\nSummary:"))
                document.add(Paragraph("Total Bookings: ${bookings.size}"))
                document.add(Paragraph("Total Revenue: R${String.format(Locale.getDefault(), "%.2f", bookings.sumOf { it.price })}"))

                // DATA TABLE
                document.add(Paragraph("\nBooking Details:"))
                val table = Table(floatArrayOf(1f, 3f, 2f, 2f)) // 4 columns with relative weights
                table.addCell("ID")
                table.addCell("Service")
                table.addCell("Status")
                table.addCell("Price")

                // Populate table rows from database records
                for (booking in bookings) {
                    table.addCell(booking.id.toString())
                    table.addCell(booking.serviceType)
                    table.addCell(booking.status)
                    table.addCell("R${String.format(Locale.getDefault(), "%.2f", booking.price)}")
                }
                document.add(table)

                // Finalize and close file
                document.close()
                
                // Show success message on Main thread
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ReportsActivity, "PDF Generated: ${filePath.absolutePath}", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ReportsActivity, "Error generating PDF: ${e.message}", Toast.LENGTH_SHORT).show()
                }
                e.printStackTrace()
            }
        }
    }
}