package com.example.fitlog

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.fitlog.ui.screens.splash.SplashScreen
import com.example.fitlog.ui.theme.FitLogTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FitLogTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var showSplashScreen by remember { mutableStateOf(true) }
    
    if (showSplashScreen) {
        SplashScreen(onTimeout = { showSplashScreen = false })
    } else {
        // Your main app content goes here
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Welcome to FitLog")
        }
    }
}



