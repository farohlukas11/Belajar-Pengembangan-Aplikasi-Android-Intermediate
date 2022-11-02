package com.dicoding.storyappsub1.login

import androidx.lifecycle.*
import com.dicoding.storyappsub1.UserRepository
import com.dicoding.storyappsub1.model.*
import kotlinx.coroutines.launch


class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun loginUser(userLogin: UserLogin) = userRepository.loginUser(userLogin)

    fun saveUser(token: String, isLogin: Boolean) {
        viewModelScope.launch {
            userRepository.saveUser(token, isLogin)
        }
    }

    fun isLogin() {
        viewModelScope.launch {
            userRepository.isLogin()
        }
    }
}