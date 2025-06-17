package com.example.fitlog.ui.screens.home

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitlog.data.model.ActivityStats
import com.example.fitlog.data.model.DailyPlan
import com.example.fitlog.data.model.WorkoutSummary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel : ViewModel() {

    var selectedDate = mutableStateOf(LocalDate.now())
        private set

    var dailyPlan = mutableStateOf(DailyPlan("", "", ""))
        private set

    var activityStats = mutableStateOf(ActivityStats(0f, 0))
        private set

    var recentWorkouts = mutableStateOf(emptyList<WorkoutSummary>())
        private set

    fun onDateSelected(date: LocalDate) {
        selectedDate.value = date
        loadHomeDataForDate(date)
    }

    fun loadHomeDataForDate(date: LocalDate) {
        viewModelScope.launch {
            // Simulate loading delay (replace with Firestore fetch later)
            delay(500)

            // Dummy data for demonstration
            dailyPlan.value = DailyPlan(
                dayOfWeek = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() },
                workoutType = "Upper Body",
                day = "2"
            )

            activityStats.value = ActivityStats(
                calories = 520.4f,
                steps = 1023
            )

            recentWorkouts.value = listOf(
                WorkoutSummary(
                    id = "1",
                    name = "Chest Day",
                    calories = 180,
                    duration = 25,
                    image = com.example.fitlog.R.drawable.ic_run,
                    progress = 0.6f
                ),
                WorkoutSummary(
                    id = "2",
                    name = "Cardio Blast",
                    calories = 220,
                    duration = 30,
                    image = com.example.fitlog.R.drawable.ic_run,
                    progress = 0.8f
                )
            )
        }
    }

    init {
        loadHomeDataForDate(selectedDate.value)
    }
}
