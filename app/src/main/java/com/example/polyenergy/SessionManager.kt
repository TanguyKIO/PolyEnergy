package com.example.polyenergy

import android.content.Context
import android.content.SharedPreferences

const val USER_COOKIE = "USER_COOKIE"

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        context.getString(R.string.app_name),
        Context.MODE_PRIVATE
    )

    fun deleteCookie() {
        val editor = prefs.edit()
        editor.putString(USER_COOKIE, null)
        editor.apply()
    }

    fun saveAuthCookie(cookie: String?) {
        val editor = prefs.edit()
        editor.putString(USER_COOKIE, cookie)
        editor.apply()
    }

}