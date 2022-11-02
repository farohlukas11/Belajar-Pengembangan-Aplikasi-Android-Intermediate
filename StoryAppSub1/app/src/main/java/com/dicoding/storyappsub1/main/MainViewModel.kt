package com.dicoding.storyappsub1.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.dicoding.storyappsub1.UserRepository
import com.dicoding.storyappsub1.api.ApiConfig
import com.dicoding.storyappsub1.model.ListStoryItem
import com.dicoding.storyappsub1.model.ResponseGetStories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val mUserRepository: UserRepository = UserRepository(application)

    private val _isRegistred = MutableLiveData<Boolean>()
    val isRegistred: LiveData<Boolean> = _isRegistred

    private val _messageRegistred = MutableLiveData<String>()
    val messageRegistred: LiveData<String> = _messageRegistred

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val mediator = MediatorLiveData<Unit>()

    private val _user = MutableLiveData<String>()
    val user: LiveData<String> = _user

    init {
        getTokenUser()
    }

    fun getStories(token: String) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().getStories("Bearer $token")
        client.enqueue(object : Callback<ResponseGetStories> {
            override fun onResponse(
                call: Call<ResponseGetStories>,
                response: Response<ResponseGetStories>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _isLoading.value = false
                        _isRegistred.value = responseBody.error
                        _messageRegistred.value = responseBody.message
                        _listStory.value = responseBody.listStory
                    } else {
                        _isLoading.value = false
                        _messageRegistred.value = responseBody?.message
                        Log.e(TAG, "onResponse: ")
                    }
                } else {
                    _isLoading.value = false
                    _messageRegistred.value = response.body()?.message
                }
            }

            override fun onFailure(call: Call<ResponseGetStories>, t: Throwable) {
                _isLoading.value = false
                _messageRegistred.value = t.message
                Log.e(TAG, "onFailure: ")
            }

        })
    }

    fun isLogout() {
        viewModelScope.launch {
            mUserRepository.isLogout()
        }
    }

    private fun getTokenUser() {
        val user = mUserRepository.getToken().asLiveData(Dispatchers.IO)
        mediator.addSource(user) {
            _user.value = it
        }
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}