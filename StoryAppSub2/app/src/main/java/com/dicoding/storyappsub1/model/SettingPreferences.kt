package com.dicoding.storyappsub1.model

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingPreferences private constructor(
    private val context: Context
) {
    private val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(
        name = DATA_STORE_NAME
    )

    suspend fun saveUser(token: String, isLogin: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[STATE_KEY] = isLogin
        }
    }

    fun getToken(): Flow<String> {
        return context.userPreferencesDataStore.data.map { preferences ->
            preferences[TOKEN_KEY] ?: ""
        }
    }

    fun getState(): Flow<Boolean> {
        return context.userPreferencesDataStore.data.map { preferences ->
            preferences[STATE_KEY] ?: false
        }
    }

    suspend fun isLogout() {
        context.userPreferencesDataStore.edit {
            it[STATE_KEY] = false
        }
    }

    suspend fun isLogin() {
        context.userPreferencesDataStore.edit {
            it[STATE_KEY] = true
        }
    }

    fun getThemeSetting(): Flow<Boolean> {
        return context.userPreferencesDataStore.data.map { preferences ->
            preferences[THEME_KEY] ?: false
        }
    }

    suspend fun saveThemeSetting(isDarkModeActive: Boolean) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[THEME_KEY] = isDarkModeActive
        }
    }

    companion object {
        const val DATA_STORE_NAME = "user2"
        val THEME_KEY = booleanPreferencesKey("theme_setting")

        private val TOKEN_KEY = stringPreferencesKey("token")
        private val STATE_KEY = booleanPreferencesKey("state")

        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: SettingPreferences? = null

        fun getInstance(context: Context): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(context)
                INSTANCE = instance
                instance
            }
        }
    }
}