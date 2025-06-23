package com.example.fitlog.data.repository

import com.example.fitlog.data.model.Workout
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class WorkoutRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    fun addWorkout(userId: String, workout: Workout, onResult: (Boolean) -> Unit) {
        val docRef = db.collection("users").document(userId)
            .collection("workouts").document(workout.id)
        val workoutWithTimestamps = workout.copy(
            createdAt = workout.createdAt.takeIf { it != Timestamp.now() } ?: Timestamp.now(),
            updatedAt = Timestamp.now()
        )
        docRef.set(workoutWithTimestamps, SetOptions.merge())
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getWorkoutByDate(userId: String, date: Timestamp, onResult: (Workout?) -> Unit) {
        db.collection("users").document(userId)
            .collection("workouts")
            .whereEqualTo("date", date)
            .get()
            .addOnSuccessListener { snapshot ->
                val workout = snapshot.documents.firstOrNull()?.toObject(Workout::class.java)
                onResult(workout)
            }
            .addOnFailureListener { onResult(null) }
    }

    fun getWorkoutById(userId: String, workoutId: String, onResult: (Workout?) -> Unit) {
        db.collection("users").document(userId)
            .collection("workouts").document(workoutId)
            .get()
            .addOnSuccessListener { doc ->
                val workout = doc.toObject(Workout::class.java)
                onResult(workout)
            }
            .addOnFailureListener { onResult(null) }
    }

    fun getWorkouts(userId: String, onResult: (List<Workout>) -> Unit) {
        db.collection("users").document(userId)
            .collection("workouts")
            .orderBy("date")
            .get()
            .addOnSuccessListener { snapshot ->
                val workouts = snapshot.documents.mapNotNull { it.toObject(Workout::class.java) }
                onResult(workouts)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun updateWorkout(userId: String, workout: Workout, onResult: (Boolean) -> Unit) {
        val docRef = db.collection("users").document(userId)
            .collection("workouts").document(workout.id)
        val workoutWithUpdatedAt = workout.copy(updatedAt = Timestamp.now())
        docRef.set(workoutWithUpdatedAt, SetOptions.merge())
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun deleteWorkout(userId: String, workoutId: String, onResult: (Boolean) -> Unit) {
        db.collection("users").document(userId)
            .collection("workouts").document(workoutId)
            .delete()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getWorkoutByDateRange(userId: String, start: Timestamp, end: Timestamp, onResult: (Workout?) -> Unit) {
        db.collection("users").document(userId)
            .collection("workouts")
            .whereGreaterThanOrEqualTo("date", start)
            .whereLessThan("date", end)
            .get()
            .addOnSuccessListener { snapshot ->
                val workout = snapshot.documents.firstOrNull()?.toObject(Workout::class.java)
                onResult(workout)
            }
            .addOnFailureListener { onResult(null) }
    }
} 