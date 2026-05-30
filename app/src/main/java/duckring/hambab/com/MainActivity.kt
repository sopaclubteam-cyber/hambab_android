package duckring.hambab.com

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import duckring.hambab.com.ui.nav.HambabNavHost
import duckring.hambab.com.ui.theme.HambabTheme
import duckring.hambab.com.ui.theme.HbCream

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            HambabTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(HbCream),
                    color = HbCream,
                ) {
                    HambabNavHost()
                }
            }
        }
    }
}
