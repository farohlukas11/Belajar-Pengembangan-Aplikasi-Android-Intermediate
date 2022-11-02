package com.dicoding.storyappsub1.api

import com.dicoding.storyappsub1.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {

    @Headers("Content-Type:application/json")
    @POST("register")
    suspend fun registerUsers(
        @Body userSignUp: UserSignUp
    ): ResponseRegister

    @Headers("Content-Type:application/json")
    @POST("login")
    suspend fun loginUsers(
        @Body userLogin: UserLogin
    ): ResponseLogin

    @Headers("Content-Type:application/json")
    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("location") location: Int
    ): ResponseGetStories

    @Headers("Content-Type:application/json")
    @GET("stories")
    suspend fun getStoriesMap(
        @Header("Authorization") token: String,
        @Query("size") size: Int,
        @Query("location") location: Int
    ): ResponseGetStories

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part,
        @Part("lat") lat: Float,
        @Part("lon") lon: Float
    ): ResponseAddStory

}