package com.example.rhh.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rhh.data.model.TripDto
import com.example.rhh.ui.theme.Accent
import com.example.rhh.ui.theme.Bg
import com.example.rhh.ui.theme.Border
import com.example.rhh.ui.theme.Loss
import com.example.rhh.ui.theme.Profit
import com.example.rhh.ui.theme.Surface
import com.example.rhh.ui.theme.TextDim
import com.example.rhh.ui.theme.TextPri
import com.example.rhh.util.Formatters

@Composable
fun RecordsScreen(
    trips: List<TripDto>,
    loading: Boolean
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Bg)
    ) {
        Text(
            "Trips",
            color = TextPri,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(start = 20.dp, top = 12.dp, bottom = 12.dp)
        )
        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Accent)
            }
        } else if (trips.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No trips yet.", color = TextDim)
            }
        } else {
            LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(20.dp, 0.dp, 20.dp, 40.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(trips) { t -> TripCard(t) }
            }
        }
    }
}

@Composable
private fun TripCard(t: TripDto) {
    val profit = t.profit ?: 0.0
    val color = if (profit >= 0) Profit else Loss
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Surface)
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(t.platform ?: "—", color = TextPri, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(Formatters.ksh(profit), color = color, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "${t.paymentMode ?: "—"} · ${t.status ?: ""}",
                    color = TextDim, fontSize = 13.sp
                )
                Text(Formatters.km(t.distance), color = TextDim, fontSize = 13.sp)
            }
        }
    }
}
