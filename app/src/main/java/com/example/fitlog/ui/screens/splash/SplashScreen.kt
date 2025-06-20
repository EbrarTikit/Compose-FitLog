package com.example.fitlog.ui.screens.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fitlog.ui.navigation.ScreenRoute
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.fitlog.data.local.preferences.UserPreferences
import com.google.firebase.auth.FirebaseAuth

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    // Define colors
    val greenPrimary = Color(0xFF2E7D32)
    val greenSecondary = Color(0xFF43A047)
    val blueColor = Color(0xFF1565C0)
    val redColor = Color(0xFFE53935)
    val goldColor = Color(0xFFFFa000)
    val purpleColor = Color(0xFF7B1FA2)

    // Animation values
    val outerCircleAnimatable = remember { Animatable(0f) }
    val innerCircleAnimatable = remember { Animatable(0f) }
    val dumbbellBarAnimatable = remember { Animatable(0f) }
    val dumbbellWeightsAnimatable = remember { Animatable(0f) }
    val heartRateAnimatable = remember { Animatable(0f) }
    val calendarAnimatable = remember { Animatable(0f) }
    val stopwatchAnimatable = remember { Animatable(0f) }
    val textAlphaAnimatable = remember { Animatable(0f) }

    // Animation sequence
    LaunchedEffect(Unit) {
        // Outer circle animation
        outerCircleAnimatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 600, easing = LinearEasing)
        )

        // Inner circle animation
        innerCircleAnimatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500, easing = LinearEasing)
        )

        // Dumbbell bar animation
        dumbbellBarAnimatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400, easing = LinearEasing)
        )

        // Weights animation
        dumbbellWeightsAnimatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500, easing = LinearEasing)
        )

        // Calendar and pulse animations in parallel
        coroutineScope.launch {
            calendarAnimatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 600, easing = LinearEasing)
            )
        }
        coroutineScope.launch {
            delay(100)
            heartRateAnimatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 800, easing = LinearOutSlowInEasing)
            )
        }

        // Stopwatch animation
        delay(200)
        stopwatchAnimatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500, easing = LinearEasing)
        )

        // Text fade-in animation
        textAlphaAnimatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 400, easing = LinearEasing)
        )

        // Wait before navigating
        delay(600)

        coroutineScope.launch {
            val isFirstLaunch = userPreferences.isFirstLaunch()
            val isLoggedIn = FirebaseAuth.getInstance().currentUser != null
            when {
                isFirstLaunch -> navController.navigate(ScreenRoute.Onboarding.route) { popUpTo(0) { inclusive = true } }
                isLoggedIn -> navController.navigate(ScreenRoute.Home.route) { popUpTo(0) { inclusive = true } }
                else -> navController.navigate(ScreenRoute.SignIn.route) { popUpTo(0) { inclusive = true } }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Draw animated logo
        Canvas(
            modifier = Modifier.size(240.dp)
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val center = Offset(canvasWidth / 2f, canvasHeight / 2f)

            // Outer circle
            if (outerCircleAnimatable.value > 0) {
                val outerCirclePath = Path().apply {
                    addOval(
                        Rect(
                            center.x - canvasWidth * 0.45f,
                            center.y - canvasHeight * 0.45f,
                            center.x + canvasWidth * 0.45f,
                            center.y + canvasHeight * 0.45f
                        )
                    )
                }

                val pathPortion = PathMeasure().apply {
                    setPath(outerCirclePath, true)
                }
                val pathLength = pathPortion.length
                val extractedPath = Path()
                pathPortion.getSegment(
                    0f,
                    pathLength * outerCircleAnimatable.value,
                    extractedPath,
                    true
                )

                drawPath(
                    path = extractedPath,
                    color = greenPrimary,
                    style = Stroke(width = 4f, join = StrokeJoin.Round)
                )
            }

            // Inner circle - fitness tracker
            if (innerCircleAnimatable.value > 0) {
                val innerCirclePath = Path().apply {
                    addOval(
                        Rect(
                            center.x - canvasWidth * 0.35f,
                            center.y - canvasHeight * 0.35f,
                            center.x + canvasWidth * 0.35f,
                            center.y + canvasHeight * 0.35f
                        )
                    )
                }

                val pathPortion = PathMeasure().apply {
                    setPath(innerCirclePath, true)
                }
                val pathLength = pathPortion.length
                val extractedPath = Path()
                pathPortion.getSegment(
                    0f,
                    pathLength * innerCircleAnimatable.value,
                    extractedPath,
                    true
                )

                drawPath(
                    path = extractedPath,
                    color = greenSecondary,
                    style = Stroke(width = 3f)
                )
            }

            // Dumbbell bar
            if (dumbbellBarAnimatable.value > 0) {
                val barStart = Offset(center.x - canvasWidth * 0.19f, center.y)
                val barEnd =
                    Offset(center.x + canvasWidth * 0.19f * dumbbellBarAnimatable.value, center.y)

                drawLine(
                    color = blueColor,
                    start = barStart,
                    end = barEnd,
                    strokeWidth = 10f,
                    cap = StrokeCap.Round
                )
            }

            // Dumbbell weights
            if (dumbbellWeightsAnimatable.value > 0) {
                // Left outer weight
                drawRect(
                    color = blueColor,
                    topLeft = Offset(
                        center.x - canvasWidth * 0.25f,
                        center.y - canvasHeight * 0.1f
                    ),
                    size = Size(
                        canvasWidth * 0.06f,
                        canvasHeight * 0.2f * dumbbellWeightsAnimatable.value
                    )
                )

                // Left inner weight
                drawRect(
                    color = blueColor,
                    topLeft = Offset(
                        center.x - canvasWidth * 0.29f,
                        center.y - canvasHeight * 0.08f
                    ),
                    size = Size(
                        canvasWidth * 0.04f,
                        canvasHeight * 0.16f * dumbbellWeightsAnimatable.value
                    )
                )

                // Right outer weight
                drawRect(
                    color = blueColor,
                    topLeft = Offset(
                        center.x + canvasWidth * 0.19f,
                        center.y - canvasHeight * 0.1f
                    ),
                    size = Size(
                        canvasWidth * 0.06f,
                        canvasHeight * 0.2f * dumbbellWeightsAnimatable.value
                    )
                )

                // Right inner weight
                drawRect(
                    color = blueColor,
                    topLeft = Offset(
                        center.x + canvasWidth * 0.25f,
                        center.y - canvasHeight * 0.08f
                    ),
                    size = Size(
                        canvasWidth * 0.04f,
                        canvasHeight * 0.16f * dumbbellWeightsAnimatable.value
                    )
                )
            }

            // Heart rate line
            if (heartRateAnimatable.value > 0) {
                val heartRatePath = Path().apply {
                    moveTo(center.x - canvasWidth * 0.27f, center.y)
                    lineTo(center.x - canvasWidth * 0.19f, center.y)
                    lineTo(center.x - canvasWidth * 0.15f, center.y - canvasHeight * 0.12f)
                    lineTo(center.x - canvasWidth * 0.08f, center.y + canvasHeight * 0.12f)
                    lineTo(center.x, center.y - canvasHeight * 0.08f)
                    lineTo(center.x + canvasWidth * 0.08f, center.y + canvasHeight * 0.08f)
                    lineTo(center.x + canvasWidth * 0.12f, center.y)
                    lineTo(center.x + canvasWidth * 0.19f, center.y)
                    lineTo(center.x + canvasWidth * 0.27f, center.y)
                }

                val pathPortion = PathMeasure().apply {
                    setPath(heartRatePath, false)
                }
                val pathLength = pathPortion.length
                val extractedPath = Path()
                pathPortion.getSegment(
                    0f,
                    pathLength * heartRateAnimatable.value,
                    extractedPath,
                    true
                )

                drawPath(
                    path = extractedPath,
                    color = redColor,
                    style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }

            // Calendar
            if (calendarAnimatable.value > 0) {
                // Calendar frame
                val calendarPath = Path().apply {
                    addRect(
                        Rect(
                            center.x - canvasWidth * 0.15f,
                            center.y - canvasHeight * 0.31f,
                            center.x + canvasWidth * 0.15f,
                            center.y - canvasHeight * 0.23f
                        )
                    )
                }

                val pathPortion = PathMeasure().apply {
                    setPath(calendarPath, true)
                }
                val pathLength = pathPortion.length
                val extractedPath = Path()
                pathPortion.getSegment(
                    0f,
                    pathLength * calendarAnimatable.value,
                    extractedPath,
                    true
                )

                drawPath(
                    path = extractedPath,
                    color = goldColor,
                    style = Stroke(width = 3f)
                )

                // Calendar dots
                val dotRadius = 5f * calendarAnimatable.value

                // Dot 1
                drawCircle(
                    color = goldColor,
                    radius = dotRadius,
                    center = Offset(center.x - canvasWidth * 0.08f, center.y - canvasHeight * 0.27f)
                )

                // Dot 2
                drawCircle(
                    color = goldColor,
                    radius = dotRadius,
                    center = Offset(center.x, center.y - canvasHeight * 0.27f)
                )

                // Dot 3
                drawCircle(
                    color = goldColor,
                    radius = dotRadius,
                    center = Offset(center.x + canvasWidth * 0.08f, center.y - canvasHeight * 0.27f)
                )
            }

            // Stopwatch
            if (stopwatchAnimatable.value > 0) {
                // Stopwatch circle
                val watchPath = Path().apply {
                    addOval(
                        Rect(
                            center.x - canvasWidth * 0.08f,
                            center.y + canvasHeight * 0.19f,
                            center.x + canvasWidth * 0.08f,
                            center.y + canvasHeight * 0.35f
                        )
                    )
                }

                val pathPortion = PathMeasure().apply {
                    setPath(watchPath, true)
                }
                val pathLength = pathPortion.length
                val extractedWatchPath = Path()
                pathPortion.getSegment(
                    0f,
                    pathLength * stopwatchAnimatable.value,
                    extractedWatchPath,
                    true
                )

                drawPath(
                    path = extractedWatchPath,
                    color = purpleColor,
                    style = Stroke(width = 3f)
                )

                // Stopwatch top
                val stopwatchTop = Path().apply {
                    moveTo(center.x, center.y + canvasHeight * 0.19f)
                    lineTo(center.x, center.y + canvasHeight * 0.17f)
                    lineTo(center.x - canvasWidth * 0.02f, center.y + canvasHeight * 0.15f)
                    lineTo(center.x + canvasWidth * 0.02f, center.y + canvasHeight * 0.15f)
                    lineTo(center.x, center.y + canvasHeight * 0.17f)
                    close()
                }

                drawPath(
                    path = stopwatchTop,
                    color = purpleColor,
                    alpha = stopwatchAnimatable.value
                )

                // Stopwatch hand
                drawLine(
                    color = purpleColor,
                    start = Offset(center.x, center.y + canvasHeight * 0.27f),
                    end = Offset(
                        center.x,
                        center.y + canvasHeight * 0.27f - (canvasHeight * 0.06f * stopwatchAnimatable.value)
                    ),
                    strokeWidth = 3f,
                    cap = StrokeCap.Round
                )
            }
        }

        // App name
        Text(
            text = "FitLog",
            color = MaterialTheme.colorScheme.primary.copy(alpha = textAlphaAnimatable.value),
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }
}