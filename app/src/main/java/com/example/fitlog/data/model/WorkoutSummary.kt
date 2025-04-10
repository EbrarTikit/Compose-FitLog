package com.example.fitlog.data.model

data class WorkoutSummary(
    val id: String,
    val name: String,
    val calories: Int,
    val duration: Int,
    val image: Int,
    val progress: Float = 0f
)
