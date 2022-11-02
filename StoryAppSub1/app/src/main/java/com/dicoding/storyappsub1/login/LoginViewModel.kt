package com.dicoding.storyappsub1.login

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.storyappsub1.UserRepository
import com.dicoding.storyappsub1.api.ApiConfig
import com.dicoding.storyappsub1.model.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _isRegistred = MutableLiveData<Boolean>()
    val isRegistred: LiveData<Boolean> = _isRegistred

    private val _messageRegistred = MutableLiveData<String>()
    val messageRegistred: LiveData<String> = _messageRegistred

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val mUserRepository: UserRepository = UserRepository(application)

    fun loginUser(userLogin: UserLogin) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().loginUsers(userLogin)
        client.enqueue(object : Callback<ResponseLogin> {
            override fun onResponse(call: Call<ResponseLogin>, response: Response<ResponseLogin>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _isLoading.value = false
                        _isRegistred.value = responseBody.error
                        _loginResult.value = responseBody.loginResult
                        _messageRegistred.value = responseBody.message
                        Log.e(TAG, "onResponse: ")
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

            override fun onFailure(call: Call<ResponseLogin>, t: Throwable) {
                _isLoading.value = false
                _messageRegistred.value = t.message
                Log.e(TAG, "onFailure: ")
            }

        })
    }

    fun saveUser(token: String, isLogin: Boolean) {
        viewModelScope.launch {
            mUserRepository.saveUser(token, isLogin)
        }
    }

    fun isLogin() {
        viewModelScope.launch {
            mUserRepository.isLogin()
        }
    }

    companion object {
        private const val TAG = "LoginViewModel"
    }
}