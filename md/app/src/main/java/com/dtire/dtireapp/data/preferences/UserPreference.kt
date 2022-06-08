package com.dtire.dtireapp.data.preferences

import android.content.Context
import com.dtire.dtireapp.data.response.LoginResponse
import com.dtire.dtireapp.data.response.UserItem

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

    fun saveUserData(user: UserItem) {
        val editor = preferences.edit()
        editor.apply{
            putString(USER_NAME, user.name)
            putString(USER_EMAIL, user.email)
            putString(USER_ADDRESS, user.address)
            putString(USER_PHONE, user.phone)
            putString(USER_IMAGE_URL, user.urlPicture)
            .apply()
        }
    }

    fun getUserId(): String {
        return preferences.getString(USER_ID, "").toString()
    }

    fun getUserData(): UserItem {
        return UserItem(
            "",
            preferences.getString(USER_ADDRESS, "") ?: "",
            preferences.getString(USER_ID, "") ?: "",
            preferences.getString(USER_PHONE, "") ?: "",
            preferences.getString(USER_IMAGE_URL, "") ?: "",
            preferences.getString(USER_NAME, "") ?: "",
            preferences.getString(USER_EMAIL, "") ?: "",
        )
    }

    fun deleteUser() {
        val editor = preferences.edit()
        editor.clear()
        editor.apply()
    }

    companion object {
        private const val PREFS_NAME = "user_pref"
        private const val USER_ID = "user_id"
        private const val USER_NAME = "user_name"
        private const val USER_EMAIL = "user_email"
        private const val USER_ADDRESS = "user_address"
        private const val USER_PHONE = "user_phone"
        private const val USER_IMAGE_URL = "user_image_url"
        private const val STATUS = "status"
    }


}