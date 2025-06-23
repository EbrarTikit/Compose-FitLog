package com.example.fitlog.ui.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlog.data.model.ActivityStats
import com.example.fitlog.data.model.DailyPlan
import com.example.fitlog.data.model.WorkoutSummary
import com.example.fitlog.data.model.Workout
import com.example.fitlog.data.model.Exercise
import com.example.fitlog.data.repository.WorkoutRepository
import com.example.fitlog.data.repository.ExerciseRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

class HomeViewModel(
    private val workoutRepository: WorkoutRepository = WorkoutRepository(),
    private val exerciseRepository: ExerciseRepository = ExerciseRepository()
) : ViewModel() {

    var selectedDate = mutableStateOf(LocalDate.now())
        private set

    var dailyPlan = mutableStateOf(DailyPlan("", "", ""))
        private set

    var activityStats = mutableStateOf(ActivityStats(0f, 0))
        private set

    var recentWorkouts = mutableStateOf(emptyList<WorkoutSummary>())
        private set

    private val _workouts = MutableStateFlow<List<Workout>>(emptyList())
    val workouts: StateFlow<List<Workout>> = _workouts.asStateFlow()

    private val _selectedWorkout = MutableStateFlow<Workout?>(null)
    val selectedWorkout: StateFlow<Workout?> = _selectedWorkout.asStateFlow()

    private val _exercises = MutableStateFlow<List<Exercise>>(emptyList())
    val exercises: StateFlow<List<Exercise>> = _exercises.asStateFlow()

    fun onDateSelected(date: LocalDate) {
        selectedDate.value = date
        loadHomeDataForDate(date)
    }

    fun loadHomeDataForDate(date: LocalDate) {
        viewModelScope.launch {
            // Remove dummy data, set empty/zero states or fetch from repository if implemented
            dailyPlan.value = DailyPlan("", "", "")
            activityStats.value = ActivityStats(0f, 0)
            recentWorkouts.value = emptyList()
        }
    }

    fun loadWorkouts(userId: String) {
        workoutRepository.getWorkouts(userId) { list ->
            _workouts.value = list
        }
    }

    fun selectWorkout(userId: String, workoutId: String) {
        workoutRepository.getWorkoutById(userId, workoutId) { workout ->
            _selectedWorkout.value = workout
            if (workout != null) {
                loadExercises(userId, workout.id)
            } else {
                _exercises.value = emptyList()
            }
        }
    }

    fun loadExercises(userId: String, workoutId: String) {
        exerciseRepository.getExercises(userId, workoutId) { list ->
            _exercises.value = list
        }
    }

    init {
        // Removed: loadHomeDataForDate(selectedDate.value)
    }
}
