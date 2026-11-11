package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.ScaleScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SlateBackground
import com.example.viewmodel.ScaleViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ScaleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = SlateBackground
                ) {
                    ScaleScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Resume sensor hardware listeners when app is in foreground
        viewModel.sensorManager.startSensorListeners()
    }

    override fun onPause() {
        super.onPause()
        // Pause sensor hardware listeners when backgrounded to prevent battery drain and memory pressure
        viewModel.sensorManager.stopSensorListeners()
    }
}
