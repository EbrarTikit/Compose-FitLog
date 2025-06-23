package com.example.fitlog

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.fitlog.ui.navigation.FitLogNavGraph
import com.example.fitlog.ui.theme.FitLogTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FitLogApp()
        }
    }
}

@Composable
fun FitLogApp() {
    FitLogTheme {
        val navController = rememberNavController()

        Scaffold { innerPadding ->
            FitLogNavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }

    }
}



//@Composable
//fun MainScreen(context: Context) {
//    var showOnboardingScreen by remember { mutableStateOf(false) }
//
//    val preferenceManager = remember { PreferenceManager(context) }
//    val userPreferences = remember { UserPreferences(preferenceManager) }
//
//    LaunchedEffect(Unit) {
//        // Onboarding tamamlanmış mı kontrol et
//        showOnboardingScreen = !userPreferences.isOnboardingCompleted()
//    }
//
//    if (showOnboardingScreen) {
//        OnboardingScreen(onFinished = {
//            userPreferences.setOnboardingCompleted(true)
//            showOnboardingScreen = false
//        })
//    } else {
//        // Ana uygulama içeriği
//        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//            Text(text = "Welcome to FitLog")
//        }
//    }
//}




