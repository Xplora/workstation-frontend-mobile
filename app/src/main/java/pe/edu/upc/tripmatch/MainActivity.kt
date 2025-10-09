package pe.edu.upc.tripmatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import pe.edu.upc.tripmatch.presentation.navigation.Home
import pe.edu.upc.tripmatch.ui.theme.TripMatchTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TripMatchTheme {
                Home()
            }
        }
    }
}
