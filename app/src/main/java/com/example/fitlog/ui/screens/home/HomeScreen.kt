package com.example.fitlog.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlog.R
import com.example.fitlog.data.model.ActivityStats
import com.example.fitlog.data.model.DailyPlan
import com.example.fitlog.data.model.WorkoutSummary


@Composable
fun HomeScreen() {
    val userName = "Linh!"
    val currentDate = "Thursday, 08 July"
    val dailyPlan = DailyPlan("Thursday", "Upper Body", "2")
    val activityStats = ActivityStats(calories = 620.68f, steps = 1240)
    val recentWorkouts = listOf(
        WorkoutSummary(
            id = "1",
            name = "FullBody Workout",
            calories = 180,
            duration = 20,
            image = R.drawable.ic_run,
            progress = 0.7f
        ),
        WorkoutSummary(
            id = "2",
            name = "FullBody Workout",
            calories = 180,
            duration = 20,
            image = R.drawable.ic_run,
            progress = 0.5f
        )
    )

    HomeContent(
        userName = userName,
        currentDate = currentDate,
        dailyPlan = dailyPlan,
        activityStats = activityStats,
        recentWorkouts = recentWorkouts,
        onCheckWorkoutClick = {}
    )
}

@Composable
private fun HomeContent(
    userName: String,
    currentDate: String,
    dailyPlan: DailyPlan,
    activityStats: ActivityStats,
    recentWorkouts: List<WorkoutSummary>,
    onCheckWorkoutClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp)
        ) {
            UserGreetingSection(userName, currentDate)

            Spacer(modifier = Modifier.height(24.dp))

            DailyPlanSection(dailyPlan, onCheckWorkoutClick)

            Spacer(modifier = Modifier.height(24.dp))

            ActivityStatusSection(activityStats)

            Spacer(modifier = Modifier.height(24.dp))

            RecentWorkoutsSection(recentWorkouts)
        }
    }
}


@Composable
fun RecentWorkoutsSection(recentWorkouts: List<WorkoutSummary>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "Latest Workout",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        recentWorkouts.forEach { workout ->
            WorkoutItemCard(workout)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun WorkoutItemCard(workout: WorkoutSummary) {
    val density = LocalDensity.current
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .padding(4.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                            ),
                            start = Offset.Zero,
                            end = Offset.Infinite
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = workout.image),
                    contentDescription = null,
                    modifier = Modifier.size(35.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = workout.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "${workout.calories} Calories Burn | ${workout.duration} minutes",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.LightGray.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(workout.progress)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary,
                                        MaterialTheme.colorScheme.tertiary
                                    )
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


@Composable
fun ActivityStatusSection(activityStats: ActivityStats) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Activity Status",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActivityStatusCard(
                title = "Calories",
                value = activityStats.calories.toString(),
                unit = "Kcal",
                iconId = R.drawable.ic_fire,
                iconTint = Color(0xFFFF5722),
                modifier = Modifier.weight(1f)
            )

            ActivityStatusCard(
                title = "Steps",
                value = activityStats.steps.toString(),
                unit = "Steps",
                iconId = R.drawable.ic_run,
                iconTint = Color(0xFF1976D2),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ActivityStatusCard(
    title: String,
    value: String,
    unit: String,
    iconId: Int,
    iconTint: Color,
    modifier: Modifier
) {
    Card(
        modifier = modifier
            .height(120.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Futuristic background pattern
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                // Draw curved lines
                for (i in 0..5) {
                    val startY = canvasHeight * (0.3f + (i * 0.1f))
                    val controlY = canvasHeight * (0.5f + (i * 0.05f))
                    val endY = canvasHeight * (0.3f + (i * 0.1f))

                    drawPath(
                        path = Path().apply {
                            moveTo(0f, startY)
                            quadraticBezierTo(canvasWidth / 2, controlY, canvasWidth, endY)
                        },
                        color = iconTint.copy(alpha = 0.03f),
                        style = Stroke(width = 1.dp.toPx())
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(13.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .shadow(8.dp, CircleShape, spotColor = iconTint.copy(alpha = 0.3f))
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        iconTint.copy(alpha = 0.2f),
                                        iconTint.copy(alpha = 0.1f)
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = iconId),
                            contentDescription = null,
                            tint = iconTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column {
                    Text(
                        text = value,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = unit,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}


@Composable
fun DailyPlanSection(dailyPlan: DailyPlan, onCheckWorkoutClick: () -> Unit) {
    val primaryColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
    ) {
        Text(
            text = "My Plan",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = dailyPlan.day,
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                // Background pattern (dots or lines for futuristic feel)
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val canvasWidth = size.width
                    val dotSpacing = 20.dp.toPx()

                    for (x in 0..canvasWidth.toInt() step dotSpacing.toInt()) {
                        drawCircle(
                            color = primaryColor,
                            radius = 2.dp.toPx(),
                            center = Offset(x.toFloat(), size.height * 0.8f)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Image(
                            painter = painterResource(id = R.drawable.ic_energy),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp),
                        )


                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = dailyPlan.workoutType,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "${dailyPlan.dayOfWeek} of week",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                            )
                        }
                    }

                    Button(
                        onClick = onCheckWorkoutClick,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier.height(40.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp)
                    ) {
                        Text(
                            text = "Check Workout",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun UserGreetingSection(
    userName: String,
    currentDate: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = CircleShape,
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.9f),
                                Color.White.copy(alpha = 0.7f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = null,
                    modifier = Modifier
                        .size(50.dp),
                    alignment = Alignment.Center
                )
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = "Hello, $userName",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = currentDate,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        IconButton(
            onClick = {},
            modifier = Modifier
                .padding(end = 8.dp)
                .size(44.dp)
                .shadow(
                    elevation = 5.dp,
                    shape = CircleShape,
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                )
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                        )
                    )
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_calendar),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen()
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun RecentWorkoutsSectionPreview() {
    MaterialTheme {
        val recentWorkouts = listOf(
            WorkoutSummary(
                id = "1",
                name = "FullBody Workout",
                calories = 180,
                duration = 20,
                image = R.drawable.ic_run,
                progress = 0.7f
            ),
            WorkoutSummary(
                id = "2",
                name = "FullBody Workout",
                calories = 180,
                duration = 20,
                image = R.drawable.ic_run,
                progress = 0.5f
            )
        )

        RecentWorkoutsSection(recentWorkouts = recentWorkouts)
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ActivityStatusSectionPreview() {
    MaterialTheme {
        val sampleActivityStats = ActivityStats(
            calories = 620.68f,
            steps = 1240
        )
        ActivityStatusSection(
            activityStats = sampleActivityStats
        )
    }
}


@Preview(showBackground = true)
@Composable
fun UserGreetingSectionPreview() {
    UserGreetingSection(userName = "John Doe", currentDate = "Thursday, 08 July")
}

@Preview(showBackground = true)
@Composable
fun DailyPlanSectionPreview() {
    DailyPlanSection(dailyPlan = DailyPlan("Monday", "Upper Body", "2"), onCheckWorkoutClick = {})
}
