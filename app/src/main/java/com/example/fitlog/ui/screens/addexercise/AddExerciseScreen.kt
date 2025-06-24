@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.fitlog.ui.screens.addexercise

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlog.R
import com.example.fitlog.data.model.ExerciseSet
import com.example.fitlog.data.model.ExerciseTemplate
import com.example.fitlog.data.repository.ExerciseRepository
import com.example.fitlog.ui.theme.Gray
import com.example.fitlog.ui.theme.LightPurple1
import com.example.fitlog.ui.theme.OptionTxtColor
import com.example.fitlog.ui.theme.OptionTxtColor2
import com.example.fitlog.ui.theme.PrimaryPurple
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.example.fitlog.data.model.Exercise
import com.google.firebase.auth.FirebaseAuth

class AddExerciseViewModel(
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
fun AddExerciseScreen(
    workouts: List<String>,
    onAddNewWorkout: () -> Unit,
    onBack: () -> Unit,
    workoutId: String,
    navController: NavController,
    viewModel: AddExerciseViewModel = viewModel()
) {
    val scope = rememberCoroutineScope()
    val workoutSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val addSetSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val setTypeSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val exerciseSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedWorkout by remember { mutableStateOf<String?>(null) }
    var selectedExercise by remember { mutableStateOf<ExerciseTemplate?>(null) }
    var sets by remember { mutableStateOf(listOf<ExerciseSet>()) }
    var tempSetType by remember { mutableStateOf("Set 1") }
    var tempReps by remember { mutableStateOf(0) }
    var tempWeight by remember { mutableStateOf(0f) }
    var tempRepsInput by remember { mutableStateOf("") }
    var tempWeightInput by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()

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
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Text("Exercise", fontSize = 20.sp)
                IconButton(
                    onClick = {
                        if (selectedExercise != null && sets.isNotEmpty() && !isSaving) {
                            isSaving = true
                            val exercise = Exercise(
                                id = selectedExercise!!.id + System.currentTimeMillis(),
                                name = selectedExercise!!.name,
                                sets = sets
                            )
                            ExerciseRepository().addExercise(userId, workoutId, exercise) { success ->
                                isSaving = false
                                if (success) {
                                    navController.navigate("detail/$workoutId") {
                                        popUpTo("add_exercise") { inclusive = true }
                                    }
                                }
                                // else: hata gösterilebilir
                            }
                        }
                    }
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Save Exercise"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { scope.launch { exerciseSheetState.show() } },
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
                        Text(
                            "Choose Exercise",
                            fontSize = 16.sp,
                            color = OptionTxtColor
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            selectedExercise?.name ?: "None",
                            fontSize = 14.sp,
                            color = OptionTxtColor2
                        )
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

    ExerciseSelectionBottomSheet(
        sheetState = exerciseSheetState,
        viewModel = viewModel,
        onExerciseSelected = { exercise ->
            selectedExercise = exercise
            scope.launch { exerciseSheetState.hide() }
        },
        onAddNewExercise = {
            onAddNewWorkout()
            scope.launch { exerciseSheetState.hide() }
        }
    )

    ChooseWorkoutSheet(workoutSheetState, workouts, onSelect = {
        selectedWorkout = it
    }, onAddNewWorkout = onAddNewWorkout)

    AddSetSheet(
        sheetState = addSetSheetState,
        setType = tempSetType,
        reps = tempRepsInput,
        weight = tempWeightInput,
        onSetTypeClick = {
            scope.launch { setTypeSheetState.show() }
        },
        onRepsChange = {
            tempRepsInput = it.filter { c -> c.isDigit() }
            tempReps = tempRepsInput.toIntOrNull() ?: 0
        },
        onWeightChange = {
            tempWeightInput = it.filter { c -> c.isDigit() || c == '.' }
            tempWeight = tempWeightInput.toFloatOrNull() ?: 0f
        },
        onAddSet = {
            if (tempReps > 0 && tempWeight > 0f) {
                sets =
                    sets + ExerciseSet(setType = tempSetType, reps = tempReps, weight = tempWeight)
                tempRepsInput = ""
                tempWeightInput = ""
                tempReps = 0
                tempWeight = 0f
                scope.launch { addSetSheetState.hide() }
            }
        },
        isAddEnabled = tempReps > 0 && tempWeight > 0f
    )

    SetTypeSheet(setTypeSheetState, onSelect = {
        tempSetType = it
        scope.launch {
            setTypeSheetState.hide()
            addSetSheetState.show()
        }
    })
}

@Composable
fun ExerciseSelectionBottomSheet(
    sheetState: SheetState,
    viewModel: AddExerciseViewModel,
    onExerciseSelected: (ExerciseTemplate) -> Unit,
    onAddNewExercise: () -> Unit
) {
    if (sheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = { },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Choose Exercise",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Search Bar
                OutlinedTextField(
                    value = viewModel.searchQuery,
                    onValueChange = { viewModel.searchExercises(it) },
                    placeholder = { Text("Search exercises...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                // Category Filter
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    items(viewModel.categories) { category ->
                        FilterChip(
                            onClick = { viewModel.filterByCategory(category) },
                            label = { Text(category) },
                            selected = viewModel.selectedCategory == category,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryPurple,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Exercise List
                if (viewModel.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    if (viewModel.exerciseTemplates.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No exercises found.")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(viewModel.exerciseTemplates) { exercise ->
                                ExerciseTemplateCard(
                                    exercise = exercise,
                                    onClick = { onExerciseSelected(exercise) }
                                )
                            }
                            // Add New Exercise Button
                            item {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { onAddNewExercise() },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(
                                            0xFFF0F0F0
                                        )
                                    ),
                                    border = BorderStroke(
                                        1.dp,
                                        PrimaryPurple.copy(alpha = 0.3f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Add,
                                            contentDescription = "Add New",
                                            tint = PrimaryPurple,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Add New Exercise",
                                            color = PrimaryPurple,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExerciseTemplateCard(
    exercise: ExerciseTemplate,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    if (exercise.category.isNotEmpty()) {
                        Text(
                            text = exercise.category,
                            fontSize = 12.sp,
                            color = PrimaryPurple,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                if (exercise.isPopular) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFE082)
                    ) {
                        Text(
                            text = "Popular",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE65100),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (exercise.equipment.isNotEmpty()) {
                Text(
                    text = "Equipment: ${exercise.equipment}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (exercise.muscleGroups.isNotEmpty()) {
                Text(
                    text = "Muscles: ${exercise.muscleGroups.joinToString(", ")}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
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
    onAddSet: () -> Unit,
    isAddEnabled: Boolean
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(setType, fontSize = 14.sp, color = OptionTxtColor2)
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
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onAddSet,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                    enabled = isAddEnabled
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
        workouts = emptyList(),
        onAddNewWorkout = {},
        onBack = {},
        workoutId = "",
        navController = NavController(
            context = TODO()
        )
    )
}

@Preview(showBackground = true)
@Composable
fun ChooseWorkoutSheetPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        emptyList<String>().forEach {
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
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
                    .clickable { },
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
