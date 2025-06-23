package com.example.fitlog.data.repository

import com.example.fitlog.data.model.Exercise
import com.example.fitlog.data.model.ExerciseSet
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class ExerciseRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    fun addExercise(userId: String, workoutId: String, exercise: Exercise, onResult: (Boolean) -> Unit) {
        val docRef = db.collection("users").document(userId)
            .collection("workouts").document(workoutId)
            .collection("exercises").document(exercise.id)
        docRef.set(exercise, SetOptions.merge())
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getExercises(userId: String, workoutId: String, onResult: (List<Exercise>) -> Unit) {
        db.collection("users").document(userId)
            .collection("workouts").document(workoutId)
            .collection("exercises")
            .get()
            .addOnSuccessListener { snapshot ->
                val exercises = snapshot.documents.mapNotNull { it.toObject(Exercise::class.java) }
                onResult(exercises)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getExercise(userId: String, workoutId: String, exerciseId: String, onResult: (Exercise?) -> Unit) {
        db.collection("users").document(userId)
            .collection("workouts").document(workoutId)
            .collection("exercises").document(exerciseId)
            .get()
            .addOnSuccessListener { doc ->
                val exercise = doc.toObject(Exercise::class.java)
                onResult(exercise)
            }
            .addOnFailureListener { onResult(null) }
    }

    fun updateExercise(userId: String, workoutId: String, exercise: Exercise, onResult: (Boolean) -> Unit) {
        addExercise(userId, workoutId, exercise, onResult)
    }

    fun deleteExercise(userId: String, workoutId: String, exerciseId: String, onResult: (Boolean) -> Unit) {
        db.collection("users").document(userId)
            .collection("workouts").document(workoutId)
            .collection("exercises").document(exerciseId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
} 