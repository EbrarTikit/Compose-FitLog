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
    var loginSuccess by mutableStateOf<Boolean?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    fun login() {
        authManager.login(email, password) { success, error ->
            loginSuccess = success
            errorMessage = error
        }
    }
}