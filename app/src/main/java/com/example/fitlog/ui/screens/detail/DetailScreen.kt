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

@Composable
fun DetailScreen(
    initialDate: LocalDate = LocalDate.now(),
    workoutsForDate: (LocalDate) -> List<Pair<String, String>>,
    onEditWorkoutClick: () -> Unit,
    onAddWorkoutClick: () -> Unit,
    onAddExerciseClick: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(initialDate) }
    val workouts = workoutsForDate(selectedDate)

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                selectedDate = selectedDate.minusDays(1)
            }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Previous Day")
            }

            Text(
                text = selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM", Locale.ENGLISH)),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = {
                selectedDate = selectedDate.plusDays(1)
            }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next Day")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Fullbody Workout",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${workouts.size} Exercises | 32 mins | 320 Calories Burn",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            IconButton(onClick = { onEditWorkoutClick() }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Workout", tint = PrimaryPurple)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (workouts.isEmpty()) {
            Text("No workout for this day", fontSize = 16.sp, color = Color.Gray)
        } else {
            workouts.forEach { (name, sets) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = CardDefaults.cardColors(containerColor = LightPurple)
                ) {
                    Column(modifier = Modifier.padding(24.dp,16.dp)) {
                        Text(text = name, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = sets, fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = {
                    if (workouts.isEmpty()) {
                        onEditWorkoutClick()
                    } else {
                        onAddExerciseClick()
                    }
                },
                shape = CircleShape,
                containerColor = PrimaryPurple
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Workout", tint = Color.White)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DetailScreenPreview() {
    DetailScreen(
        LocalDate.now(),
        workoutsForDate = { date ->
            listOf(
                "Workout 1" to "3 sets",
                "Workout 2" to "4 sets"
            )
        },
        onEditWorkoutClick = {},
        onAddWorkoutClick = {},
        onAddExerciseClick = {}
    )
}