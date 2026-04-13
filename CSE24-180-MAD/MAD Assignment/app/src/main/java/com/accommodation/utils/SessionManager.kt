package com.accommodation.utils

import android.content.Context
import android.content.SharedPreferences

object SessionManager {
    private const val PREFS = "session"
    private const val KEY_USER_ID = "userId"
    private const val KEY_ROLE = "role"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun save(context: Context, userId: Int, role: String) =
        prefs(context).edit().putInt(KEY_USER_ID, userId).putString(KEY_ROLE, role).apply()

    fun getUserId(context: Context): Int = prefs(context).getInt(KEY_USER_ID, -1)
    fun getRole(context: Context): String = prefs(context).getString(KEY_ROLE, "") ?: ""
    fun isLoggedIn(context: Context): Boolean = getUserId(context) != -1
    fun clear(context: Context) = prefs(context).edit().clear().apply()
}
