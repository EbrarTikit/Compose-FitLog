package com.example.fitlog.data.model

import com.google.firebase.Timestamp


data class Exercise(
    val id: String = "",
    val name: String = "",
    val sets: List<ExerciseSet> = emptyList(),
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)
