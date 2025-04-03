package com.example.fitlog.data.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {
    private val dataStore = context.dataStore

    suspend fun setFirstLaunchCompleted() {
        context.dataStore.edit { preferences ->
            preferences[FIRST_LAUNCH] = false
        }
    }

    fun isFirstLaunch(): Boolean {
        return runBlocking {
            context.dataStore.data.map { preferences ->
                preferences[FIRST_LAUNCH] ?: true
            }.first()
        }
    }

    companion object {
        private val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }
}

