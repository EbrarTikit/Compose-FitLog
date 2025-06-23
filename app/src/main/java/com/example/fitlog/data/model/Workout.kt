package com.example.fitlog.data.model

import com.google.firebase.Timestamp

data class Workout(
    val id: String = "",
    val userId: String = "",
    val date: Timestamp = Timestamp.now(),
    val name: String = "",
    val duration: Int = 0,
    val calories: Int = 0,
    val createdAt: Timestamp = Timestamp.now(),
    val updatedAt: Timestamp = Timestamp.now()
)