package com.example.fitlog

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.fitlog.data.local.preferences.PreferenceManager
import com.example.fitlog.data.local.preferences.UserPreferences
import com.example.fitlog.ui.screens.onboarding.OnboardingScreen
import com.example.fitlog.ui.screens.splash.SplashScreen
import com.example.fitlog.ui.theme.FitLogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Yeni Splash API kullanımı
        installSplashScreen()

        super.onCreate(savedInstanceState)
        setContent {
            FitLogTheme {
                MainScreen(context = this)
            }
        }
    }
}

@Composable
fun MainScreen(context: Context) {
    var showOnboardingScreen by remember { mutableStateOf(false) }

    val preferenceManager = remember { PreferenceManager(context) }
    val userPreferences = remember { UserPreferences(preferenceManager) }

    LaunchedEffect(Unit) {
        // Onboarding tamamlanmış mı kontrol et
        showOnboardingScreen = !userPreferences.isOnboardingCompleted()
    }

    if (showOnboardingScreen) {
        OnboardingScreen(onFinished = {
            userPreferences.setOnboardingCompleted(true)
            showOnboardingScreen = false
        })
    } else {
        // Ana uygulama içeriği
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Welcome to FitLog")
        }
    }
}




