package com.dtire.dtireapp.data.preferences

import android.content.Context
import com.dtire.dtireapp.data.response.LoginResponse

class UserPreference(context: Context) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean {
        return preferences.getString(USER_ID, "") != ""
    }

    fun saveUserId(user: LoginResponse) {
        val editor = preferences.edit()
        editor.putString(USER_ID, user.userId)
        editor.putInt(STATUS, user.status)
        editor.apply()
    }

    fun getUserId(): String? {
        return preferences.getString(USER_ID, "")
    }

    fun getUserLoginStatus(): Int {
        return preferences.getInt(STATUS, 0)
    }

    fun deleteUser() {
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val USER_ID = "user_id"
        private const val STATUS = "status"
    }


}