package com.example.resdelivery.util

import android.content.SharedPreferences

class SessionManagement(
    private val sharedPreferences: SharedPreferences,
    private val editor: SharedPreferences.Editor
    ) {

    companion object {
        const val KEY_IS_LOGIN: String = "is_login"
        const val KEY_USER_ID: String = "user_id"
        const val KEY_USER_NAME: String = "user_name"
        const val KEY_USER_EMAIL: String = "user_email"
        const val KEY_USER_LATITUDE: String = "user_latitude"
        const val KEY_USER_LONGITUDE: String = "user_longitude"
    }
    fun createSession(status: Boolean, id: String, name: String, email: String) {
        editor.putBoolean(KEY_IS_LOGIN, status)
        editor.putString(KEY_USER_ID, id)
        editor.putString(KEY_USER_NAME, name)
        editor.putString(KEY_USER_EMAIL, email)
        editor.commit()
    }

    //Don't forget to redirect the user to the login screen
    fun clearUser() {
        editor.clear()
        editor.commit()
    }

    fun setUserLocation(latitude: String, longitude: String) {
        editor.putString(KEY_USER_LATITUDE, latitude)
        editor.putString(KEY_USER_LONGITUDE, longitude)
        editor.commit() // Don't forget this you idiot
    }

    fun getUserLatitude(): Double? {
        return sharedPreferences.getString(KEY_USER_LATITUDE, null)?.toDouble()
    }

    fun getUserLongitude(): Double? {
        return sharedPreferences.getString(KEY_USER_LONGITUDE, null)?.toDouble()
    }

    fun getValue(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun setLoggedIn() {
        editor.putBoolean(KEY_IS_LOGIN, true)
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean(KEY_IS_LOGIN, false)
    }
}