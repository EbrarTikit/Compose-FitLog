package com.example.fitlog.ui.screens.home

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fitlog.R
import com.example.fitlog.data.model.ActivityStats
import com.example.fitlog.data.model.DailyPlan
import com.example.fitlog.data.model.WorkoutSummary
import com.example.fitlog.ui.navigation.ScreenRoute
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

data class ActivityStatusItem(
    val title: String,
    val value: String,
    val unit: String,
    val iconId: Int,
    val iconColor: Color
)

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    val context = LocalContext.current
    val firebaseUser = FirebaseAuth.getInstance().currentUser
    val userName = firebaseUser?.displayName ?: firebaseUser?.email ?: "User"
    val currentDate by remember { derivedStateOf { viewModel.selectedDate.value.toString() } }

    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val dailyPlan by viewModel.dailyPlan
    val activityStats by viewModel.activityStats
    val recentWorkouts by viewModel.recentWorkouts

    LaunchedEffect(viewModel.selectedDate.value) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            viewModel.initializeData()
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFCDC9FF), Color(0xFFE4E4FB), Color(0xFFEDEAFB)),
                    startY = 0.0f, endY = 1400f
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 36.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(36.dp))
                UserGreetingSectionModern(
                    userName,
                    currentDate,
                    onCalendarClick = { showDatePicker = true })
                Spacer(modifier = Modifier.height(28.dp))
            }
            item {
                Text(
                    text = "My Plan",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3632F2),
                    modifier = Modifier.padding(start = 28.dp, bottom = 12.dp)
                )
                if (dailyPlan.workoutType.isBlank() && dailyPlan.dayOfWeek.isBlank()) {
                    MyPlanCardEmptyModern(onCreatePlanClick = {
                        val dateMillis =
                            viewModel.selectedDate.value.atStartOfDay(ZoneId.systemDefault())
                                .toInstant().toEpochMilli()
                        navController.navigate("${ScreenRoute.EditWorkout.route}?date=$dateMillis")
                    })
                } else {
                    MyPlanCardModern(
                        workoutType = dailyPlan.workoutType,
                        dayOfWeek = dailyPlan.dayOfWeek + " of week",
                        onCheckClick = {
                            val workoutId = viewModel.getCurrentWorkoutId()
                            val dateMillis =
                                viewModel.selectedDate.value.atStartOfDay(ZoneId.systemDefault())
                                    .toInstant().toEpochMilli()
                            if (workoutId.isNotEmpty()) navController.navigate("detail/$workoutId?date=$dateMillis") else navController.navigate(
                                "detail/current?date=$dateMillis"
                            )
                        }
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
            item {
                ActivityStatusSectionFuturistic(
                    items = listOf(
                        ActivityStatusItem(
                            "Calories",
                            activityStats.calories.takeIf { it != 0f }?.toString() ?: "0",
                            "Kcal",
                            com.example.fitlog.R.drawable.ic_fire,
                            Color(0xFFFF5722)
                        ),
                        ActivityStatusItem(
                            "Steps",
                            activityStats.steps.takeIf { it != 0 }?.toString() ?: "0",
                            "Steps",
                            com.example.fitlog.R.drawable.ic_run,
                            Color(0xFF1976D2)
                        ),
                        ActivityStatusItem(
                            "Distance",
                            "2.3",
                            "km",
                            com.example.fitlog.R.drawable.ic_run,
                            Color(0xFF43A047)
                        ),
                        ActivityStatusItem(
                            "Active",
                            "45",
                            "min",
                            com.example.fitlog.R.drawable.ic_fire,
                            Color(0xFF7C5CFA)
                        )
                    )
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
            item {
                if (recentWorkouts.isEmpty()) {
                    LatestWorkoutsSectionEmpty()
                } else {
                    RecentWorkoutsSectionModern(
                        recentWorkouts = recentWorkouts,
                        onWorkoutClick = { workoutId -> navController.navigate("detail/$workoutId") }
                    )
                }
            }
        }
        if (showDatePicker) {
            AndroidView(factory = { ctx ->
                val datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Tarih Seçin")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
                datePicker.addOnPositiveButtonClickListener { millis ->
                    val instant = Instant.ofEpochMilli(millis)
                    val zoneId = ZoneId.systemDefault()
                    val localDate = instant.atZone(zoneId).toLocalDate()
                    viewModel.onDateSelected(localDate)
                    showDatePicker = false
                }
                datePicker.addOnDismissListener { showDatePicker = false }
                datePicker.show((ctx as AppCompatActivity).supportFragmentManager, "DATE_PICKER")
                View(ctx)
            })
        }
    }
}

@Composable
fun UserGreetingSectionModern(
    userName: String,
    currentDate: String,
    onCalendarClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(
                        elevation = 24.dp,
                        shape = CircleShape,
                        ambientColor = Color(0xFFB9DFFF),
                        spotColor = Color(0xFFCFCAF7)
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(Color(0xFF8B8AF7), Color.Transparent), radius = 42f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    alignment = Alignment.Center
                )
            }
            Spacer(Modifier.width(18.dp))
            Column {
                Text(
                    text = "Hello, $userName",
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF3632F2)
                )
                Text(
                    text = currentDate,
                    fontSize = 15.sp,
                    color = Color(0xFF7473B4),
                    fontWeight = FontWeight.Medium
                )
            }
        }
        IconButton(
            onClick = onCalendarClick,
            modifier = Modifier
                .size(52.dp)
                .shadow(elevation = 8.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFC0BCFD), Color(0xFFDBDEF6))
                    )
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_calendar),
                contentDescription = null,
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

@Composable
fun MyPlanCardModern(
    workoutType: String,
    dayOfWeek: String,
    onCheckClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E5EA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF7C5CFA).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = com.example.fitlog.R.drawable.ic_energy),
                    contentDescription = null,
                    tint = Color(0xFF7C5CFA),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = workoutType,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF2D2154)
                )
                Text(
                    text = dayOfWeek,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF7C5CFA).copy(alpha = 0.7f)
                )
            }
            Button(
                onClick = onCheckClick,
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7C5CFA),
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "Check",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
fun MyPlanCardEmptyModern(onCreatePlanClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .padding(horizontal = 16.dp)
            .clickable { onCreatePlanClick() },
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E5EA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF7C5CFA).copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = com.example.fitlog.R.drawable.ic_energy),
                    contentDescription = null,
                    tint = Color(0xFF7C5CFA).copy(alpha = 0.3f),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "No Plan Yet",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFFB0AFC6)
                )
                Text(
                    text = "Create your first workout plan!",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFB0AFC6)
                )
            }
        }
    }
}

@Composable
fun ActivityStatusSectionFuturistic(items: List<ActivityStatusItem>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Activity Status",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D2154),
            modifier = Modifier.padding(bottom = 12.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 108.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ActivityStatusCardFuturistic(items[0], Modifier.weight(1f))
            Spacer(Modifier.width(6.dp))
            ActivityStatusCardFuturistic(items[1], Modifier.weight(1f))
        }
        Spacer(Modifier.height(14.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 108.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ActivityStatusCardFuturistic(items[2], Modifier.weight(1f))
            Spacer(Modifier.width(6.dp))
            ActivityStatusCardFuturistic(items[3], Modifier.weight(1f))
        }
    }
}

@Composable
fun ActivityStatusCardFuturistic(item: ActivityStatusItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(90.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E5EA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(item.iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = item.iconId),
                        contentDescription = null,
                        tint = item.iconColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = item.title,
                    fontSize = 13.sp,
                    color = Color(0xFF7C5CFA),
                    fontWeight = FontWeight.Medium
                )
            }
            Text(
                text = item.value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D2154)
            )
            Text(
                text = item.unit,
                fontSize = 11.sp,
                color = Color(0xFF7C5CFA).copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun RecentWorkoutsSectionModern(
    recentWorkouts: List<WorkoutSummary>,
    onWorkoutClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "Latest Workout",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D2154),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        recentWorkouts.forEach { workout ->
            WorkoutItemCardModern(workout, onWorkoutClick)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun WorkoutItemCardModern(workout: WorkoutSummary, onWorkoutClick: (String) -> Unit) {
    val density = LocalDensity.current
    val tertiaryColor = Color(0xFF7C5CFA)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .clickable { onWorkoutClick(workout.id) },
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE5E5EA)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
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
                        spotColor = Color(0xFF7C5CFA).copy(alpha = 0.3f)
                    )
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF7C5CFA).copy(alpha = 0.8f),
                                Color(0xFF7C5CFA).copy(alpha = 0.6f)
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
                    colorFilter = ColorFilter.tint(Color(0xFF7C5CFA))
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
                    color = Color(0xFF2D2154)
                )

                Text(
                    text = "${workout.calories} Calories Burn | ${workout.duration} minutes",
                    fontSize = 13.sp,
                    color = Color(0xFF7C5CFA).copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFF7C5CFA).copy(alpha = 0.15f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(workout.progress)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color(0xFF7C5CFA),
                                        Color(0xFF7C5CFA).copy(alpha = 0.7f)
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
                                Color(0xFF7C5CFA).copy(alpha = 0.2f),
                                Color(0xFF7C5CFA).copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = Color(0xFF7C5CFA),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun LatestWorkoutsSectionEmpty() {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = "Latest Workout",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2D2154),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            shape = RoundedCornerShape(30.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E5EA)),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = com.example.fitlog.R.drawable.ic_run),
                        contentDescription = null,
                        tint = Color(0xFF7C5CFA).copy(alpha = 0.2f),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "No workouts yet",
                        color = Color(0xFFB0AFC6),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun DailyGoalProgressBar(current: Int, goal: Int, modifier: Modifier = Modifier) {
    val progress = (current.toFloat() / goal).coerceIn(0f, 1f)
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE5E5EA)),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF7C5CFA).copy(alpha = 0.13f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = com.example.fitlog.R.drawable.ic_run),
                    contentDescription = null,
                    tint = Color(0xFF7C5CFA),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Daily Steps Goal",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF2D2154)
                )
                Text(
                    text = "$current / $goal steps",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF7C5CFA).copy(alpha = 0.7f)
                )
                LinearProgressIndicator(
                    progress = progress,
                    color = Color(0xFF7C5CFA),
                    trackColor = Color(0xFF7C5CFA).copy(alpha = 0.15f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .padding(top = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen(
            navController = rememberNavController(),
        )
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

        RecentWorkoutsSectionModern(recentWorkouts = recentWorkouts, onWorkoutClick = {})
    }
}

@Preview(showBackground = true)
@Composable
fun UserGreetingSectionPreview() {
    UserGreetingSectionModern(
        userName = "John Doe",
        currentDate = "Thursday, 08 July",
        onCalendarClick = {}
    )
}

@Preview(showBackground = true)
@Composable
fun MyPlanCardPreview() {
    MyPlanCardModern(workoutType = "Upper Body", dayOfWeek = "Monday of week", onCheckClick = {})
}
