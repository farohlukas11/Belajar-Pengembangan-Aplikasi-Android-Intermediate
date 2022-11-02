package com.dicoding.storyappsub1.maps

import androidx.lifecycle.*
import com.dicoding.storyappsub1.UserRepository
import kotlinx.coroutines.Dispatchers

class MapsViewModel(private val userRepository: UserRepository) : ViewModel() {
    val mediator = MediatorLiveData<Unit>()

    private val _user = MutableLiveData<String>()
    val user: LiveData<String> = _user

    init {
        getTokenUser()
    }

    private fun getTokenUser() {
        val user = userRepository.getToken().asLiveData(Dispatchers.IO)
        mediator.addSource(user) {
            _user.value = it
        }
    }

    fun getStoriesMap(token: String) = userRepository.getStoriesMap(token)
}