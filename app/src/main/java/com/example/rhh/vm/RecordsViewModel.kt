package com.example.rhh.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rhh.ServiceLocator
import com.example.rhh.data.model.TripDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecordsState(
    val loading: Boolean = true,
    val refreshing: Boolean = false,
    val error: String? = null,
    val trips: List<TripDto> = emptyList()
)

class RecordsViewModel : ViewModel() {
    private val repo = ServiceLocator.repo
    private val _state = MutableStateFlow(RecordsState())
    val state: StateFlow<RecordsState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        _state.value = _state.value.copy(loading = _state.value.trips.isEmpty(), refreshing = true)
        try {
            _state.value = _state.value.copy(
                loading = false, refreshing = false,
                trips = repo.trips(), error = null
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(loading = false, refreshing = false, error = e.message)
        }
    }
}
