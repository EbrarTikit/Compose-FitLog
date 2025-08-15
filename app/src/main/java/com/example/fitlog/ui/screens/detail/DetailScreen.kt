package com.example.fitlog.ui.screens.detail

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.fitlog.data.model.Exercise
import com.example.fitlog.data.model.ExerciseTemplate
import com.example.fitlog.data.model.Workout
import com.example.fitlog.data.repository.ExerciseRepository
import com.example.fitlog.data.repository.WorkoutRepository
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.Month
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DetailScreen(
    workoutId: String,
    onEditWorkoutClick: () -> Unit,
    onAddWorkoutClick: () -> Unit,
    onAddExerciseClick: () -> Unit,
    onBackToHome: () -> Unit,
    onExerciseSelected: (ExerciseTemplate) -> Unit = {},
    viewModel: DetailViewModel = viewModel(),
    navController: NavController,
    initialDateMillis: Long? = null
) {
    var workout by remember { mutableStateOf<Workout?>(null) }
    var exercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val today = remember { LocalDate.now() }
    val initialFromArg: LocalDate? = remember(initialDateMillis) {
        initialDateMillis?.let { millis ->
            java.time.Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
        }
    }
    var selectedDate by remember { mutableStateOf(initialFromArg ?: today) }
    var currentWorkoutId by remember { mutableStateOf(workoutId) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showMonthDropdown by remember { mutableStateOf(false) }

    val allMonths = Month.values()
    val currentMonth = selectedDate.month
    val currentYear = selectedDate.year
    val isLeap = selectedDate.isLeapYear
    val daysInMonth = currentMonth.length(isLeap)
    val daysListState = rememberLazyListState()
    LaunchedEffect(selectedDate.month, selectedDate.year) {
        val index = (selectedDate.dayOfMonth - 1).coerceAtLeast(0)
        val target = (index - 3).coerceAtLeast(0)
        daysListState.scrollToItem(target)
    }

    LaunchedEffect(currentWorkoutId, selectedDate) {
        if (userId.isNotEmpty()) {
            if (currentWorkoutId.isNotEmpty() && currentWorkoutId != "current") {
                // Load specific workout by ID
                WorkoutRepository().getWorkoutById(userId, currentWorkoutId) {
                    workout = it

                    if (it != null) {
                        // Sync header date with workout date
                        val localDate = it.date.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        selectedDate = localDate
                        ExerciseRepository().getExercises(userId, it.id) { exerciseList ->
                            exercises = exerciseList
                        }
                    }
                }
            } else {
                val zoneId = ZoneId.systemDefault()
                val startOfDay = selectedDate.atStartOfDay(zoneId).toInstant().toEpochMilli()
                val endOfDay =
                    selectedDate.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
                val startTimestamp =
                    Timestamp(startOfDay / 1000, ((startOfDay % 1000) * 1000000).toInt())
                val endTimestamp = Timestamp(endOfDay / 1000, ((endOfDay % 1000) * 1000000).toInt())

                WorkoutRepository().getWorkoutByDateRange(
                    userId,
                    startTimestamp,
                    endTimestamp
                ) { foundWorkout ->
                    workout = foundWorkout
                    if (foundWorkout != null) {
                        ExerciseRepository().getExercises(userId, foundWorkout.id) { exerciseList ->
                            if (exerciseList.isEmpty()) {
                                ExerciseRepository().populateSampleExercises(
                                    userId,
                                    foundWorkout.id
                                ) { success ->
                                    if (success) {
                                        // Reload exercises after creating samples
                                        ExerciseRepository().getExercises(
                                            userId,
                                            foundWorkout.id
                                        ) { newExerciseList ->
                                            exercises = newExerciseList
                                        }
                                    } else {
                                        exercises = exerciseList
                                    }
                                }
                            } else {
                                exercises = exerciseList
                            }
                        }
                    } else {
                        exercises = emptyList()
                    }
                }
            }
        } else {
            Log.d("DetailScreen", "User ID is empty, cannot load data")
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                ) {
                    IconButton(onClick = onBackToHome,
                        modifier = Modifier.align(Alignment.CenterStart)) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF7C5CFA)
                        )
                    }
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "Daily Plan",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color(0xFF2D2154)
                    )
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, bottom = 4.dp, start = 10.dp, end = 10.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedDate.format(
                            DateTimeFormatter.ofPattern(
                                "dd MMMM, yyyy",
                                Locale.ENGLISH
                            )
                        ),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color(0xFF2D2154),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(14.dp))
                    Box {
                        OutlinedButton(
                            onClick = { showMonthDropdown = true },
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, Color(0xFF7C5CFA)),
                            modifier = Modifier.height(34.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                        ) {
                            Text(
                                text = selectedDate.month.getDisplayName(
                                    java.time.format.TextStyle.SHORT,
                                    Locale.ENGLISH
                                ),
                                fontSize = 15.sp,
                                color = Color(0xFF7C5CFA),
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                Icons.Filled.KeyboardArrowDown,
                                contentDescription = "Select Month",
                                tint = Color(0xFF7C5CFA)
                            )
                        }
                        DropdownMenu(
                            expanded = showMonthDropdown,
                            onDismissRequest = { showMonthDropdown = false }
                        ) {
                            allMonths.forEach { month ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            month.getDisplayName(
                                                java.time.format.TextStyle.FULL,
                                                Locale.ENGLISH
                                            ),
                                            color = if (month == currentMonth) Color(0xFF7C5CFA) else Color(
                                                0xFF2D2154
                                            )
                                        )
                                    },
                                    onClick = {
                                        val safeDay =
                                            minOf(selectedDate.dayOfMonth, month.length(isLeap))
                                        selectedDate = LocalDate.of(currentYear, month, safeDay)
                                        currentWorkoutId = ""
                                        showMonthDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(7.dp))
                LazyRow(
                    Modifier
                        .fillMaxWidth()
                        .padding(start = 2.dp, end = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    state = daysListState
                ) {
                    items(daysInMonth) { i ->
                        val date = LocalDate.of(currentYear, currentMonth, i + 1)
                        val isSelected = date == selectedDate
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .width(54.dp)
                                .animateItemPlacement()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    if (isSelected) Color(0xFF7C5CFA)
                                    else Color.White
                                )
                                .clickable { selectedDate = date; currentWorkoutId = "" }
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = date.dayOfWeek.getDisplayName(
                                    java.time.format.TextStyle.SHORT,
                                    Locale.ENGLISH
                                ).uppercase(),
                                fontWeight = FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (isSelected) Color.White else Color(0xFF7C5CFA)
                            )
                            Text(
                                text = "%02d".format(date.dayOfMonth),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = if (isSelected) Color.White else Color(0xFF2D2154)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(36.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FF))
                ) {
                    Column(Modifier.padding(30.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                workout?.let {
                                    Text(
                                        text = it.name.replaceFirstChar { ch -> ch.uppercase() },
                                        fontSize = 23.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2D2154),
                                        modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                                    )
                                }
                            }
                            if (workout != null) {
                                IconButton(onClick = onEditWorkoutClick) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF7C5CFA))
                                }
                            }
                        }
                        workout?.let {
                            Text(
                                text = "${exercises.size} Exercises · ${it.duration} mins · ${it.calories} Cal",
                                color = Color(0xFF9191C6),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }
                        if (workout == null) {
                            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "No workout for today!",
                                    color = Color(0xFF9191C6),
                                    fontSize = 16.sp
                                )
                                Spacer(Modifier.height(12.dp))
                                OutlinedButton(
                                    onClick = { onAddWorkoutClick() },
                                    shape = RoundedCornerShape(50)
                                ) {
                                    Text("Create Plan", color = Color(0xFF7C5CFA), fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            if (exercises.isEmpty()) {
                                Text(
                                    text = "No exercises for this workout.",
                                    color = Color(0xFF9191C6),
                                    fontSize = 15.sp,
                                    modifier = Modifier.padding(vertical = 14.dp)
                                )
                                Spacer(Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        val targetId = workout?.id
                                        if (!targetId.isNullOrEmpty()) {
                                            navController.navigate("add_exercise/$targetId")
                                        } else {
                                            onAddExerciseClick()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(32.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C5CFA)),
                                    contentPadding = PaddingValues(vertical = 13.dp)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = "Add Exercise", tint = Color.White)
                                    Text("  Add Exercise", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            } else {
                                Column(Modifier.fillMaxWidth()) {
                                    exercises.forEach { exercise ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 7.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(Color(0xFFEDEAFF))
                                                .padding(vertical = 12.dp, horizontal = 14.dp)
                                                .clickable {
                                                    val wid = workout?.id ?: currentWorkoutId
                                                    if (!wid.isNullOrEmpty()) {
                                                        navController.navigate("exercise_detail/$wid/${exercise.id}")
                                                    }
                                                },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Box(
                                                Modifier
                                                    .size(36.dp)
                                                    .background(
                                                        Color(0x197C5CFA),
                                                        shape = CircleShape
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(imageVector = Icons.Filled.Search, contentDescription = null, tint = Color(0xFF7C5CFA), modifier = Modifier.size(20.dp))
                                            }
                                            Spacer(Modifier.width(11.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = exercise.name,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 16.sp,
                                                    color = Color(0xFF2D2154)
                                                )
                                                Text(
                                                    text = "${exercise.sets.size} Set",
                                                    fontWeight = FontWeight.Medium,
                                                    fontSize = 13.sp,
                                                    color = Color(0xFF9191C6)
                                                )
                                            }
                                        }
                                    }
                                }
                                Spacer(Modifier.height(18.dp))
                                Button(
                                    onClick = {
                                        val targetId = workout?.id
                                        if (!targetId.isNullOrEmpty()) {
                                            navController.navigate("add_exercise/$targetId")
                                        } else {
                                            onAddExerciseClick()
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(32.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C5CFA)),
                                    contentPadding = PaddingValues(vertical = 13.dp)
                                ) {
                                    Icon(Icons.Filled.Add, contentDescription = "Add Exercise", tint = Color.White)
                                    Text("  Add Exercise", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
    }
    if (showDatePicker) {
        AndroidView(factory = { ctx ->
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select date")
                .setSelection(
                    selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                )
                .build()
            datePicker.addOnPositiveButtonClickListener { millis ->
                selectedDate = java.time.Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
                    .toLocalDate()
                currentWorkoutId = ""
                showDatePicker = false
            }
            datePicker.addOnDismissListener { showDatePicker = false }
            datePicker.show((ctx as AppCompatActivity).supportFragmentManager, "MODERN_DATE_PICKER")
            android.view.View(ctx)
        })
    }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    DetailScreen(
        "",
        onEditWorkoutClick = {},
        onAddWorkoutClick = {},
        onAddExerciseClick = {},
        onBackToHome = {},
        navController = rememberNavController()
    )
}
