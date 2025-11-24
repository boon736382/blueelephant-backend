package com.pongsawad.blueelephant.network

import com.pongsawad.blueelephant.LoginResponse
import com.pongsawad.blueelephant.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val email: String, val password: String)

interface ApiService {

    @POST("/login")
    fun login(@Body request: LoginRequest): Call<LoginResponse>

    @POST("/register")
    fun register(@Body request: RegisterRequest): Call<RegisterResponse>
}
