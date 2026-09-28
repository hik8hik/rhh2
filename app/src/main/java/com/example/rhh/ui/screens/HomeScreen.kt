package com.example.rhh.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rhh.data.model.HourBucket
import com.example.rhh.ui.components.DayTrack
import com.example.rhh.ui.components.GradientButton
import com.example.rhh.ui.theme.Accent
import com.example.rhh.ui.theme.Bg
import com.example.rhh.ui.theme.Border
import com.example.rhh.ui.theme.Surface
import com.example.rhh.ui.theme.TextDim
import com.example.rhh.ui.theme.TextPri
import com.example.rhh.util.Formatters
import com.example.rhh.util.TimeUtil
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    buckets: List<HourBucket>,
    todayProfit: Double,
    todayTrips: Int,
    refreshing: Boolean,
    onRefresh: () -> Unit,
    onStartTracking: () -> Unit
) {
    // live countdown
    var secondsLeft by remember { mutableStateOf(TimeUtil.secondsLeftToday()) }
    LaunchedEffect(Unit) {
        while (true) {
            secondsLeft = TimeUtil.secondsLeftToday()
            delay(1000)
        }
    }
    val h = secondsLeft / 3600
    val m = (secondsLeft % 3600) / 60
    val s = secondsLeft % 60

    Box(modifier = Modifier.fillMaxSize().background(Bg)) {
        // Ambient yellow gradient top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(560.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Accent.copy(alpha = 0.18f),
                            Accent.copy(alpha = 0.05f),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 40.dp)
        ) {
            // Watermark text
            Text(
                "belair",
                color = Accent.copy(alpha = 0.10f),
                fontSize = 130.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-8).sp,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )

            // Stat card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Surface.copy(alpha = 0.6f))
                    .padding(20.dp)
            ) {
                Column {
                    Text("Today", color = Accent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(
                        Formatters.dateShort(System.currentTimeMillis()),
                        color = TextPri,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        "$todayTrips trip${if (todayTrips == 1) "" else "s"} · ${Formatters.ksh(todayProfit)}",
                        color = TextDim,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            Spacer(Modifier.height(26.dp))

            // Countdown
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text("Day ends in", color = TextDim, fontSize = 13.sp)
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Unit(h.toString().padStart(2, '0'), "Hours", Modifier.weight(1f))
                    Unit(m.toString().padStart(2, '0'), "Minutes", Modifier.weight(1f))
                    Unit(s.toString().padStart(2, '0'), "Seconds", Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(16.dp))

            // The hero — 24h track
            DayTrack(buckets = buckets)

            Spacer(Modifier.height(12.dp))

            // CTA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Surface.copy(alpha = 0.55f))
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Start Live Tracking", color = TextPri, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
                    Text(
                        "Now ${TimeUtil.clockString()} · background GPS",
                        color = TextDim, fontSize = 13.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                GradientButton(
                    text = "→",
                    onClick = onStartTracking,
                    colors = listOf(Color.White, Color(0xFFE8E8E8))
                )
            }
        }
    }
}

@Composable
private fun Unit(num: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(num, color = TextPri, fontSize = 52.sp, fontWeight = FontWeight.Light)
        Text(label, color = TextDim, fontSize = 12.sp)
    }
}
