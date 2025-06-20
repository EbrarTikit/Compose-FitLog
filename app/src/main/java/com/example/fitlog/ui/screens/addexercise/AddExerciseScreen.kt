@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.fitlog.ui.screens.addexercise

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fitlog.R
import com.example.fitlog.data.model.ExerciseSet
import com.example.fitlog.ui.theme.Gray
import com.example.fitlog.ui.theme.LightPurple1
import com.example.fitlog.ui.theme.OptionTxtColor
import com.example.fitlog.ui.theme.OptionTxtColor2
import com.example.fitlog.ui.theme.PrimaryPurple
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExerciseScreen(
    workouts: List<String>,
    onAddNewWorkout: () -> Unit,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val workoutSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val addSetSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val setTypeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedWorkout by remember { mutableStateOf<String?>(null) }
    var sets by remember { mutableStateOf(listOf<ExerciseSet>()) }
    var tempSetType by remember { mutableStateOf("Set 1") }
    var tempReps by remember { mutableStateOf(0) }
    var tempWeight by remember { mutableStateOf(0f) }
    var tempRepsInput by remember { mutableStateOf("") }
    var tempWeightInput by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier
                    .padding(12.dp),
                onClick = { scope.launch { addSetSheetState.show() } },
                containerColor = PrimaryPurple,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Set",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("Exercise", fontSize = 20.sp)
                IconButton(onClick = {}) {
                    Icon(Icons.Default.MoreVert, contentDescription = "More")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { scope.launch { workoutSheetState.show() } },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Gray)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_dumbell),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Choose Workout",
                            fontSize = 16.sp,
                            color = OptionTxtColor)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(selectedWorkout ?: "None", fontSize = 14.sp, color = OptionTxtColor2)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            painter = painterResource(id = R.drawable.ic_right),
                            contentDescription = null,
                            tint = OptionTxtColor2,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            sets.forEach { set ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = LightPurple1)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${set.setType}")
                        Text("${set.reps} reps, ${set.weight}kg")
                    }
                }
            }
        }
    }

    ChooseWorkoutSheet(workoutSheetState, workouts, onSelect = {
        selectedWorkout = it
    }, onAddNewWorkout = onAddNewWorkout)

    AddSetSheet(addSetSheetState, tempSetType, tempRepsInput, tempWeightInput, onSetTypeClick = {
        scope.launch { setTypeSheetState.show() }
    }, onRepsChange = {
        tempRepsInput = it
        tempReps = it.toIntOrNull() ?: 0
    }, onWeightChange = {
        tempWeightInput = it
        tempWeight = it.toFloatOrNull() ?: 0f
    }, onAddSet = {
        sets = sets + ExerciseSet(setType = tempSetType, reps = tempReps, weight = tempWeight)
        tempRepsInput = ""
        tempWeightInput = ""
        tempReps = 0
        tempWeight = 0f
    })

    SetTypeSheet(setTypeSheetState, onSelect = {
        tempSetType = it
    })
}

@Composable
fun ChooseWorkoutSheet(
    sheetState: SheetState,
    workouts: List<String>,
    onSelect: (String) -> Unit,
    onAddNewWorkout: () -> Unit
) {
    if (sheetState.isVisible) {
        ModalBottomSheet(onDismissRequest = { }, sheetState = sheetState) {
            Column(modifier = Modifier.padding(16.dp)) {
                workouts.forEach {
                    Text(
                        text = it,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(it) }
                            .padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "+ Add New",
                    color = PrimaryPurple,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAddNewWorkout() }
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun AddSetSheet(
    sheetState: SheetState,
    setType: String,
    reps: String,
    weight: String,
    onSetTypeClick: () -> Unit,
    onRepsChange: (String) -> Unit,
    onWeightChange: (String) -> Unit,
    onAddSet: () -> Unit
) {
    if (sheetState.isVisible) {
        ModalBottomSheet(onDismissRequest = { }, sheetState = sheetState) {
            Column(modifier = Modifier.padding(16.dp)) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSetTypeClick() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Gray)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_dumbell),
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                                tint = OptionTxtColor
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Set", fontSize = 16.sp, color = OptionTxtColor)
                        }
                        Text(setType, fontSize = 14.sp, color = OptionTxtColor2)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                TextField(
                    value = reps,
                    onValueChange = onRepsChange,
                    placeholder = { Text("Reps", color = OptionTxtColor) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Gray, RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = weight,
                    onValueChange = onWeightChange,
                    placeholder = { Text("Weight", color = OptionTxtColor) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Gray, RoundedCornerShape(8.dp)),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onAddSet,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
                ) {
                    Text("ADD", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun SetTypeSheet(sheetState: SheetState, onSelect: (String) -> Unit) {
    if (sheetState.isVisible) {
        ModalBottomSheet(onDismissRequest = { }, sheetState = sheetState) {
            Column(modifier = Modifier.padding(16.dp)) {
                val types = listOf(
                    Triple(R.drawable.ic_s, "Set 2", "Set 2"),
                    Triple(R.drawable.ic_w, "Warm Up Set", "Warm Up Set"),
                    Triple(R.drawable.ic_f, "Burnout Set", "Burnout Set"),
                    Triple(R.drawable.ic_d, "Drop Set", "Drop Set")
                )

                types.forEach { (icon, label, value) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onSelect(value) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Gray)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = icon),
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color.Unspecified
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(label, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddExerciseScreenPreview() {
    AddExerciseScreen(
        workouts = listOf("Chest Press", "Squat"),
        onAddNewWorkout = {},
        onBack = {}
    )
}

@Preview(showBackground = true)
@Composable
fun ChooseWorkoutSheetPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        listOf("Chest Press", "Squat").forEach {
            Text(
                text = it,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "+ Add New",
            color = PrimaryPurple,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddSetSheetContentPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        // Set Type Row
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Gray)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_dumbell),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = OptionTxtColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Set", fontSize = 16.sp, color = OptionTxtColor)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Set 2", fontSize = 14.sp, color = OptionTxtColor2)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_right),
                        contentDescription = null,
                        tint = OptionTxtColor2,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Reps Field
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Reps", color = OptionTxtColor) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(Gray, RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Weight Field
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Weight", color = OptionTxtColor) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(Gray, RoundedCornerShape(8.dp)),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ADD Button
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
        ) {
            Text("ADD", color = Color.White)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SetTypeSheetContentPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        val types = listOf(
            Pair(R.drawable.ic_s, "Set 2"),
            Pair(R.drawable.ic_w, "Warm Up Set"),
            Pair(R.drawable.ic_f, "Burnout Set"),
            Pair(R.drawable.ic_d, "Drop Set")
        )

        types.forEach { (icon, label) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Gray)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(label, fontSize = 16.sp)
                }
            }
        }
    }
}


