package com.dicoding.storyappsub1.add_story

import android.graphics.Bitmap
import androidx.lifecycle.*
import com.dicoding.storyappsub1.UserRepository
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class AddStoryViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _fileImage = MutableLiveData<File>()
    val fileImage: LiveData<File> = _fileImage

    private val _fileBitmap = MutableLiveData<Bitmap>()
    val fileBitmap: LiveData<Bitmap> = _fileBitmap

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

    fun uploadImage(
        imageMultipart: MultipartBody.Part,
        description: RequestBody,
        token: String,
        lat: Float,
        lon: Float
    ) = userRepository.uploadImage(
        imageMultipart = imageMultipart,
        description = description,
        token = "Bearer $token",
        lat = lat,
        lon = lon
    )

    private fun getTokenUser() {
        val user = userRepository.getToken().asLiveData(Dispatchers.IO)
        mediator.addSource(user) {
            _user.value = it
        }
    }
}