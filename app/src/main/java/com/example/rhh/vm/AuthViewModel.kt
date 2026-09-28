package com.example.rhh.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rhh.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthState(
    val token: String? = null,
    val busy: Boolean = false,
    val error: String? = null,
    val ready: Boolean = false
)

class AuthViewModel : ViewModel() {
    private val repo = ServiceLocator.repo
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val t = repo.let { if (it.isLoggedIn()) repo.tokenFlow().let { _ -> null } else null } // noop
        }
        // Observe the token flow — this is the source of truth for auth.
        viewModelScope.launch {
            repo.tokenFlow().collect { t ->
                _state.value = _state.value.copy(token = t, ready = true)
            }
        }
    }

    fun login(identifier: String, password: String) = viewModelScope.launch {
        _state.value = _state.value.copy(busy = true, error = null)
        try {
            repo.login(identifier.trim(), password)
            _state.value = _state.value.copy(busy = false)
        } catch (e: Exception) {
            _state.value = _state.value.copy(busy = false, error = e.message ?: "Login failed")
        }
    }

    fun register(username: String, email: String, password: String) = viewModelScope.launch {
        _state.value = _state.value.copy(busy = true, error = null)
        try {
            repo.register(username.trim(), email.trim().lowercase(), password)
            _state.value = _state.value.copy(busy = false)
        } catch (e: Exception) {
            _state.value = _state.value.copy(busy = false, error = e.message ?: "Registration failed")
        }
    }

    fun clearError() { _state.value = _state.value.copy(error = null) }

    fun logout() = viewModelScope.launch { repo.logout() }
}
