package com.example.fitlog.ui.screens.signin

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.fitlog.data.remote.FirebaseAuthManager

class SignInViewModel : ViewModel() {
    private val authManager = FirebaseAuthManager()

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var passwordVisible by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var loginSuccess by mutableStateOf<Boolean?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    fun onEmailChange(newEmail: String) {
        email = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
    }

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun login() {
        if (email.isEmpty() || password.isEmpty()) {
            loginSuccess = false
            errorMessage = "Email and password cannot be empty"
            return
        }

        isLoading = true
        authManager.login(email, password) { success, error ->
            isLoading = false
            loginSuccess = success
            errorMessage = error
        }
    }

    fun resetErrorState() {
        loginSuccess = null
        errorMessage = null
    }
}