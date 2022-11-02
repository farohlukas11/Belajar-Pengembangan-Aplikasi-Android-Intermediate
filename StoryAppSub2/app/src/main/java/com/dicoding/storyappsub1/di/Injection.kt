package com.dicoding.storyappsub1.di

import android.content.Context
import com.dicoding.storyappsub1.UserRepository
import com.dicoding.storyappsub1.api.ApiConfig
import com.dicoding.storyappsub1.model.SettingPreferences

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val dataStore = SettingPreferences.getInstance(context)

        return UserRepository.getInstance(apiService, dataStore)
    }
}