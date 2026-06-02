package com.example.engine

import android.graphics.BlurMaskFilter
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun WatchRenderer(
    modifier: Modifier = Modifier,
    accentColor: Color = Color(0xFF00FFC2), // glowing neon cyan
    backgroundColor: Color = Color(0xFF12131A),
    watchStyle: String = "Sleek Gold",
    interactiveTimeAdjust: ((Int, Int) -> Unit)? = null
) {
    var currentTimeMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var isInteracting by remember { mutableStateOf(false) }
    var interactiveAngle by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(key1 = isInteracting) {
        while (!isInteracting) {
            currentTimeMillis = System.currentTimeMillis()
            kotlinx.coroutines.delay(16) // ~60fps smooth sweep
        }
    }

    val calendar = remember(currentTimeMillis) {
        Calendar.getInstance().apply { timeInMillis = currentTimeMillis }
    }

    val actualHour = calendar.get(Calendar.HOUR)
    val actualMinute = calendar.get(Calendar.MINUTE)
    val actualSecond = calendar.get(Calendar.SECOND)
    val actualMillisecond = calendar.get(Calendar.MILLISECOND)

    val sweepSecondPart = actualSecond + (actualMillisecond / 1000f)
    val secondAngle = sweepSecondPart * 6f

    val minuteAngle = if (isInteracting) {
        interactiveAngle
    } else {
        actualMinute * 6f + actualSecond * 0.1f
    }

    val hourAngle = if (isInteracting) {
        (interactiveAngle / 12f) % 360f
    } else {
        actualHour * 30f + actualMinute * 0.5f
    }

    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { _ -> isInteracting = true },
                        onDragEnd = { isInteracting = false },
                        onDragCancel = { isInteracting = false },
                        onDrag = { change, _ ->
                            val size = this.size
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val position = change.position
                            val rad = atan2(position.y - center.y, position.x - center.x)
                            var degree = (Math.toDegrees(rad.toDouble()).toFloat() + 90f)
                            if (degree < 0) degree += 360f
                            
                            interactiveAngle = degree

                            val estimatedMinute = ((degree / 6f).toInt()) % 60
                            val estimatedHour = ((((degree / 30f) * 2).toInt()) % 12).coerceAtLeast(1)
                            
                            interactiveTimeAdjust?.invoke(estimatedHour, estimatedMinute)
                        }
                    )
                }
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f * 0.9f

            if (watchStyle.contains("Sleek", ignoreCase = true) || watchStyle.contains("Minimal", ignoreCase = true)) {
                // ================= Sleek Gold / Minimalist Dial Style (from design guidelines) =================
                val startColor = Color(0xFF2A2D31)
                val endColor = Color(0xFF0A0B0D)
                val borderRingColor = Color(0xFF1C1F22)
                
                // Outer 3D bevel / border ring
                drawCircle(
                    color = borderRingColor,
                    radius = radius,
                    center = center
                )
                
                // Face background gradient
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(startColor, endColor),
                        center = center,
                        radius = radius * 0.96f
                    ),
                    radius = radius * 0.96f,
                    center = center
                )

                // Inner bezel glow/rim (white/5)
                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = radius * 0.85f,
                    center = center,
                    style = Stroke(width = 4f)
                )

                // 4 Compass Markers
                // 12 o'clock (Active glowing Accent, e.g., warm orange #FFB74D)
                val rad12 = -PI / 2.0
                val rStart12 = radius * 0.83f
                val rEnd12 = rStart12 - radius * 0.12f
                drawLine(
                    color = accentColor,
                    start = Offset((center.x + rStart12 * cos(rad12)).toFloat(), (center.y + rStart12 * sin(rad12)).toFloat()),
                    end = Offset((center.x + rEnd12 * cos(rad12)).toFloat(), (center.y + rEnd12 * sin(rad12)).toFloat()),
                    strokeWidth = 10f,
                    cap = StrokeCap.Round
                )

                // 3, 6, 9 o'clock (white/25)
                listOf(0.0 to 15, PI / 2.0 to 30, PI to 45).forEach { (rad, _) ->
                    val rStart = radius * 0.83f
                    val rEnd = rStart - radius * 0.12f
                    drawLine(
                        color = Color.White.copy(0.25f),
                        start = Offset((center.x + rStart * cos(rad)).toFloat(), (center.y + rStart * sin(rad)).toFloat()),
                        end = Offset((center.x + rEnd * cos(rad)).toFloat(), (center.y + rEnd * sin(rad)).toFloat()),
                        strokeWidth = 10f,
                        cap = StrokeCap.Round
                    )
                }

                // Inner text brand (extremely minimal)
                drawIntoCanvas { canvas ->
                    val brandPaint = Paint().asFrameworkPaint().apply {
                        isAntiAlias = true
                        color = Color.White.copy(alpha = 0.15f).toArgb()
                        textSize = radius * 0.06f
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.create("monospace", android.graphics.Typeface.NORMAL)
                    }
                    canvas.nativeCanvas.drawText("NEXVORA", center.x, center.y - radius * 0.25f, brandPaint)
                    canvas.nativeCanvas.drawText("CHRONO", center.x, center.y + radius * 0.28f, brandPaint)
                }

                // Hands Drawing
                // Hour Hand (Solid white)
                val hrRad = (hourAngle - 90f) * PI / 180f
                val hrLength = radius * 0.50f
                drawLine(
                    color = Color.White,
                    start = center,
                    end = Offset((center.x + hrLength * cos(hrRad)).toFloat(), (center.y + hrLength * sin(hrRad)).toFloat()),
                    strokeWidth = 14f,
                    cap = StrokeCap.Round
                )

                // Minute Hand (white/90)
                val minRad = (minuteAngle - 90f) * PI / 180f
                val minLength = radius * 0.75f
                drawLine(
                    color = Color.White.copy(alpha = 0.9f),
                    start = center,
                    end = Offset((center.x + minLength * cos(minRad)).toFloat(), (center.y + minLength * sin(minRad)).toFloat()),
                    strokeWidth = 9f,
                    cap = StrokeCap.Round
                )

                // Second Hand (accentColor, e.g. #FFB74D, hidden during interactive drag)
                if (!isInteracting) {
                    val secRad = (secondAngle - 90f) * PI / 180f
                    val secLength = radius * 0.85f
                    val tailEnd = Offset(
                        x = (center.x - (radius * 0.16f) * cos(secRad)).toFloat(),
                        y = (center.y - (radius * 0.16f) * sin(secRad)).toFloat()
                    )
                    drawLine(
                        color = accentColor,
                        start = tailEnd,
                        end = Offset((center.x + secLength * cos(secRad)).toFloat(), (center.y + secLength * sin(secRad)).toFloat()),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                }

                // Center crown cap: bg-[#1A1C1E] border-2 border-[#FFB74D]
                drawCircle(color = Color(0xFF1A1C1E), radius = 16f, center = center)
                drawCircle(color = accentColor, radius = 16f, center = center, style = Stroke(width = 4f))
                drawCircle(color = Color.White.copy(0.1f), radius = 6f, center = center)

            } else {
                // ================= Classic Theme Glowing Face Style =================
                // 1. Shadow & Metallic gradient backing (3D effect)
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            backgroundColor,
                            backgroundColor.copy(alpha = 0.85f),
                            Color.Black
                        ),
                        center = center,
                        radius = radius
                    ),
                    radius = radius,
                    center = center
                )

                // Inner glass rim
                drawCircle(
                    color = accentColor.copy(alpha = 0.08f),
                    radius = radius * 0.98f,
                    center = center,
                    style = Stroke(width = 8f)
                )

                drawCircle(
                    color = Color.White.copy(alpha = 0.05f),
                    radius = radius * 0.94f,
                    center = center,
                    style = Stroke(width = 3f)
                )

                // 2. Dial ticks
                for (i in 0 until 60) {
                    val angleRad = (i * 6 - 90) * PI / 180f
                    val isMajor = i % 5 == 0
                    val tickLength = if (isMajor) radius * 0.12f else radius * 0.05f
                    val tickWidth = if (isMajor) 4f else 1.5f
                    val tickColor = if (isMajor) accentColor else accentColor.copy(alpha = 0.3f)

                    val startRadius = radius * 0.88f
                    val endRadius = startRadius - tickLength

                    val start = Offset(
                        x = (center.x + startRadius * cos(angleRad)).toFloat(),
                        y = (center.y + startRadius * sin(angleRad)).toFloat()
                    )
                    val end = Offset(
                        x = (center.x + endRadius * cos(angleRad)).toFloat(),
                        y = (center.y + endRadius * sin(angleRad)).toFloat()
                    )

                    drawLine(
                        color = tickColor,
                        start = start,
                        end = end,
                        strokeWidth = tickWidth,
                        cap = StrokeCap.Round
                    )
                }

                // 3. Glowing typography
                drawIntoCanvas { canvas ->
                    val nativePaint = Paint().asFrameworkPaint().apply {
                        isAntiAlias = true
                        color = accentColor.toArgb()
                        textSize = radius * 0.16f
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.create("sans-serif-thin", android.graphics.Typeface.BOLD)
                    }

                    val blurPaint = Paint().asFrameworkPaint().apply {
                        isAntiAlias = true
                        color = accentColor.toArgb()
                        textSize = radius * 0.16f
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.create("sans-serif-thin", android.graphics.Typeface.BOLD)
                        maskFilter = BlurMaskFilter(15f, BlurMaskFilter.Blur.NORMAL)
                    }

                    // Render critical digits
                    val digits = listOf("12" to 12, "3" to 3, "6" to 6, "9" to 9)
                    for ((label, valNum) in digits) {
                        val angleRad = (valNum * 30 - 90) * PI / 180f
                        val numRadius = radius * 0.65f
                        val x = (center.x + numRadius * cos(angleRad)).toFloat()
                        val y = (center.y + numRadius * sin(angleRad)).toFloat() + (nativePaint.textSize / 3f)

                        canvas.nativeCanvas.drawText(label, x, y, blurPaint)
                        canvas.nativeCanvas.drawText(label, x, y, nativePaint)
                    }

                    // Brand Labels
                    val brandPaint = Paint().asFrameworkPaint().apply {
                        isAntiAlias = true
                        color = Color.White.copy(alpha = 0.40f).toArgb()
                        textSize = radius * 0.075f
                        textAlign = android.graphics.Paint.Align.CENTER
                        typeface = android.graphics.Typeface.DEFAULT_BOLD
                    }
                    canvas.nativeCanvas.drawText("NEXTIME 3D", center.x, center.y - radius * 0.32f, brandPaint)
                    
                    val statusPaint = Paint().asFrameworkPaint().apply {
                        isAntiAlias = true
                        color = accentColor.copy(alpha = 0.6f).toArgb()
                        textSize = radius * 0.055f
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    canvas.nativeCanvas.drawText("OFFLINE AI OS", center.x, center.y + radius * 0.35f, statusPaint)
                }

                // 4. Hour Hand
                val hrRad = (hourAngle - 90f) * PI / 180f
                val hrLength = radius * 0.46f
                val hrEnd = Offset(
                    x = (center.x + hrLength * cos(hrRad)).toFloat(),
                    y = (center.y + hrLength * sin(hrRad)).toFloat()
                )
                drawLine(
                    color = Color.White,
                    start = center,
                    end = hrEnd,
                    strokeWidth = 12f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = accentColor,
                    start = center,
                    end = hrEnd,
                    strokeWidth = 5f,
                    cap = StrokeCap.Round
                )

                // 5. Minute Hand
                val minRad = (minuteAngle - 90f) * PI / 180f
                val minLength = radius * 0.72f
                val minEnd = Offset(
                    x = (center.x + minLength * cos(minRad)).toFloat(),
                    y = (center.y + minLength * sin(minRad)).toFloat()
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.9f),
                    start = center,
                    end = minEnd,
                    strokeWidth = 8f,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = accentColor,
                    start = center,
                    end = minEnd,
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )

                // 6. Orange Sweeping Second Hand (Hidden during manual drag adjustment)
                if (!isInteracting) {
                    val secRad = (secondAngle - 90f) * PI / 180f
                    val secLength = radius * 0.82f
                    val secEnd = Offset(
                        x = (center.x + secLength * cos(secRad)).toFloat(),
                        y = (center.y + secLength * sin(secRad)).toFloat()
                    )
                    val tailEnd = Offset(
                        x = (center.x - (radius * 0.16f) * cos(secRad)).toFloat(),
                        y = (center.y - (radius * 0.16f) * sin(secRad)).toFloat()
                    )
                    drawLine(
                        color = Color(0xFFFF4848), // Sweep Ember
                        start = tailEnd,
                        end = secEnd,
                        strokeWidth = 2.5f,
                        cap = StrokeCap.Round
                    )
                }

                // 7. Center Crown Cap
                drawCircle(color = Color.Black, radius = 15f, center = center)
                drawCircle(color = Color.White, radius = 9f, center = center)
                drawCircle(color = accentColor, radius = 5f, center = center)
            }
        }
    }
}
