package com.geoffrey.blackjackpractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.geoffrey.blackjackpractice.ui.navigation.BlackjackNavHost
import com.geoffrey.blackjackpractice.ui.theme.BlackjackPracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BlackjackPracticeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    BlackjackNavHost()
                }
            }
        }
    }
}
