package com.example.fitlog.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fitlog.data.local.preferences.UserPreferences
import com.example.fitlog.ui.screens.daylist.DayListScreen
import com.example.fitlog.ui.screens.detail.DetailScreen
import com.example.fitlog.ui.screens.editworkout.EditWorkoutScreen
import com.example.fitlog.ui.screens.home.HomeScreen
import com.example.fitlog.ui.screens.onboarding.OnboardingScreen
import com.example.fitlog.ui.screens.signin.SignInScreen
import com.example.fitlog.ui.screens.signup.SignUpScreen
import com.example.fitlog.ui.screens.splash.SplashScreen
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
            startDestination = ScreenRoute.Splash.route,
            modifier = modifier
        ) {
            composable(ScreenRoute.Splash.route) {
                SplashScreen(navController = navController)
            }
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

            composable(ScreenRoute.Home.route) {
                HomeScreen(
                    navController = navController
                )
            }

            composable(ScreenRoute.EditWorkout.route) {
                EditWorkoutScreen(
                    onSave = { name, duration, calories ->
                        println("Saved: $name, $duration, $calories")
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(ScreenRoute.DayList.route) {
                DayListScreen(
                    workoutsForDate = { date ->
                        // dummy data
                        listOf(
                            "Push-ups" to "3 sets",
                            "Pull-ups" to "4 sets",
                            "Squats" to "5 sets"
                        )
                    },
                    onSeeAllClick = { date ->
                        println("See all clicked for $date")
                        navController.navigate(ScreenRoute.Detail.route)
                    }
                )
            }

            composable(ScreenRoute.Detail.route) {
                DetailScreen(
                    workoutsForDate = { date ->
                        // Dummy data
                        if (date.dayOfMonth % 2 == 0) {
                            listOf(
                                "Push-ups" to "3 sets",
                                "Squats" to "4 sets"
                            )
                        } else {
                            emptyList()
                        }
                    },
                    onEditWorkoutClick = {
                        navController.navigate(ScreenRoute.EditWorkout.route)
                    },
                    onAddWorkoutClick = {
                        navController.navigate(ScreenRoute.EditWorkout.route)
                    },
                    onAddExerciseClick = {
                        navController.navigate(ScreenRoute.AddExercise.route)
                    }
                )
            }

        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }
