package com.example.masandazacleaning

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Login is now handled in MainActivity (Landing Page)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
