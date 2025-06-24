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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Timestamp
import java.time.ZoneId
import android.util.Log

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
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val userId = user.uid
                val zoneId = ZoneId.systemDefault()
                val startOfDay = date.atStartOfDay(zoneId).toInstant().toEpochMilli()
                val endOfDay = date.plusDays(1).atStartOfDay(zoneId).toInstant().toEpochMilli()
                val startTimestamp = Timestamp(startOfDay / 1000, ((startOfDay % 1000) * 1000000).toInt())
                val endTimestamp = Timestamp(endOfDay / 1000, ((endOfDay % 1000) * 1000000).toInt())
                Log.d("FitLog", "userId=$userId, start=$startTimestamp, end=$endTimestamp, date=$date")

                // Load workout for specific date
                workoutRepository.getWorkoutByDateRange(userId, startTimestamp, endTimestamp) { workout ->
                    Log.d("FitLog", "workout result: $workout")
                    if (workout != null) {
                        dailyPlan.value = DailyPlan(
                            day = date.dayOfMonth.toString(),
                            workoutType = workout.name,
                            dayOfWeek = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
                        )
                    } else {
                        dailyPlan.value = DailyPlan("", "", "")
                    }
                }

                // Always load recent workouts
                workoutRepository.getWorkouts(userId) { workoutList ->
                    val recentWorkoutSummaries = workoutList.take(3).map { workout ->
                        WorkoutSummary(
                            id = workout.id,
                            name = workout.name,
                            calories = workout.calories,
                            duration = workout.duration,
                            image = com.example.fitlog.R.drawable.ic_run,
                            progress = 0.7f // Default progress
                        )
                    }
                    recentWorkouts.value = recentWorkoutSummaries
                }
            } else {
                dailyPlan.value = DailyPlan("", "", "")
                recentWorkouts.value = emptyList()
            }
            activityStats.value = ActivityStats(0f, 0)
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
        loadHomeDataForDate(selectedDate.value)
    }

    fun initializeData() {
        loadHomeDataForDate(selectedDate.value)
    }
}
