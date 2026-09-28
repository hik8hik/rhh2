package com.example.rhh.data.api

import com.example.rhh.data.model.*
import retrofit2.http.*

interface ApiService {

    // ---------- Auth ----------
    @POST("api/auth/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    // ---------- Private ----------
    @GET("api/private/")
    suspend fun me(): PrivateUserEnvelope

    @GET("api/private/gettrips")
    suspend fun trips(): TripsEnvelope

    @GET("api/private/getallrecords")
    suspend fun records(): RecordsEnvelope

    // ---------- Payments ----------
    @GET("api/payments/subscription")
    suspend fun subscription(): SubscriptionEnvelope
}
