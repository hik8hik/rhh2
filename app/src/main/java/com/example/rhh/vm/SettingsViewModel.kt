package com.example.rhh.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rhh.ServiceLocator
import com.example.rhh.data.model.SubscriptionDto
import com.example.rhh.data.model.PrivateUserDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsState(
    val loading: Boolean = true,
    val user: PrivateUserDto? = null,
    val sub: SubscriptionDto? = null,
    val error: String? = null
)

class SettingsViewModel : ViewModel() {
    private val repo = ServiceLocator.repo
    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init { load() }

    fun load() = viewModelScope.launch {
        try {
            val u = repo.me()
            val s = runCatching { repo.subscription() }.getOrNull()
            _state.value = SettingsState(loading = false, user = u, sub = s)
        } catch (e: Exception) {
            _state.value = SettingsState(loading = false, error = e.message)
        }
    }
}
