package com.dicoding.storyappsub1.signup

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.storyappsub1.api.ApiConfig
import com.dicoding.storyappsub1.model.ResponseRegister
import com.dicoding.storyappsub1.model.UserSignUp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    private val _isRegistred = MutableLiveData<Boolean>()
    val isRegistred: LiveData<Boolean> = _isRegistred

    private val _messageRegistred = MutableLiveData<String>()
    val messageRegistred: LiveData<String> = _messageRegistred

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun signUpUser(userSignUp: UserSignUp) {
        _isLoading.value = true
        val client = ApiConfig.getApiService().registerUsers(userSignUp)
        client.enqueue(object : Callback<ResponseRegister> {
            override fun onResponse(
                call: Call<ResponseRegister>,
                response: Response<ResponseRegister>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _isLoading.value = false
                        _isRegistred.value = responseBody.error
                        _messageRegistred.value = responseBody.message
                        Log.e(TAG, "onResponse: ")
                    } else {
                        _isLoading.value = false
                        _messageRegistred.value = responseBody?.message
                        Log.e(TAG, "onResponse: ")
                    }
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<ResponseRegister>, t: Throwable) {
                _isLoading.value = false
                _messageRegistred.value = t.message
                Log.e(TAG, "onFailure: ")
            }
        })
    }

    companion object {
        private const val TAG = "SignUpViewModel"
    }
}