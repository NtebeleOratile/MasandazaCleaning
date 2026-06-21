package com.example.masandazacleaning

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.masandazacleaning.database.AppDatabase
import com.example.masandazacleaning.database.User
import kotlinx.coroutines.launch
import java.util.Locale

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val tvName = findViewById<TextView>(R.id.tvProfileName)
        val tvRole = findViewById<TextView>(R.id.tvProfileRole)
        val tvEmail = findViewById<TextView>(R.id.tvProfileEmail)
        val tvPhone = findViewById<TextView>(R.id.tvProfilePhone)
        val tvAddress = findViewById<TextView>(R.id.tvProfileAddress)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        val sessionManager = SessionManager(this)
        val db = AppDatabase.getDatabase(this)
        val userId = sessionManager.getUserId()

        if (userId != -1) {
            lifecycleScope.launch {
                try {
                    val user: User? = db.userDao().getUserById(userId)
                    user?.let {
                        tvName.text = it.fullName
                        tvRole.text = it.role.replaceFirstChar { char -> 
                            if (char.isLowerCase()) char.titlecase(Locale.getDefault()) else char.toString() 
                        }
                        tvEmail.text = it.email
                        tvPhone.text = it.phone
                        tvAddress.text = it.address
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        btnLogout.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
