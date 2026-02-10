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

    // 1. Initial Registration (Saves to pgAdmin with Email/Password)
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    // 2. Login (Retrieves user and token)
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // 3. Onboarding (Updates the row created in step 1)
    @Multipart
    @POST("api/auth/update-profile")
    suspend fun updateProfile(
        @Part("email") email: RequestBody,
        @Part("name") name: RequestBody,
        @Part("age") age: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part profile_image: MultipartBody.Part
    ): Response<RegisterResponse>

    // 4. Fetch Users (For your Friend list)
    @GET("api/auth/users")
    suspend fun getAllUsers(): Response<List<UserData>>
}

