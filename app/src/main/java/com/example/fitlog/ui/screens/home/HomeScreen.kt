package com.example.fitlog.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlog.R
import com.example.fitlog.data.model.ActivityStats
import com.example.fitlog.data.model.DailyPlan
import com.example.fitlog.data.model.WorkoutSummary
import com.example.fitlog.ui.theme.LightPurple2
import com.example.fitlog.ui.theme.PrimaryPurple


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
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        recentWorkouts.forEach { workout ->
            WorkoutItemCard(workout)
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun WorkoutItemCard(workout: WorkoutSummary) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(LightPurple2.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = workout.image),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
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
                    color = Color.Black
                )
                Text(
                    text = "${workout.calories} Calories Burn | ${workout.duration} minutes",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Progress indicator
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(2.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(workout.progress)
                            .height(4.dp)
                            .background(PrimaryPurple, RoundedCornerShape(2.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(LightPurple2.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ActivityStatusSection(activityStats: ActivityStats) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "Activity Status",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 12.dp)
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
            .height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Arka plan dekoratif çizgi çizen canvas
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                drawLine(
                    color = iconTint.copy(alpha = 0.1f),
                    start = Offset(0f, canvasHeight * 0.7f),
                    end = Offset(canvasWidth, canvasHeight * 0.7f),
                    strokeWidth = 2.dp.toPx()
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(iconTint.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Column {
                Text(
                    text = value,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Text(
                    text = unit,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }

    }
}

@Composable
fun DailyPlanSection(dailyPlan: DailyPlan, onCheckWorkoutClick: () -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Text(
            text = "My Plan",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = dailyPlan.day,
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = LightPurple2.copy(alpha = 0.7f)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
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
                        modifier = Modifier.size(40.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = dailyPlan.workoutType,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.Black
                        )
                        Text(
                            text =  "${dailyPlan.dayOfWeek} of week",
                            fontSize = 12.sp,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.DarkGray
                        )
                    }
                }

                Button(
                    onClick = { onCheckWorkoutClick() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryPurple.copy(alpha = 0.9f)
                    ),
                    modifier = Modifier
                        .height(36.dp)
                        .fillMaxWidth(0.85f),
                    shape = RoundedCornerShape(20.dp), // Tam yuvarlak buton
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Check Workout",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = Color.White
                    )
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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .size(60.dp),
                    alignment = Alignment.Center
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.align(Alignment.CenterVertically)
            ) {
                Text(
                    text = "Hello, $userName!",
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = currentDate,
                    fontSize = 14.sp,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        IconButton(
            onClick = {},
            modifier = Modifier.padding(end = 12.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_calendar),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
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
