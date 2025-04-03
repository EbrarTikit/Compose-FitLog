package com.example.fitlog.data.model

import androidx.annotation.DrawableRes

sealed class Onboarding(
    @DrawableRes val image: Int,
    val title: String,
    val description: String,
    ) {

    data object First : Onboarding(
        image = com.example.fitlog.R.drawable.onboarding1,
        title = "Track Your Goal",
        description = "Your personal fitness companion. Track your workouts, monitor your progress, and achieve your fitness goals with ease."
    )

    data object Second : Onboarding(
        image = com.example.fitlog.R.drawable.onboarding2,
        title = "Track Your Workouts",
        description = "Log your exercises, sets, reps, and weights. Keep a detailed record of your fitness journey."
    )

    data object Third : Onboarding(
        image = com.example.fitlog.R.drawable.onboarding3,
        title = "Monitor Your Progress",
        description = "Visualize your progress with charts and statistics. Stay motivated by seeing how far you've come!"
    )

}
