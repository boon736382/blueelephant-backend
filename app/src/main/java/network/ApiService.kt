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

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("api/auth/users")
    suspend fun getAllUsers(): Response<List<UserData>>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // --- NEW: This is for the Onboarding screen ---
    @Multipart
    @POST("api/auth/update-profile")
    suspend fun updateProfile(
        @Part("email") email: RequestBody,
        @Part("name") name: RequestBody,
        @Part("age") age: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part profile_image: MultipartBody.Part // Matches your backend 'profile_image'
    ): Response<RegisterResponse>

    // Keep this only if you want to upload everything at once during signup
    @Multipart
    @POST("api/auth/register")
    suspend fun uploadProfile(
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("name") name: RequestBody,
        @Part("age") age: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part profile_image: MultipartBody.Part
    ): Response<RegisterResponse>
}

