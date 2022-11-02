package com.dicoding.storyappsub1.setting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyappsub1.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingViewModel(application: Application) : AndroidViewModel(application) {
    private val mUserRepository: UserRepository = UserRepository(application)

    fun getThemeSetting(): LiveData<Boolean> = mUserRepository.getTheme().asLiveData(Dispatchers.IO)

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            mUserRepository.saveTheme(isDarkModeActive)
        }
    }

    fun isLogout() {
        viewModelScope.launch {
            mUserRepository.isLogout()
        }
    }

    fun saveUser(token: String, isLogin: Boolean) {
        viewModelScope.launch {
            mUserRepository.saveUser(token, isLogin)
        }
    }
}