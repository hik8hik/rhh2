package com.example.rhh.data.auth

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.authStore by preferencesDataStore(name = "rhh_auth")

class TokenStore(private val context: Context) {

    private val JWT = stringPreferencesKey("jwt")
    private val USERNAME = stringPreferencesKey("username")
    private val EMAIL = stringPreferencesKey("email")

    val tokenFlow: Flow<String?> = context.authStore.data.map { it[JWT] }
    val usernameFlow: Flow<String?> = context.authStore.data.map { it[USERNAME] }
    val emailFlow: Flow<String?> = context.authStore.data.map { it[EMAIL] }

    suspend fun currentToken(): String? = tokenFlow.first()

    suspend fun save(token: String, username: String?, email: String?) {
        context.authStore.edit { p ->
            p[JWT] = token
            if (username != null) p[USERNAME] = username
            if (email != null) p[EMAIL] = email
        }
    }

    suspend fun clear() {
        context.authStore.edit { it.clear() }
    }
}
