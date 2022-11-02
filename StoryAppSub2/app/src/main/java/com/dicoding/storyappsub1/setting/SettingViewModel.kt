package com.dicoding.storyappsub1.setting

import androidx.lifecycle.*
import com.dicoding.storyappsub1.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingViewModel(private val userRepository: UserRepository) : ViewModel() {

    val mediator = MediatorLiveData<Unit>()

    private val _user = MutableLiveData<Boolean>()
    val user: LiveData<Boolean> = _user

    init {
        getUser()
    }

    private fun getUser() {
        val user = userRepository.getState().asLiveData(Dispatchers.IO)
        mediator.addSource(user) {
            _user.value = it
        }
    }

    fun getThemeSetting(): LiveData<Boolean> = userRepository.getTheme().asLiveData(Dispatchers.IO)

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            userRepository.saveTheme(isDarkModeActive)
        }
    }

    fun isLogout() {
        viewModelScope.launch {
            userRepository.isLogout()
        }
    }

    fun saveUser(token: String, isLogin: Boolean) {
        viewModelScope.launch {
            userRepository.saveUser(token, isLogin)
        }
    }
}