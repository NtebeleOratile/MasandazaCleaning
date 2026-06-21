package com.example.masandazacleaning

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.masandazacleaning.database.AppDatabase
import com.example.masandazacleaning.database.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etAddress = findViewById<EditText>(R.id.etAddress)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val cbIsStaff = findViewById<CheckBox>(R.id.cbIsStaff)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val tvLogin = findViewById<TextView>(R.id.tvLogin)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val db = AppDatabase.getDatabase(this)

        btnRegister.setOnClickListener {
            val fullName = etFullName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val address = etAddress.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val role = if (cbIsStaff.isChecked) "staff" else "customer"

            if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidPassword(password)) {
                Toast.makeText(this, "Password must be at least 6 characters with uppercase and lowercase letters", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                try {
                    val existingUser = db.userDao().getUserByEmail(email)
                    if (existingUser != null) {
                        Toast.makeText(this@RegisterActivity, "Email already registered", Toast.LENGTH_SHORT).show()
                    } else {
                        val user = User(
                            fullName = fullName,
                            email = email,
                            phone = phone,
                            address = address,
                            password = password,
                            role = role
                        )
                        db.userDao().registerUser(user)
                        Toast.makeText(this@RegisterActivity, "Registration Successful! Please login.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                        finish()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@RegisterActivity, "Registration failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }

        tvLogin.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_bookings, R.id.nav_payments, R.id.nav_profile -> {
                    Toast.makeText(this, "Please login first", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }

    private fun isValidPassword(password: String): Boolean {
        return password.length >= 6 && password.any { it.isUpperCase() } && password.any { it.isLowerCase() }
    }
}