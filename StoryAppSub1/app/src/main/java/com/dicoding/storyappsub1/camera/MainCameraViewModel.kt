package com.dicoding.storyappsub1.camera

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.*
import com.dicoding.storyappsub1.UserRepository
import com.dicoding.storyappsub1.api.ApiConfig
import com.dicoding.storyappsub1.model.ResponseAddStory
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainCameraViewModel(application: Application) : AndroidViewModel(application) {

    private val mUserRepository: UserRepository = UserRepository(application)

    private val _fileImage = MutableLiveData<File>()
    val fileImage: LiveData<File> = _fileImage

    private val _fileBitmap = MutableLiveData<Bitmap>()
    val fileBitmap: LiveData<Bitmap> = _fileBitmap

    private val _isRegistred = MutableLiveData<Boolean>()
    val isRegistred: LiveData<Boolean> = _isRegistred

    private val _messageRegistred = MutableLiveData<String>()
    val messageRegistred: LiveData<String> = _messageRegistred

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val mediator = MediatorLiveData<Unit>()

    private val _user = MutableLiveData<String>()
    val user: LiveData<String> = _user

    init {
        getTokenUser()
    }

    fun setImage(file: File) {
        _fileImage.value = file
    }

    fun setBitmap(bitmap: Bitmap) {
        _fileBitmap.value = bitmap
    }

    fun uploadImage(imageMultipart: MultipartBody.Part, description: RequestBody, token: String) {
        _isLoading.value = true
        val client =
            ApiConfig.getApiService().uploadImage("Bearer $token", description, imageMultipart)
        client.enqueue(object : Callback<ResponseAddStory> {
            override fun onResponse(
                call: Call<ResponseAddStory>,
                response: Response<ResponseAddStory>
            ) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _isLoading.value = false
                        _isRegistred.value = responseBody.error
                        _messageRegistred.value = responseBody.message
                    } else {
                        _isLoading.value = false
                        _messageRegistred.value = responseBody?.message
                        Log.e(TAG, "onResponse: ")
                    }
                } else {
                    _isLoading.value = false
                }
            }

            override fun onFailure(call: Call<ResponseAddStory>, t: Throwable) {
                _isLoading.value = false
                _messageRegistred.value = t.message
                Log.e(TAG, "onFailure: ")
            }
        })
    }

    private fun getTokenUser() {
        val user = mUserRepository.getToken().asLiveData(Dispatchers.IO)
        mediator.addSource(user) {
            _user.value = it
        }
    }

    companion object {
        private const val TAG = "MainCameraViewModel"
    }
}