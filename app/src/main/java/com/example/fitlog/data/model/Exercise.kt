package com.example.fitlog.data.model

import com.google.firebase.Timestamp


data class Exercise(
    val id: String = "",
    val name: String = "",
    val sets: List<ExerciseSet> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)

data class ExerciseTemplate(
    val id: String = "",
    val name: String = "",
    val category: String = "", // e.g., "Chest", "Back", "Legs", etc.
    val description: String = "",
    val muscleGroups: List<String> = emptyList(),
    val equipment: String = "", // e.g., "Barbell", "Dumbbell", "Bodyweight"
    val isPopular: Boolean = false,
    val createdAt: Timestamp = Timestamp.now()
)
