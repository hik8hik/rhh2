package com.example.rhh.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rhh.data.model.PrivateUserDto
import com.example.rhh.data.model.SubscriptionDto
import com.example.rhh.ui.theme.Accent
import com.example.rhh.ui.theme.Bg
import com.example.rhh.ui.theme.Border
import com.example.rhh.ui.theme.Loss
import com.example.rhh.ui.theme.Surface
import com.example.rhh.ui.theme.TextDim
import com.example.rhh.ui.theme.TextPri

@Composable
fun SettingsScreen(
    user: PrivateUserDto?,
    sub: SubscriptionDto?,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("Settings", color = TextPri, style = androidx.compose.material3.MaterialTheme.typography.headlineLarge)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Surface)
                .padding(18.dp)
        ) {
            Text("Signed in as", color = TextDim, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(user?.username ?: "—", color = TextPri, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(top = 6.dp))
            Text(user?.email ?: "", color = TextDim, fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
        }

        Column(modifier = Modifier.padding(top = 20.dp)) {
            val status = when {
                sub?.isTrial == true -> "Trial · ${(sub.tier ?: "").uppercase()}"
                sub != null -> "${(sub.tier ?: "free").uppercase()} · ${sub.status ?: ""}"
                else -> "—"
            }
            RowItem("Subscription", status) { /* nav to subscription */ }
            RowItem("Vehicles", "") {}
            RowItem("Fuel price", "") {}
            RowItem("Export data", "") {}
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Loss.copy(alpha = 0.12f))
                .clickable { onSignOut() }
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Sign out", color = Loss, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun RowItem(label: String, trailing: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextPri, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        if (trailing.isNotEmpty()) Text(trailing, color = Accent, fontSize = 14.sp)
        Text("  ›", color = TextDim, fontSize = 22.sp)
    }
}
