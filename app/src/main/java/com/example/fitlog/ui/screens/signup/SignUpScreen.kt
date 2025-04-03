package com.example.fitlog.ui.screens.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.fitlog.ui.components.ButtonUI
import com.example.fitlog.ui.theme.FitLogTheme
import com.example.fitlog.ui.theme.PrimaryPurple
import com.example.fitlog.ui.components.CustomPasswordField
import com.example.fitlog.ui.components.CustomTextField

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = viewModel(),
    onSignUpSuccess: () -> Unit,
    onSignInClick: () -> Unit
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
            SignUpHeader()
            
            Spacer(modifier = Modifier.height(32.dp))
            
            SignUpForm(
                firstName = viewModel.firstName,
                onFirstNameChange = viewModel::onFirstNameChange,
                lastName = viewModel.lastName,
                onLastNameChange = viewModel::onLastNameChange,
                email = viewModel.email,
                onEmailChange = viewModel::onEmailChange,
                password = viewModel.password,
                onPasswordChange = viewModel::onPasswordChange,
                passwordVisible = viewModel.passwordVisible,
                onTogglePasswordVisibility = viewModel::togglePasswordVisibility
            )
            
            PasswordRequirements()
            
            Spacer(modifier = Modifier.weight(1f))
            
            SignUpButton(
                isLoading = viewModel.isLoading,
                onClick = viewModel::signUp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SignInLink(onSignInClick = onSignInClick)
            
            // Show error if signup fails
            viewModel.errorMessage?.let { error ->
                ErrorMessage(error)
            }
        }
    }
    
    // Navigate to home screen on successful signup
    LaunchedEffect(viewModel.signUpSuccess) {
        if (viewModel.signUpSuccess == true) {
            onSignUpSuccess()
        }
    }
}

@Composable
fun SignUpHeader() {
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
            text = "Create an Account",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
fun SignUpForm(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onTogglePasswordVisibility: () -> Unit
) {
    FirstNameField(
        firstName = firstName,
        onFirstNameChange = onFirstNameChange
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    LastNameField(
        lastName = lastName,
        onLastNameChange = onLastNameChange
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
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
fun FirstNameField(
    firstName: String,
    onFirstNameChange: (String) -> Unit
) {
    CustomTextField(
        value = firstName,
        onValueChange = onFirstNameChange,
        label = "First Name",
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun LastNameField(
    lastName: String,
    onLastNameChange: (String) -> Unit
) {
    CustomTextField(
        value = lastName,
        onValueChange = onLastNameChange,
        label = "Last Name",
        modifier = Modifier.fillMaxWidth()
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
        keyboardType = KeyboardType.Email,
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
fun PasswordRequirements() {
    Text(
        text = "Your password must be at least 6 characters and contain a number or a symbol.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

@Composable
fun SignUpButton(
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
            Text("REGISTER")
        }
    }
}

@Composable
fun SignInLink(onSignInClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Already have an account?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextButton(onClick = onSignInClick) {
            Text(
                text = "Login",
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
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    FitLogTheme {
        SignUpScreen(
            onSignUpSuccess = {},
            onSignInClick = {}
        )
    }
}

