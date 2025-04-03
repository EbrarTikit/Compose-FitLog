package com.example.fitlog.ui.screens.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlog.ui.components.CustomPasswordField
import com.example.fitlog.ui.components.CustomTextField

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = viewModel(),
    onSignInSuccess: () -> Unit,
    onSignUpClick: () -> Unit
) {
    // Reset error state when screen is launched
    LaunchedEffect(Unit) {
        viewModel.resetErrorState()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            SignInHeader()
            
            Spacer(modifier = Modifier.height(32.dp))
            
            SignInForm(
                email = viewModel.email,
                onEmailChange = viewModel::onEmailChange,
                password = viewModel.password,
                onPasswordChange = viewModel::onPasswordChange,
                passwordVisible = viewModel.passwordVisible,
                onTogglePasswordVisibility = viewModel::togglePasswordVisibility
            )
            
            ForgotPasswordLink()
            
            Spacer(modifier = Modifier.weight(1f))
            
            SignInButton(
                isLoading = viewModel.isLoading,
                onClick = viewModel::login
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SignUpLink(onSignUpClick = onSignUpClick)
            
            // Show error if login fails
            viewModel.errorMessage?.let { error ->
                ErrorMessage(error)
            }
        }
    }
    
    // Navigate to home screen on successful login
    LaunchedEffect(viewModel.loginSuccess) {
        if (viewModel.loginSuccess == true) {
            onSignInSuccess()
        }
    }
}

@Composable
fun SignInHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Text(
            text = "Hey there,",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Welcome Back",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun SignInForm(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit
) {
    EmailField(
        email = email,
        onEmailChange = onEmailChange
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    PasswordField(
        password = password,
        onPasswordChange = onPasswordChange,
        passwordVisible = passwordVisible,
        onTogglePasswordVisibility = onTogglePasswordVisibility
    )
}

@Composable
fun EmailField(
    email: String,
    onEmailChange: (String) -> Unit
) {
    CustomTextField(
        value = email,
        onValueChange = onEmailChange,
        label = "Email",
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun PasswordField(
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit
) {
    CustomPasswordField(
        value = password,
        onValueChange = onPasswordChange,
        label = "Password",
        passwordVisible = passwordVisible,
        onTogglePasswordVisibility = onTogglePasswordVisibility,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun ForgotPasswordLink() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        TextButton(
            onClick = { /* Handle forgot password */ },
            modifier = Modifier.align(androidx.compose.ui.Alignment.CenterEnd)
        ) {
            Text(
                text = "Forgot your password?",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun SignInButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text("LOG IN")
        }
    }
}

@Composable
fun SignUpLink(onSignUpClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Don't have an account?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(onClick = onSignUpClick) {
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun ErrorMessage(error: String) {
    Text(
        text = error,
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(top = 8.dp)
    )
}