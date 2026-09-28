package com.example.rhh.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rhh.ui.theme.Border
import com.example.rhh.ui.theme.Surface2
import com.example.rhh.ui.theme.TextDim
import com.example.rhh.ui.theme.TextPri
import com.example.rhh.util.Formatters

/**
 * Wave chart you can scrub with your finger.
 *
 *  - The wave is drawn with quadratic smoothness.
 *  - On drag, we find the nearest x-index and draw a dashed vertical guide
 *    plus a filled dot at that point.
 *  - A small floating tooltip shows the value at the scrubbed point.
 */
@Composable
fun WaveScrubChart(
    data: List<Pair<String, Double>>,   // (label, value)
    modifier: Modifier = Modifier,
    lineColor: Color = Color(0xFF3DDC84),
    format: (Double) -> String = { Formatters.ksh(it) }
) {
    if (data.isEmpty()) return

    var activeIndex by remember { mutableStateOf<Int?>(null) }

    val values = data.map { it.second }
    val minV = minOf(0.0, values.min())
    val maxV = maxOf(1.0, values.max())

    Box(modifier = modifier.fillMaxWidth().height(240.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp)
                .pointerInput(data) {
                    detectDragGestures(
                        onDragStart = { pos ->
                            activeIndex = nearestIndex(pos.x, size.width, data.size)
                        },
                        onDragEnd = { activeIndex = null },
                        onDragCancel = { activeIndex = null }
                    ) { change, _ ->
                        activeIndex = nearestIndex(change.position.x, size.width, data.size)
                    }
                }
        ) {
            val w = size.width
            val h = size.height
            val padTop = 24f
            val padBottom = 24f
            val chartH = h - padTop - padBottom
            val n = data.size

            val points = data.mapIndexed { i, (_, v) ->
                val x = if (n == 1) w / 2f else (i.toFloat() / (n - 1)) * w
                val y = padTop + chartH - (((v - minV) / (maxV - minV)).toFloat() * chartH)
                Offset(x, y)
            }

            // Smooth path
            val path = Path().apply {
                moveTo(points[0].x, points[0].y)
                for (i in 1 until points.size) {
                    val prev = points[i - 1]
                    val cur = points[i]
                    val midX = (prev.x + cur.x) / 2f
                    cubicTo(midX, prev.y, midX, cur.y, cur.x, cur.y)
                }
            }

            // Filled area under the wave
            val areaPath = Path().apply {
                addPath(path)
                lineTo(points.last().x, padTop + chartH)
                lineTo(points.first().x, padTop + chartH)
                close()
            }
            drawPath(
                path = areaPath,
                brush = Brush.verticalGradient(
                    colors = listOf(lineColor.copy(alpha = 0.35f), Color.Transparent)
                )
            )

            // Wave stroke
            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 4f)
            )

            // Scrub marker
            activeIndex?.let { idx ->
                val p = points[idx]
                drawLine(
                    color = TextPri.copy(alpha = 0.35f),
                    start = Offset(p.x, 0f),
                    end = Offset(p.x, h),
                    strokeWidth = 2f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 12f))
                )
                drawCircle(color = Color(0xFF0B0B0F), radius = 12f, center = p)
                drawCircle(color = lineColor, radius = 8f, center = p)
            }
        }

        // Floating tooltip
        activeIndex?.let { idx ->
            val (label, value) = data[idx]
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .padding(horizontal = 0.dp)
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .then(Modifier)
                        .padding(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .padding(0.dp)
                    ) {
                        androidx.compose.foundation.layout.Column(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .padding(0.dp)
                        ) {
                            // Real content box
                            androidx.compose.foundation.layout.Column(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .padding(0.dp)
                            ) {}
                        }
                    }
                }
            }
            // Simpler tooltip card
            androidx.compose.material3.Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 12.dp),
                color = Surface2,
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Border)
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(label, color = TextDim, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text(format(value), color = TextPri, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

private fun nearestIndex(x: Float, width: Int, n: Int): Int {
    if (n <= 1) return 0
    val f = (x / width).coerceIn(0f, 1f)
    return (f * (n - 1)).toInt().coerceIn(0, n - 1)
}
