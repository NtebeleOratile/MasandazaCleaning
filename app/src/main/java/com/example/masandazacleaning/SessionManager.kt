package com.example.masandazacleaning

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    fun saveUser(userId: Int, name: String, role: String) {
        val editor = prefs.edit()
        editor.putInt("USER_ID", userId)
        editor.putString("USER_NAME", name)
        editor.putString("USER_ROLE", role)
        editor.putBoolean("IS_LOGGED_IN", true)
        editor.apply()
    }

    fun getUserId(): Int = prefs.getInt("USER_ID", -1)
    fun getUserName(): String? = prefs.getString("USER_NAME", null)
    fun getUserRole(): String? = prefs.getString("USER_ROLE", null)
    fun isLoggedIn(): Boolean = prefs.getBoolean("IS_LOGGED_IN", false)

    fun logout() {
        prefs.edit().clear().apply()
    }
}
