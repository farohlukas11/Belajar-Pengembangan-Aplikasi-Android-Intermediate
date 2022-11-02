package com.dicoding.storyappsub1.api

import com.dicoding.storyappsub1.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    @Headers("Content-Type:application/json")
    @POST("register")
    fun registerUsers(
        @Body userSignUp: UserSignUp
    ): Call<ResponseRegister>

    @Headers("Content-Type:application/json")
    @POST("login")
    fun loginUsers(
        @Body userLogin: UserLogin
    ): Call<ResponseLogin>

    @Headers("Content-Type:application/json")
    @GET("stories")
    fun getStories(
        @Header("Authorization") token: String
    ): Call<ResponseGetStories>

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
    ): Call<ResponseAddStory>

}