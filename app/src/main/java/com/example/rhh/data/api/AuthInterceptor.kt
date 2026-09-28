package com.example.rhh.data.api

import com.example.rhh.data.auth.TokenStore
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val store: TokenStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { store.currentToken() }
        val req = chain.request().newBuilder()
            .addHeader("ngrok-skip-browser-warning", "true")
            .apply { if (!token.isNullOrEmpty()) addHeader("Authorization", "Bearer $token") }
            .build()
        return chain.proceed(req)
    }
}
