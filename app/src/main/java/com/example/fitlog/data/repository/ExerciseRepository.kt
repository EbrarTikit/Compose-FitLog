package com.example.fitlog.data.repository

import com.example.fitlog.data.model.Exercise
import com.example.fitlog.data.model.ExerciseTemplate
import com.example.fitlog.data.model.ExerciseSet
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.Timestamp

class ExerciseRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    // Exercise Template Methods
    fun getExerciseTemplates(onResult: (List<ExerciseTemplate>) -> Unit) {
        db.collection("exerciseTemplates")
            .orderBy("isPopular", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .orderBy("name")
            .get()
            .addOnSuccessListener { snapshot ->
                val templates =
                    snapshot.documents.mapNotNull { it.toObject(ExerciseTemplate::class.java) }
                onResult(templates)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun getExerciseTemplatesByCategory(
        category: String,
        onResult: (List<ExerciseTemplate>) -> Unit
    ) {
        db.collection("exerciseTemplates")
            .whereEqualTo("category", category)
            .orderBy("name")
            .get()
            .addOnSuccessListener { snapshot ->
                val templates =
                    snapshot.documents.mapNotNull { it.toObject(ExerciseTemplate::class.java) }
                onResult(templates)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    fun addExerciseTemplate(template: ExerciseTemplate, onResult: (Boolean) -> Unit) {
        db.collection("exerciseTemplates").document(template.id)
            .set(template)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun searchExerciseTemplates(query: String, onResult: (List<ExerciseTemplate>) -> Unit) {
        db.collection("exerciseTemplates")
            .orderBy("name")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .addOnSuccessListener { snapshot ->
                val templates =
                    snapshot.documents.mapNotNull { it.toObject(ExerciseTemplate::class.java) }
                onResult(templates)
            }
            .addOnFailureListener { onResult(emptyList()) }
    }

    // Helper function to populate Firebase with sample exercise templates
    fun populateSampleExerciseTemplates(onResult: (Boolean) -> Unit) {
        // Bu fonksiyon devre dışı bırakıldı. Artık örnek veri eklemiyor.
        onResult(false)
    }

    // Existing Exercise Methods
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

    // Helper function to populate Firebase with sample exercises for testing
    fun populateSampleExercises(userId: String, workoutId: String, onResult: (Boolean) -> Unit) {
        val sampleExercises = listOf(
            Exercise(
                id = "ex1",
                name = "Push-ups",
                sets = listOf(
                    ExerciseSet(setType = "Warm Up", reps = 10, weight = 0f),
                    ExerciseSet(setType = "Set 1", reps = 15, weight = 0f),
                    ExerciseSet(setType = "Set 2", reps = 12, weight = 0f)
                ),
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            ),
            Exercise(
                id = "ex2",
                name = "Squats",
                sets = listOf(
                    ExerciseSet(setType = "Set 1", reps = 20, weight = 0f),
                    ExerciseSet(setType = "Set 2", reps = 18, weight = 0f),
                    ExerciseSet(setType = "Set 3", reps = 15, weight = 0f)
                ),
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            ),
            Exercise(
                id = "ex3",
                name = "Plank",
                sets = listOf(
                    ExerciseSet(setType = "Set 1", reps = 30, weight = 0f), // 30 seconds
                    ExerciseSet(setType = "Set 2", reps = 45, weight = 0f), // 45 seconds
                    ExerciseSet(setType = "Set 3", reps = 60, weight = 0f)  // 60 seconds
                ),
                createdAt = Timestamp.now(),
                updatedAt = Timestamp.now()
            )
        )

        val batch = db.batch()
        sampleExercises.forEach { exercise ->
            val docRef = db.collection("users").document(userId)
                .collection("workouts").document(workoutId)
                .collection("exercises").document(exercise.id)
            batch.set(docRef, exercise)
        }

        batch.commit()
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }
}
