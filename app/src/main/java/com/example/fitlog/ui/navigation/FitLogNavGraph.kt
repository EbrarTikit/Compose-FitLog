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
import androidx.navigation.navArgument
import com.example.fitlog.data.local.preferences.UserPreferences
import com.example.fitlog.ui.screens.addexercise.AddExerciseScreen
import com.example.fitlog.ui.screens.daylist.DayListScreen
import com.example.fitlog.ui.screens.detail.DetailScreen
import com.example.fitlog.ui.screens.editworkout.EditWorkoutScreen
import com.example.fitlog.ui.screens.home.HomeScreen
import com.example.fitlog.ui.screens.onboarding.OnboardingScreen
import com.example.fitlog.ui.screens.signin.SignInScreen
import com.example.fitlog.ui.screens.signup.SignUpScreen
import com.example.fitlog.ui.screens.splash.SplashScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.Timestamp
import java.util.UUID
import com.example.fitlog.data.repository.WorkoutRepository
import com.example.fitlog.data.model.Workout
import kotlinx.coroutines.launch
import androidx.navigation.NavType
import java.time.ZoneId
import java.time.LocalDate

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
                val viewModel: com.example.fitlog.ui.screens.home.HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                HomeScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }

            composable(ScreenRoute.EditWorkout.route) {
                val viewModel: com.example.fitlog.ui.screens.home.HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
                val selectedDate = viewModel.selectedDate.value
                EditWorkoutScreen(
                    onSave = { name, duration, calories ->
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user != null) {
                            val userId = user.uid
                            val workoutId = UUID.randomUUID().toString()
                            val startOfDay = selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                            val timestamp = Timestamp(startOfDay / 1000, ((startOfDay % 1000) * 1000000).toInt())
                            val workout = Workout(
                                id = workoutId,
                                userId = userId,
                                name = name,
                                duration = duration.toIntOrNull() ?: 0,
                                calories = calories.toIntOrNull() ?: 0,
                                date = timestamp,
                                createdAt = Timestamp.now(),
                                updatedAt = Timestamp.now()
                            )
                            WorkoutRepository().addWorkout(userId, workout) { success ->
                                if (success) {
                                    navController.navigate("detail/$workoutId") {
                                        popUpTo(ScreenRoute.EditWorkout.route) { inclusive = true }
                                    }
                                }
                            }
                        }
                    },
                    onBack = {
                        navController.navigate(ScreenRoute.Home.route) {
                            popUpTo(ScreenRoute.Home.route) { inclusive = false }
                        }
                    }
                )
            }

            composable(
                route = "detail/{workoutId}",
                arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
                DetailScreen(
                    workoutId = workoutId,
                    onEditWorkoutClick = {
                        navController.navigate(ScreenRoute.EditWorkout.route)
                    },
                    onAddWorkoutClick = {
                        navController.navigate(ScreenRoute.EditWorkout.route)
                    },
                    onAddExerciseClick = {
                        navController.navigate(ScreenRoute.AddExercise.route)
                    },
                    onBackToHome = {
                        navController.navigate(ScreenRoute.Home.route) {
                            popUpTo(ScreenRoute.Home.route) { inclusive = false }
                        }
                    }
                )
            }

            composable(ScreenRoute.DayList.route) {
                DayListScreen(
                    workoutsForDate = { date ->
                        // TODO: Replace with real data source
                        emptyList()
                    },
                    onSeeAllClick = { date ->
                        // TODO: Implement navigation or action
                        navController.navigate(ScreenRoute.Detail.route)
                    }
                )
            }

            composable(ScreenRoute.AddExercise.route) {
                AddExerciseScreen(
                    // TODO: Replace with real data source
                    workouts = emptyList(),
                    onAddNewWorkout = {
                        navController.navigate(ScreenRoute.EditWorkout.route)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }


        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }
