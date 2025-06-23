package com.example.fitlog.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlog.ui.theme.LightPurple
import com.example.fitlog.ui.theme.LightPurple1
import com.example.fitlog.ui.theme.PrimaryPurple
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import com.example.fitlog.data.model.Workout
import com.example.fitlog.data.repository.WorkoutRepository
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.LaunchedEffect
import com.example.fitlog.data.model.Exercise
import com.example.fitlog.data.repository.ExerciseRepository

@Composable
fun DetailScreen(
    workoutId: String,
    onEditWorkoutClick: () -> Unit,
    onAddWorkoutClick: () -> Unit,
    onAddExerciseClick: () -> Unit
) {
    var workout by remember { mutableStateOf<Workout?>(null) }
    var exercises by remember { mutableStateOf<List<Exercise>>(emptyList()) }
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    val today = remember { java.time.LocalDate.now() }
    var selectedDate by remember { mutableStateOf(today) }

    LaunchedEffect(workoutId) {
        if (userId.isNotEmpty() && workoutId.isNotEmpty()) {
            WorkoutRepository().getWorkoutById(userId, workoutId) {
                workout = it
            }
            ExerciseRepository().getExercises(userId, workoutId) {
                exercises = it
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize().padding(0.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onAddWorkoutClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                IconButton(onClick = { /* menu or more */ }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedDate = selectedDate.minusDays(1) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous Day")
                }
                Text(
                    text = selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM", Locale.ENGLISH)),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                IconButton(onClick = { selectedDate = selectedDate.plusDays(1) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next Day")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
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
            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {
                if (exercises.isEmpty()) {
                    Text("No exercises yet", color = Color.Gray, modifier = Modifier.padding(16.dp))
                } else {
                    exercises.forEach { exercise ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            shape = RoundedCornerShape(24.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F4FF))
                        ) {
                            Column(modifier = Modifier.padding(20.dp, 12.dp)) {
                                Text(text = exercise.name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "${exercise.sets.size} Set", fontSize = 14.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
            Box(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), contentAlignment = Alignment.BottomEnd) {
                FloatingActionButton(
                    onClick = onAddExerciseClick,
                    shape = CircleShape,
                    containerColor = Color(0xFF7C5CFA),
                    modifier = Modifier.padding(end = 24.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Exercise", tint = Color.White)
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
        onAddExerciseClick = {}
    )
}