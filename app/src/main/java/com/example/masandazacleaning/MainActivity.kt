package com.example.masandazacleaning

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.masandazacleaning.database.AppDatabase
import kotlinx.coroutines.launch

/**
 * MainActivity serves as the entry point of the application.
 * It handles user authentication (Login) and session redirection.
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SessionManager to check if the user is already logged in
        val sessionManager = SessionManager(this)

        // If user is already logged in, redirect them directly to the Dashboard
        if (sessionManager.isLoggedIn()) {
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        // Initialize UI components
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvCreateAccount = findViewById<TextView>(R.id.tvCreateAccount)

        // Get instance of the Room Database
        val db = AppDatabase.getDatabase(this)

        // Login button click listener
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Basic validation for empty fields
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Perform login authentication in a coroutine (background thread)
            lifecycleScope.launch {
                try {
                    val user = db.userDao().loginUser(email, password)
                    if (user != null) {
                        // Save user session details
                        sessionManager.saveUser(user.id, user.fullName, user.role)
                        Toast.makeText(this@MainActivity, "Login Successful", Toast.LENGTH_SHORT).show()

                        // Navigate to DashboardActivity and clear activity stack
                        val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        // Notify user if credentials don't match
                        Toast.makeText(this@MainActivity, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    // Handle potential database errors
                    Toast.makeText(this@MainActivity, "Login failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Navigate to Registration screen
        tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}