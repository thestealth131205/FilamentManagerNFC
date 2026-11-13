package de.letheapp.filamentmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.letheapp.filamentmanager.ui.AppNavigation
import de.letheapp.filamentmanager.ui.theme.FilamentManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = (application as FilamentManagerApp).database

        setContent {
            FilamentManagerTheme {
                AppNavigation(database = database)
            }
        }
    }
}
