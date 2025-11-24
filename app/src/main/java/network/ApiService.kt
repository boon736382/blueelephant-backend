package com.pongsawad.blueelephant.network

import com.pongsawad.blueelephant.network.GenericResponse
import com.pongsawad.blueelephant.network.ChangePasswordRequest
import com.pongsawad.blueelephant.network.LoginRequest
import com.pongsawad.blueelephant.network.LoginResponse
import com.pongsawad.blueelephant.network.RegisterRequest
import com.pongsawad.blueelephant.network.RegisterResponse

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/auth/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>

    @POST("api/auth/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("api/auth/change-password")
    fun changePassword(@Body request: ChangePasswordRequest): Call<GenericResponse>
}
