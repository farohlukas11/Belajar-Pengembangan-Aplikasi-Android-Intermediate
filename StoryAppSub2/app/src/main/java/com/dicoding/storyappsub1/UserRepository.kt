package com.dicoding.storyappsub1

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.storyappsub1.api.ApiService
import com.dicoding.storyappsub1.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.lang.Exception

class UserRepository(
    private val apiService: ApiService,
    private val dataStore: SettingPreferences
) {

    fun loginUser(userLogin: UserLogin): LiveData<Result<ResponseLogin>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.loginUsers(userLogin)
            emit(Result.Success(response))
        } catch (e: Exception) {
            val eMessage = e.message.toString()
            Log.d(TAG, "loginUser: $eMessage")
            emit(Result.Error(eMessage))
        }
    }

    fun signUpUser(userSignUp: UserSignUp): LiveData<Result<ResponseRegister>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.registerUsers(userSignUp)
            emit(Result.Success(response))
        } catch (e: Exception) {
            val eMessage = e.message.toString()
            Log.d(TAG, "signUpUser: $eMessage")
            emit(Result.Error(eMessage))
        }
    }

    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            pagingSourceFactory = {
                StoriesPagingSource(token, apiService)
            }
        ).liveData
    }

    fun getStoriesMap(token: String): LiveData<Result<ResponseGetStories>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getStoriesMap("Bearer $token", 100, 1)
            emit(Result.Success(response))
        } catch (e: Exception) {
            val eMessage = e.message.toString()
            Log.d(TAG, "getStoriesMap: $eMessage")
            emit(Result.Error(eMessage))
        }
    }

    fun uploadImage(
        imageMultipart: MultipartBody.Part,
        description: RequestBody,
        token: String,
        lat: Float,
        lon: Float
    ): LiveData<Result<ResponseAddStory>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.uploadImage(
                token = token,
                description = description,
                photo = imageMultipart,
                lat = lat,
                lon = lon
            )
            emit(Result.Success(response))
        } catch (e: Exception) {
            val eMessage = e.message.toString()
            Log.d(TAG, "uploadImage: $eMessage")
            emit(Result.Error(eMessage))
        }
    }

    suspend fun saveUser(token: String, isLogin: Boolean) {
        dataStore.saveUser(token, isLogin)
    }

    fun getToken() = dataStore.getToken()

    fun getState() = dataStore.getState()

    suspend fun isLogin() = dataStore.isLogin()

    suspend fun isLogout() = dataStore.isLogout()

    suspend fun saveTheme(isDarkModeActive: Boolean) =
        dataStore.saveThemeSetting(isDarkModeActive)

    fun getTheme() = dataStore.getThemeSetting()

    companion object {
        const val TAG = "User_Repository"

        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            dataStore: SettingPreferences
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, dataStore)
            }.also { instance = it }
    }
}