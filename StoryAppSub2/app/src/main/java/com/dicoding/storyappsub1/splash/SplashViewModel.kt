package com.dicoding.storyappsub1.splash

import androidx.lifecycle.*
import com.dicoding.storyappsub1.UserRepository
import kotlinx.coroutines.Dispatchers

class SplashViewModel(private val userRepository: UserRepository) : ViewModel() {

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

}