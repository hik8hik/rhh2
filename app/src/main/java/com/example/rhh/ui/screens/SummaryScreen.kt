package com.example.rhh.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.rhh.vm.SummaryPoint
import com.example.rhh.ui.components.WaveScrubChart
import com.example.rhh.ui.theme.Accent
import com.example.rhh.ui.theme.Bg
import com.example.rhh.ui.theme.Loss
import com.example.rhh.ui.theme.Profit
import com.example.rhh.ui.theme.Surface
import com.example.rhh.ui.theme.TextDim
import com.example.rhh.ui.theme.TextPri
import com.example.rhh.util.Formatters

@Composable
fun SummaryScreen(
    loading: Boolean,
    series: List<SummaryPoint>,
    total: Double
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Bg)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 40.dp)
    ) {
        Text(
            "Summary",
            color = TextPri,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(start = 20.dp, top = 12.dp)
        )
        Text(
            "Drag across the wave to scrub the last 14 days.",
            color = TextDim, fontSize = 13.sp,
            modifier = Modifier.padding(start = 20.dp, top = 6.dp, end = 20.dp)
        )

        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Accent)
            }
        } else {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(Surface)
                    .padding(8.dp)
            ) {
                WaveScrubChart(
                    data = series.map { it.label to it.value },
                    lineColor = if (total >= 0) Profit else Loss
                )
            }
            Text("14-day total", color = TextDim, fontSize = 12.sp, modifier = Modifier.padding(start = 20.dp, top = 12.dp))
            Text(
                Formatters.ksh(total),
                color = if (total >= 0) Profit else Loss,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(start = 20.dp, top = 4.dp)
            )
        }
    }
}
