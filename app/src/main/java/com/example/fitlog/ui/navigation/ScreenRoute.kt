package com.example.fitlog.ui.navigation

sealed class ScreenRoute(val route: String) {
    object SignIn : ScreenRoute("sign_in")
    object SignUp : ScreenRoute("sign_up")
    object Onboarding : ScreenRoute("onboarding")
    object Home : ScreenRoute("home")
}