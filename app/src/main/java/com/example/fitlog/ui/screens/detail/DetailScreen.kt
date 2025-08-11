package com.example.fitlog.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlog.ui.theme.LightPurple
import com.example.fitlog.ui.theme.LightPurple1
import com.example.fitlog.ui.theme.PrimaryPurple
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import com.example.fitlog.data.model.Workout
import com.example.fitlog.data.model.Exercise
import com.example.fitlog.data.model.ExerciseTemplate
import com.example.fitlog.data.repository.WorkoutRepository
import com.example.fitlog.data.repository.ExerciseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Timestamp
import android.util.Log
import androidx.navigation.NavController
import androidx.compose.ui.platform.LocalContext
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.compose.rememberNavController

class DetailViewModel(
    private val exerciseRepository: ExerciseRepository = ExerciseRepository()
) : ViewModel() {

    var exerciseTemplates by mutableStateOf<List<ExerciseTemplate>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    var selectedCategory by mutableStateOf("All")
        private set

    var isLoading by mutableStateOf(false)
        private set

    val categories = listOf("All", "Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Cardio")

    fun loadExerciseTemplates() {
        isLoading = true
        exerciseRepository.getExerciseTemplates { templates ->
            exerciseTemplates = templates
            isLoading = false
        }
    }

    fun searchExercises(query: String) {
        searchQuery = query
        if (query.isBlank()) {
            loadExerciseTemplates()
        } else {
            isLoading = true
            exerciseRepository.searchExerciseTemplates(query) { templates ->
                exerciseTemplates = templates
                isLoading = false
            }
        }
    }

    fun filterByCategory(category: String) {
        selectedCategory = category
        if (category == "All") {
            loadExerciseTemplates()
        } else {
            isLoading = true
            exerciseRepository.getExerciseTemplatesByCategory(category) { templates ->
                exerciseTemplates = templates
                isLoading = false
            }
        }
    }

    init {
        loadExerciseTemplates()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
    val context = LocalContext.current
    val activity = context as? AppCompatActivity
    val today = remember { LocalDate.now() }
    val initialFromArg: LocalDate? = remember(initialDateMillis) {
        initialDateMillis?.let { millis ->
            java.time.Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
        }
    }
    var selectedDate by remember { mutableStateOf(initialFromArg ?: today) }
    var currentWorkoutId by remember { mutableStateOf(workoutId) }

    LaunchedEffect(currentWorkoutId, selectedDate) {
        if (userId.isNotEmpty()) {
            Log.d(
                "DetailScreen",
                "Loading data for workoutId: $workoutId, userId: $userId, selectedDate: $selectedDate"
            )

            if (currentWorkoutId.isNotEmpty() && currentWorkoutId != "current") {
                // Load specific workout by ID
                Log.d("DetailScreen", "Loading specific workout by ID: $currentWorkoutId")
                WorkoutRepository().getWorkoutById(userId, currentWorkoutId) {
                    Log.d("DetailScreen", "Loaded workout: ${it?.name}, id: ${it?.id}")
                    workout = it

                    if (it != null) {
                        // Sync header date with workout date
                        val localDate = it.date.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                        selectedDate = localDate
                        Log.d(
                            "DetailScreen",
                            "Loading exercises for workout: ${it.id}"
                        )
                        ExerciseRepository().getExercises(userId, it.id) { exerciseList ->
                            Log.d(
                                "DetailScreen",
                                "Loaded ${exerciseList.size} exercises: ${exerciseList.map { ex -> ex.name }}"
                            )
                            exercises = exerciseList
                        }
                    }
                }
            } else {
                // Load workout for selected date (including "current" case)
                Log.d("DetailScreen", "Loading workout for selected date: $selectedDate")
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
                    Log.d(
                        "DetailScreen",
                        "Found workout by date: ${foundWorkout?.name}, id: ${foundWorkout?.id}"
                    )
                    workout = foundWorkout
                    if (foundWorkout != null) {
                        Log.d(
                            "DetailScreen",
                            "Loading exercises for found workout: ${foundWorkout.id}"
                        )
                        ExerciseRepository().getExercises(userId, foundWorkout.id) { exerciseList ->
                            Log.d(
                                "DetailScreen",
                                "Loaded ${exerciseList.size} exercises for date workout: ${exerciseList.map { ex -> ex.name }}"
                            )
                            // If no exercises found, create sample exercises for testing
                            if (exerciseList.isEmpty()) {
                                Log.d(
                                    "DetailScreen",
                                    "No exercises found, creating sample exercises for testing"
                                )
                                ExerciseRepository().populateSampleExercises(
                                    userId,
                                    foundWorkout.id
                                ) { success ->
                                    if (success) {
                                        Log.d(
                                            "DetailScreen",
                                            "Sample exercises created, reloading..."
                                        )
                                        // Reload exercises after creating samples
                                        ExerciseRepository().getExercises(
                                            userId,
                                            foundWorkout.id
                                        ) { newExerciseList ->
                                            Log.d(
                                                "DetailScreen",
                                                "Reloaded ${newExerciseList.size} exercises after creating samples"
                                            )
                                            exercises = newExerciseList
                                        }
                                    } else {
                                        Log.e("DetailScreen", "Failed to create sample exercises")
                                        exercises = exerciseList
                                    }
                                }
                            } else {
                                exercises = exerciseList
                            }
                        }
                    } else {
                        Log.d("DetailScreen", "No workout found for date, setting empty exercises")
                        exercises = emptyList()
                    }
                }
            }
        } else {
            Log.d("DetailScreen", "User ID is empty, cannot load data")
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(0.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
                IconButton(onClick = onBackToHome) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back to Home")
                }
                IconButton(onClick = { /* menu or more */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    selectedDate = selectedDate.minusDays(1)
                    currentWorkoutId = ""
                }) { Icon(Icons.Default.ArrowBack, contentDescription = "Previous Day") }

                // Inline date picker trigger
                TextButton(onClick = {
                    activity?.let { act ->
                        val picker = com.google.android.material.datepicker.MaterialDatePicker.Builder.datePicker()
                            .setTitleText("Select date")
                            .setSelection(selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
                            .build()
                        picker.addOnPositiveButtonClickListener { millis ->
                            selectedDate = java.time.Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                            currentWorkoutId = ""
                        }
                        picker.show(act.supportFragmentManager, "DETAIL_DATE_PICKER")
                    }
                }) {
                    Text(
                        text = selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM", Locale.ENGLISH)),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                IconButton(onClick = {
                    selectedDate = selectedDate.plusDays(1)
                    currentWorkoutId = ""
                }) { Icon(Icons.Default.ArrowForward, contentDescription = "Next Day") }
        }
            Spacer(modifier = Modifier.height(8.dp))
            
            if (workout != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                Text(
                        text = workout?.name?.replaceFirstChar { it.uppercase() } ?: "-",
                        fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                    IconButton(onClick = onEditWorkoutClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Workout")
                    }
                }
                Text(
                    text = "${exercises.size} Exercises | ${workout?.duration ?: 0}mins | ${workout?.calories ?: 0} Calories Burn",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Column(modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)) {
                    if (exercises.isEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F4FF))
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "No exercises yet for this workout",
                                    color = Color.Gray,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Add your first exercise to get started",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        Log.d("DetailScreen", "Displaying ${exercises.size} exercises")
                        LazyColumn {
                            items(exercises) { exercise ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .clickable {
                                            val wid = workout?.id ?: currentWorkoutId
                                            if (!wid.isNullOrEmpty()) {
                                                navController.navigate("exercise_detail/$wid/${exercise.id}")
                                            }
                                        },
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(
                                            0xFFF7F4FF
                                        )
                                    )
                                ) {
                                    Column(modifier = Modifier.padding(20.dp, 12.dp)) {
                                        Text(
                                            text = exercise.name,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "${exercise.sets.size} Set",
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
        } else {
                // No workout found for this date
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F4FF))
                    ) {
                        Column(
                            modifier = Modifier.padding(40.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No workout found",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No workout scheduled for ${selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM", Locale.ENGLISH))}",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                    .padding(bottom = 24.dp), contentAlignment = Alignment.BottomEnd) {
            FloatingActionButton(
                onClick = {
                    val targetId = workout?.id
                    if (!targetId.isNullOrEmpty()) {
                        navController.navigate("add_exercise/$targetId")
                    } else {
                        // If no workout exists for this day, go create one first
                        onAddWorkoutClick()
                    }
                },
                shape = CircleShape,
                containerColor = Color(0xFF7C5CFA),
                modifier = Modifier.padding(end = 24.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Exercise",
                    tint = Color.White
                )
            }
        }
        }
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
