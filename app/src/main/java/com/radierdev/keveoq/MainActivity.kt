package com.radierdev.keveoq

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.radierdev.keveoq.presentation.KeveOQApp
import com.radierdev.keveoq.presentation.theme.KeveOQTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KeveOQTheme {
                KeveOQApp()
            }
        }
    }
}
