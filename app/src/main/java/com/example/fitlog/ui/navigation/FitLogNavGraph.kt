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
import com.example.fitlog.ui.screens.exercisedetail.ExerciseDetailScreen
import com.example.fitlog.ui.screens.daylist.DayListScreen
import com.example.fitlog.ui.screens.detail.DetailScreen
import com.example.fitlog.ui.screens.editworkout.EditWorkoutScreen
import com.example.fitlog.ui.screens.home.HomeScreen
import com.example.fitlog.ui.screens.onboarding.OnboardingScreen
import com.example.fitlog.ui.screens.signin.SignInScreen
import com.example.fitlog.ui.screens.signup.SignUpScreen
import com.example.fitlog.ui.screens.splash.SplashScreen
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID
import com.example.fitlog.data.repository.WorkoutRepository
import com.example.fitlog.data.model.Workout
import kotlinx.coroutines.launch
import androidx.navigation.NavType
import java.time.ZoneId
import java.time.LocalDate
import com.google.firebase.Timestamp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.example.fitlog.ui.screens.logtracking.AnalyticsScreen
import com.example.fitlog.ui.screens.profile.ProfileScreen

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
                MainTabsScaffold(rootNavController = navController)
            }

            composable(
                route = "${ScreenRoute.EditWorkout.route}?date={date}",
                arguments = listOf(
                    navArgument("date") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val dateMillis = backStackEntry.arguments?.getLong("date") ?: -1L
                EditWorkoutScreen(
                    onSave = { name, duration, calories ->
                        val user = FirebaseAuth.getInstance().currentUser
                        if (user != null) {
                            val userId = user.uid
                            val workoutId = UUID.randomUUID().toString()
                            val targetDate = if (dateMillis > 0) java.time.Instant.ofEpochMilli(dateMillis).atZone(ZoneId.systemDefault()).toLocalDate() else LocalDate.now()
                            val startOfDay = targetDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
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
                route = "detail/{workoutId}?date={date}",
                arguments = listOf(
                    navArgument("workoutId") { type = NavType.StringType },
                    navArgument("date") {
                        type = NavType.LongType
                        defaultValue = -1L
                    }
                )
            ) { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
                val dateMillis = backStackEntry.arguments?.getLong("date") ?: -1L
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
                    },
                    onExerciseSelected = { exerciseTemplate ->
                        navController.navigate("${ScreenRoute.AddExercise.route}?exerciseId=${exerciseTemplate.id}")
                    },
                    navController = navController,
                    initialDateMillis = if (dateMillis > 0) dateMillis else null
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

            composable(
                route = "add_exercise/{workoutId}",
                arguments = listOf(navArgument("workoutId") { type = NavType.StringType })
            ) { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
                AddExerciseScreen(
                    workouts = emptyList(),
                    onAddNewWorkout = { navController.navigate(ScreenRoute.EditWorkout.route) },
                    onBack = { navController.popBackStack() },
                    workoutId = workoutId,
                    navController = navController
                )
            }

            composable(
                route = "exercise_detail/{workoutId}/{exerciseId}",
                arguments = listOf(
                    navArgument("workoutId") { type = NavType.StringType },
                    navArgument("exerciseId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val workoutId = backStackEntry.arguments?.getString("workoutId") ?: ""
                val exerciseId = backStackEntry.arguments?.getString("exerciseId") ?: ""
                ExerciseDetailScreen(
                    workoutId = workoutId,
                    exerciseId = exerciseId,
                    onBack = { navController.popBackStack() },
                    onSaved = { navController.popBackStack() }
                )
            }

        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) { launchSingleTop = true }

@Composable
private fun MainTabsScaffold(rootNavController: NavHostController) {
    var selectedIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Home", "Analytics", "Profile")

    Scaffold(
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, label ->
                    val icon = when (label) {
                        "Home" -> Icons.Filled.Home
                        "Analytics" -> Icons.Filled.ShowChart
                        else -> Icons.Filled.Person
                    }
                    NavigationBarItem(
                        selected = selectedIndex == index,
                        onClick = { selectedIndex = index },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            when (selectedIndex) {
                0 -> {
                    val viewModel: com.example.fitlog.ui.screens.home.HomeViewModel =
                        androidx.lifecycle.viewmodel.compose.viewModel()
                    HomeScreen(navController = rootNavController, viewModel = viewModel)
                }

                1 -> AnalyticsScreen()
                2 -> ProfileScreen()
            }
        }
    }
}
