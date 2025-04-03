package com.example.fitlog.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fitlog.data.local.preferences.UserPreferences
import com.example.fitlog.ui.screens.home.HomeScreen
import com.example.fitlog.ui.screens.onboarding.OnboardingScreen
import com.example.fitlog.ui.screens.signin.SignInScreen
import com.example.fitlog.ui.screens.signup.SignUpScreen
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun FitLogNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }

    var startDestination by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val isFirstLaunch = userPreferences.isFirstLaunch()
            startDestination = when {
                isFirstLaunch -> ScreenRoute.Onboarding.route
                FirebaseAuth.getInstance().currentUser != null -> ScreenRoute.Home.route
                else -> ScreenRoute.SignIn.route
            }
        }
    }

    if (startDestination != null) {
        NavHost(
            navController = navController,
            startDestination = startDestination!!,
            modifier = modifier
        ) {
            composable(ScreenRoute.Onboarding.route) {
                OnboardingScreen(
                    navController = navController,
                    onFinish = {
                        navController.navigate(ScreenRoute.SignIn.route) {
                            popUpTo(ScreenRoute.Onboarding.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(ScreenRoute.SignUp.route) {
                SignUpScreen(
                    onSignUpSuccess = { navController.navigate(ScreenRoute.Home.route) },
                    onSignInClick = { navController.navigate(ScreenRoute.SignIn.route) }
                )
            }
            composable(ScreenRoute.SignIn.route) {
                SignInScreen(
                    onSignInSuccess = { navController.navigateSingleTopTo(ScreenRoute.Home.route) },
                    onSignUpClick = { navController.navigate(ScreenRoute.SignUp.route) }

                )
            }
            composable(ScreenRoute.Home.route) { HomeScreen() }
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }
