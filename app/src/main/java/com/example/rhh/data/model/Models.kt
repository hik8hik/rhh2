package com.example.rhh.data.model

import com.google.gson.annotations.SerializedName

// ---------- Auth ----------

data class RegisterRequest(val username: String, val email: String, val password: String)
data class LoginRequest(val username: String? = null, val email: String? = null, val password: String)
data class AuthResponse(val success: Boolean?, val token: String?)

// ---------- Private user ----------

data class PrivateUserDto(val _id: String?, val username: String?, val email: String?)
data class PrivateUserEnvelope(val success: Boolean?, val data: PrivateUserDto?)

// ---------- Trips ----------

data class TripDto(
    val _id: String,
    val platform: String?,
    val paymentMode: String?,
    val startOdometer: Double?,
    val endOdometer: Double?,
    val startBalance: Double?,
    val endBalance: Double?,
    val distance: Double?,
    val revenue: Double?,
    val fuelCost: Double?,
    val profit: Double?,
    val commission: Double?,
    val tip: Double?,
    val startTime: String?,
    val endTime: String?,
    val status: String?
)

data class TripsEnvelope(val success: Boolean?, val data: List<TripDto>?)

// ---------- Records ----------

data class RecordDto(
    val _id: String,
    val recordtype: String?,
    val toorfrom: String?,
    val amount: Double?,
    val paid: Double?,
    val currency: String?,
    val description: String?,
    val date: String?,
    val code: String?
)

data class RecordsEnvelope(val success: Boolean?, val data: List<RecordDto>?)

// ---------- Subscription ----------

data class SubscriptionDto(
    val tier: String?,
    val status: String?,
    val isTrial: Boolean?,
    val trialUsed: Boolean?,
    val trialEnd: String?,
    val currentPeriodEnd: String?,
    val eligibleForTrial: Boolean?,
    val trialClaimHint: String?
)

data class SubscriptionEnvelope(
    val success: Boolean?,
    val subscription: SubscriptionDto?,
    val eligibleForTrial: Boolean?,
    val trialClaimHint: String?
)

// ---------- Generic ----------

data class SimpleEnvelope(val success: Boolean?, val data: String?)

// ---------- UI-only ----------

/** One-hour bucket derived from trips/records. */
data class HourBucket(
    val hour: Int,
    val netProfit: Double,
    val trips: Int
)
