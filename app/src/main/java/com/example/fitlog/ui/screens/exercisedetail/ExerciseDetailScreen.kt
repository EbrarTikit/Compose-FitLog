package com.example.fitlog.ui.screens.exercisedetail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
// no top-bar save icon; bottom button used
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlog.data.model.Exercise
import com.example.fitlog.data.model.ExerciseSet
import com.example.fitlog.data.repository.ExerciseRepository
import com.example.fitlog.ui.theme.LightPurple1
import com.example.fitlog.ui.theme.PrimaryPurple
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun ExerciseDetailScreen(
    workoutId: String,
    exerciseId: String,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var exercise by remember { mutableStateOf<Exercise?>(null) }
    var isSaving by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(userId, workoutId, exerciseId) {
        if (userId.isNotEmpty() && workoutId.isNotEmpty() && exerciseId.isNotEmpty()) {
            ExerciseRepository().getExercise(userId, workoutId, exerciseId) {
                exercise = it
            }
        }
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text(
                    text = exercise?.name ?: "Exercise",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // add empty set quick
                    exercise = (exercise ?: Exercise(id = exerciseId, name = "")).copy(
                        sets = (exercise?.sets ?: emptyList()) + ExerciseSet(
                            id = System.currentTimeMillis().toString(),
                            setType = "Set ${(exercise?.sets?.size ?: 0) + 1}",
                            reps = 0,
                            weight = 0f
                        )
                    )
                },
                containerColor = PrimaryPurple,
                shape = CircleShape
            ) { Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White) }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Transparent)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Button(
                    onClick = {
                        if (exercise != null && !isSaving) {
                            isSaving = true
                            ExerciseRepository().updateExercise(userId, workoutId, exercise!!) { _ ->
                                isSaving = false
                                onSaved()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Text("Save", color = Color.White)
                }
            }
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp)
        ) {
            if (exercise == null) {
                Text("Loading...", color = Color.Gray)
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    itemsIndexed(
                        exercise!!.sets,
                        key = { index, set ->
                            val base = set.id.ifBlank { "${set.setType}-${set.reps}-${set.weight}" }
                            "$base-$index"
                        }
                    ) { index, set ->
                        SetItem(
                            set = set,
                            onChange = { updated ->
                                val updatedList = exercise!!.sets.toMutableList()
                                updatedList[index] = updated
                                exercise = exercise!!.copy(sets = updatedList)
                            },
                            onDelete = {
                                val updatedList = exercise!!.sets.toMutableList()
                                if (index in updatedList.indices) {
                                    updatedList.removeAt(index)
                                }
                                exercise = exercise!!.copy(sets = updatedList)
                            }
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SetItem(
    set: ExerciseSet,
    onChange: (ExerciseSet) -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = LightPurple1)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(text = set.setType, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = (set.reps.takeIf { it > 0 }?.toString() ?: ""),
                    onValueChange = { v ->
                        val reps = v.filter { it.isDigit() }.toIntOrNull() ?: 0
                        onChange(set.copy(reps = reps))
                    },
                    label = { Text("Reps") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = (set.weight.takeIf { it > 0f }?.toString() ?: ""),
                    onValueChange = { v ->
                        val cleaned = v.filter { it.isDigit() || it == '.' }
                        onChange(set.copy(weight = cleaned.toFloatOrNull() ?: 0f))
                    },
                    label = { Text("Weight (kg)") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Delete",
                    color = Color(0xFFD32F2F),
                    modifier = Modifier.clickable { onDelete() }
                )
            }
        }
    }
}


