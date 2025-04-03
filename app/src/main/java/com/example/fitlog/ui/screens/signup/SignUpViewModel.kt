package com.example.fitlog.ui.screens.signup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.fitlog.data.remote.FirebaseAuthManager

class SignUpViewModel : ViewModel() {
    private val authManager = FirebaseAuthManager()

    var firstName by mutableStateOf("")
    var lastName by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var passwordVisible by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var signUpSuccess by mutableStateOf<Boolean?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    fun onFirstNameChange(newFirstName: String) {
        firstName = newFirstName
    }

    fun onLastNameChange(newLastName: String) {
        lastName = newLastName
    }

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun signUp() {
        // Basic validation
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
            signUpSuccess = false
            errorMessage = "Please fill in all fields"
            return
        }

        // Password validation
        if (password.length < 6) {
            signUpSuccess = false
            errorMessage = "Password must be at least 6 characters"
            return
        }

        isLoading = true
        authManager.signUp(email, password) { success, error ->
            isLoading = false
            signUpSuccess = success
            errorMessage = error
        }
    }

    fun resetErrorState() {
        signUpSuccess = null
        errorMessage = null
    }
}