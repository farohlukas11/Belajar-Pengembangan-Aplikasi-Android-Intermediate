package com.dicoding.storyappsub1.main

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.dicoding.storyappsub1.UserRepository
import kotlinx.coroutines.Dispatchers

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {

    val mediator = MediatorLiveData<Unit>()

    private val _user = MutableLiveData<String>()
    val user: LiveData<String> = _user

    init {
        getTokenUser()
    }

    fun getStories(token: String) = userRepository.getStories(token).cachedIn(viewModelScope)

    private fun getTokenUser() {
        val user = userRepository.getToken().asLiveData(Dispatchers.IO)
        mediator.addSource(user) {
            _user.value = it
        }
    }
}