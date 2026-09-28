package com.example.rhh.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.rhh.data.model.HourBucket
import com.example.rhh.ui.theme.Accent
import com.example.rhh.ui.theme.Bg
import com.example.rhh.ui.theme.Loss
import com.example.rhh.ui.theme.Neutral
import com.example.rhh.ui.theme.Profit
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

/**
 * The hero component: a semicircular 24-hour rail.
 *
 *  - Each hour is a 7.5° arc segment.
 *  - Segment color reflects that hour's net profit / loss:
 *      big profit -> bright green, small profit -> dim green
 *      big loss   -> bright red,   small loss   -> dim red
 *      no data    -> grey
 *  - A yellow "car" drives from midnight to the current time on mount,
 *    using an [Animatable] tween for the smooth animation.
 */
@Composable
fun DayTrack(
    buckets: List<HourBucket>,
    modifier: Modifier = Modifier
) {
    val carProgress = remember { Animatable(0f) }

    LaunchedEffect(buckets) {
        carProgress.snapTo(0f)
        carProgress.animateTo(
            targetValue = currentFractionOfDay(),
            animationSpec = tween(durationMillis = 2200, easing = FastOutSlowInEasing)
        )
    }

    val maxAbs = remember(buckets) {
        val m = buckets.maxOfOrNull { abs(it.netProfit) } ?: 0.0
        if (m < 1.0) 1.0 else m
    }

    Box(modifier = modifier.fillMaxWidth().height(260.dp)) {
        Canvas(modifier = Modifier.fillMaxWidth().height(260.dp)) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h - 20f                 // arc baseline
            val radius = w * 0.44f
            val strokeWidth = 14.dp.toPx()

            // Arc bounding box: our drawArc must start at 180° and sweep 180°.
            // Compose measures angles clockwise from 3 o'clock (positive X axis).
            val arcRect = androidx.compose.ui.geometry.Rect(
                left = cx - radius,
                top = cy - radius,
                right = cx + radius,
                bottom = cy + radius
            )

            // 1. base rail
            drawArc(
                color = Neutral,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = arcRect.topLeft,
                size = Size(arcRect.width, arcRect.height),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 2. hourly segments
            val perHour = 180f / 24f
            val gap = 0.9f
            buckets.forEach { b ->
                val color = hourColor(b.netProfit, maxAbs)
                drawArc(
                    color = color,
                    startAngle = 180f + b.hour * perHour + gap / 2f,
                    sweepAngle = perHour - gap,
                    useCenter = false,
                    topLeft = arcRect.topLeft,
                    size = Size(arcRect.width, arcRect.height),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // 3. glow under the car
            val carAngleRad = (PI * (1f + carProgress.value)).toFloat()
            val carX = cx + radius * cos(carAngleRad)
            val carY = cy + radius * sin(carAngleRad)
            drawCircle(
                color = Accent.copy(alpha = 0.22f),
                radius = 22f,
                center = Offset(carX, carY)
            )

            // 4. car dot
            drawCircle(color = Accent, radius = 9f, center = Offset(carX, carY))
            drawCircle(color = Bg, radius = 4f, center = Offset(carX, carY))
        }
    }
}

private fun currentFractionOfDay(): Float {
    val cal = java.util.Calendar.getInstance()
    val seconds = cal.get(java.util.Calendar.HOUR_OF_DAY) * 3600 +
                  cal.get(java.util.Calendar.MINUTE) * 60 +
                  cal.get(java.util.Calendar.SECOND)
    return seconds.toFloat() / 86400f
}

private fun hourColor(net: Double, maxAbs: Double): Color {
    if (net == 0.0) return Neutral
    val t = (abs(net) / maxAbs).coerceIn(0.0, 1.0)
    return if (net > 0) {
        when {
            t > 0.66 -> Profit
            t > 0.33 -> Profit.copy(alpha = 0.72f)
            else     -> Profit.copy(alpha = 0.42f)
        }
    } else {
        when {
            t > 0.66 -> Loss
            t > 0.33 -> Loss.copy(alpha = 0.72f)
            else     -> Loss.copy(alpha = 0.42f)
        }
    }
}
