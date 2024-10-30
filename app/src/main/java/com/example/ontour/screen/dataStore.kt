package com.example.ontour.screen

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import java.util.prefs.Preferences



// Function to save the token in SharedPreferences
fun saveToken(context: Context, token: Int) {
    val sharedPreferences = context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
    sharedPreferences.edit().putInt("id_service", token).apply()
}

// Function to retrieve the token from SharedPreferences
fun getToken(context: Context): Int {
    val sharedPreferences = context.getSharedPreferences("token_prefs", Context.MODE_PRIVATE)
    return sharedPreferences.getInt("id_service", 0)
}
