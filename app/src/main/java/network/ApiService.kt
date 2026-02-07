package com.pongsawad.blueelephant.network

import com.pongsawad.blueelephant.Friend
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @GET("api/auth/users")
    suspend fun getFriends(): List<Friend>
}
