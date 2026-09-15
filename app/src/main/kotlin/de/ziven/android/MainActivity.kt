package de.ziven.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import de.ziven.android.navigation.ZivenNavHost
import de.ziven.android.ui.theme.ZivenTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZivenTheme {
                Surface {
                    val navController = rememberNavController()
                    ZivenNavHost(navController = navController)
                }
            }
        }
    }
}
