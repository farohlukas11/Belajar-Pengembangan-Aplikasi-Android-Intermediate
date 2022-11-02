package com.dicoding.storyappsub1

import android.app.Application
import com.dicoding.storyappsub1.model.SettingPreferences

class UserRepository(application: Application) {

    private val mUserPreferences: SettingPreferences

    init {
        mUserPreferences = SettingPreferences.getInstance(application)
    }

    suspend fun saveUser(token: String, isLogin: Boolean) {
        mUserPreferences.saveUser(token, isLogin)
    }

    fun getToken() = mUserPreferences.getToken()

    fun getState() = mUserPreferences.getState()

    suspend fun isLogin() = mUserPreferences.isLogin()

    suspend fun isLogout() = mUserPreferences.isLogout()

    suspend fun saveTheme(isDarkModeActive: Boolean) =
        mUserPreferences.saveThemeSetting(isDarkModeActive)

    fun getTheme() = mUserPreferences.getThemeSetting()
}