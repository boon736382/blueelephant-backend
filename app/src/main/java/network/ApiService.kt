package com.pongsawad.blueelephant.network

import com.pongsawad.blueelephant.Friend
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response // Make sure to import this!
import retrofit2.http.Multipart
import retrofit2.http.Part

interface ApiService {

    // Remove "api/auth/" because the server already adds it
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("api/auth/users")
    suspend fun getAllUsers(): Response<List<UserData>>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @Multipart
    @POST("api/auth/register")
    suspend fun uploadProfile(
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("name") name: RequestBody,
        @Part("age") age: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part profile_image: MultipartBody.Part
    ): Response<SimpleResponse>

}

