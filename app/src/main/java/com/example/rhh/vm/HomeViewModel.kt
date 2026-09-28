package com.example.rhh.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rhh.ServiceLocator
import com.example.rhh.data.model.HourBucket
import com.example.rhh.data.model.RecordDto
import com.example.rhh.data.model.TripDto
import com.example.rhh.util.TimeUtil
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class HomeState(
    val loading: Boolean = true,
    val refreshing: Boolean = false,
    val error: String? = null,
    val trips: List<TripDto> = emptyList(),
    val records: List<RecordDto> = emptyList(),
    val hourlyBuckets: List<HourBucket> = List(24) { HourBucket(it, 0.0, 0) },
    val todayProfit: Double = 0.0,
    val todayTrips: Int = 0
)

class HomeViewModel : ViewModel() {
    private val repo = ServiceLocator.repo
    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init { refresh() }

    fun refresh() = viewModelScope.launch {
        _state.value = _state.value.copy(loading = _state.value.trips.isEmpty(), refreshing = true, error = null)
        try {
            val trips = repo.trips()
            val records = repo.records()
            val buckets = bucketToday(trips, records)
            val profit = buckets.sumOf { it.netProfit }
            val tripsToday = trips.count {
                TimeUtil.sameDay(parseIso(it.startTime)) && it.status == "completed"
            }
            _state.value = _state.value.copy(
                loading = false, refreshing = false,
                trips = trips, records = records,
                hourlyBuckets = buckets,
                todayProfit = profit,
                todayTrips = tripsToday
            )
        } catch (e: Exception) {
            _state.value = _state.value.copy(loading = false, refreshing = false, error = e.message)
        }
    }

    private fun bucketToday(trips: List<TripDto>, records: List<RecordDto>): List<HourBucket> {
        val nets = DoubleArray(24)
        val counts = IntArray(24)
        val today = System.currentTimeMillis()

        trips.forEach { t ->
            val ms = parseIso(t.startTime)
            if (TimeUtil.sameDay(ms, today) && t.status == "completed") {
                val h = Calendar.getInstance().apply { timeInMillis = ms }.get(Calendar.HOUR_OF_DAY)
                nets[h] += (t.profit ?: 0.0)
                counts[h] += 1
            }
        }
        records.forEach { r ->
            val ms = parseIso(r.date)
            if (TimeUtil.sameDay(ms, today)) {
                val h = Calendar.getInstance().apply { timeInMillis = ms }.get(Calendar.HOUR_OF_DAY)
                val amt = r.amount ?: 0.0
                nets[h] += if (r.recordtype == "expense") -amt else amt
            }
        }
        return List(24) { HourBucket(it, nets[it], counts[it]) }
    }

    private fun parseIso(s: String?): Long {
        if (s.isNullOrBlank()) return 0L
        return try {
            // Backend returns ISO 8601 (e.g. 2026-09-27T10:00:00.000Z)
            val cleaned = s.replace("Z", "+0000").substringBefore(".") + "Z"
            // try a couple of formats
            val fmts = listOf(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            )
            fmts.forEach { f ->
                try { return f.parse(s.replace("Z", "Z")).time } catch (_: Exception) {}
            }
            // fall back
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US).parse(s)?.time ?: 0L
        } catch (_: Exception) { 0L }
    }
}
