package com.dicoding.storyappsub1.splash

import android.app.Application
import androidx.lifecycle.*
import com.dicoding.storyappsub1.UserRepository
import kotlinx.coroutines.Dispatchers

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val mUserRepository: UserRepository = UserRepository(application)
    val mediator = MediatorLiveData<Unit>()

    private val _user = MutableLiveData<Boolean>()
    val user: LiveData<Boolean> = _user

    init {
        getUser()
    }

    private fun getUser() {
        val user = mUserRepository.getState().asLiveData(Dispatchers.IO)
        mediator.addSource(user) {
            _user.value = it
        }
    }

    fun getThemeSetting(): LiveData<Boolean> = mUserRepository.getTheme().asLiveData(Dispatchers.IO)

}