package com.example.rhh.data.repo

import com.example.rhh.data.api.ApiService
import com.example.rhh.data.auth.TokenStore
import com.example.rhh.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DriveLedgerRepo(
    private val api: ApiService,
    private val store: TokenStore
) {

    // ---------- Auth ----------
    suspend fun register(username: String, email: String, password: String): String {
        val res = api.register(RegisterRequest(username, email, password))
        val token = res.token ?: throw IllegalStateException("No token returned")
        store.save(token, username, email)
        return token
    }

    suspend fun login(identifier: String, password: String): String {
        val isEmail = identifier.contains("@")
        val body = if (isEmail) LoginRequest(email = identifier, password = password)
                   else LoginRequest(username = identifier, password = password)
        val res = api.login(body)
        val token = res.token ?: throw IllegalStateException("Invalid credentials")
        store.save(token, if (isEmail) null else identifier, if (isEmail) identifier else null)
        return token
    }

    suspend fun logout() = store.clear()

    suspend fun isLoggedIn(): Boolean = !store.currentToken().isNullOrEmpty()
    fun tokenFlow() = store.tokenFlow
    fun usernameFlow() = store.usernameFlow
    fun emailFlow() = store.emailFlow

    // ---------- Data ----------
    suspend fun me(): PrivateUserDto? = withContext(Dispatchers.IO) {
        api.me().data
    }

    suspend fun trips(): List<TripDto> = withContext(Dispatchers.IO) {
        api.trips().data ?: emptyList()
    }

    suspend fun records(): List<RecordDto> = withContext(Dispatchers.IO) {
        api.records().data ?: emptyList()
    }

    suspend fun subscription(): SubscriptionDto? = withContext(Dispatchers.IO) {
        api.subscription().subscription
    }
}
