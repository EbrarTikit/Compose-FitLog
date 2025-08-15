package com.example.fitlog.ui.screens.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.fitlog.data.model.ExerciseTemplate
import com.example.fitlog.data.repository.ExerciseRepository

class DetailViewModel(
    private val exerciseRepository: ExerciseRepository = ExerciseRepository()
) : ViewModel() {

    var exerciseTemplates by mutableStateOf<List<ExerciseTemplate>>(emptyList())
        private set

    var searchQuery by mutableStateOf("")
        private set

    var selectedCategory by mutableStateOf("All")
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun loadExerciseTemplates() {
        isLoading = true
        exerciseRepository.getExerciseTemplates { templates ->
            exerciseTemplates = templates
            isLoading = false
        }
    }

    init {
        loadExerciseTemplates()
    }
}