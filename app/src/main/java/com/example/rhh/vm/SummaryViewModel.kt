package com.example.rhh.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rhh.ServiceLocator
import com.example.rhh.data.model.TripDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class SummaryPoint(val label: String, val value: Double)
data class SummaryState(
    val loading: Boolean = true,
    val error: String? = null,
    val series: List<SummaryPoint> = emptyList(),
    val total: Double = 0.0
)

class SummaryViewModel : ViewModel() {
    private val repo = ServiceLocator.repo
    private val _state = MutableStateFlow(SummaryState())
    val state: StateFlow<SummaryState> = _state.asStateFlow()

    init { load() }

    fun load() = viewModelScope.launch {
        try {
            val trips = repo.trips()
            _state.value = buildSeries(trips)
        } catch (e: Exception) {
            _state.value = SummaryState(loading = false, error = e.message)
        }
    }

    private fun buildSeries(trips: List<TripDto>): SummaryState {
        val today = Calendar.getInstance()
        val days = mutableListOf<Pair<Calendar, Double>>()
        for (i in 13 downTo 0) {
            val c = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -i) }
            days.add(c to 0.0)
        }
        val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        trips.forEach { t ->
            if (t.status != "completed") return@forEach
            val s = t.startTime ?: return@forEach
            val parsed = try {
                val cleaned = s.replace("Z", "").substringBefore(".")
                fmt.parse(cleaned)
            } catch (_: Exception) { null } ?: return@forEach

            val tc = Calendar.getInstance().apply { time = parsed }
            days.forEachIndexed { idx, (d, v) ->
                if (d.get(Calendar.YEAR) == tc.get(Calendar.YEAR) &&
                    d.get(Calendar.DAY_OF_YEAR) == tc.get(Calendar.DAY_OF_YEAR)) {
                    days[idx] = d to (v + (t.profit ?: 0.0))
                }
            }
        }
        val series = days.map { (c, v) ->
            val label = SimpleDateFormat("d MMM", Locale.US).format(c.time)
            SummaryPoint(label, v)
        }
        return SummaryState(loading = false, series = series, total = series.sumOf { it.value })
    }
}
