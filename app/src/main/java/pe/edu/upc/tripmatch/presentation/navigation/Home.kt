package pe.edu.upc.tripmatch.presentation.navigation

import androidx.compose.foundation.layout.Box // Importar Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pe.edu.upc.tripmatch.presentation.di.PresentationModule
import pe.edu.upc.tripmatch.presentation.view.AuthFlowScreen
import pe.edu.upc.tripmatch.presentation.view.AuthScreen
import pe.edu.upc.tripmatch.presentation.view.ExperienceListScreen
import pe.edu.upc.tripmatch.presentation.view.FavoritesScreen
import pe.edu.upc.tripmatch.presentation.viewmodel.AuthViewModel

data class NavigationItem(val title: String, val route: String)

/**
 * Define la estructura de la aplicación principal (después del login), incluyendo la barra de navegación inferior.
 * Este componente asume que el usuario está autenticado.
 */
@Composable
fun MainAppContent(authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    val currentUser by authViewModel.uiState.collectAsState()
    val isAgency = currentUser.currentUser?.role == "agency"

    val navigationItems = if (isAgency) {
        listOf(
            NavigationItem("Inicio", "home"),
            NavigationItem("Gestión de Experiencias", "manage_experiences"),
            NavigationItem("Reservas", "bookings"),
            NavigationItem("Consultas", "queries"),
            NavigationItem("Perfil", "profile")
        )
    } else {
        listOf(
            NavigationItem("Inicio", "home"),
            NavigationItem("Favoritos", "favorites"),
            NavigationItem("Itinerarios", "itineraries")
        )
    }

    val selectedIndex = remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomAppBar {
                navigationItems.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedIndex.value == index,
                        onClick = {
                            selectedIndex.value = index
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = {
                            Icon(
                                when (item.title) {
                                    "Inicio" -> Icons.Default.Home
                                    "Favoritos" -> Icons.Default.Favorite
                                    else -> Icons.Default.Home
                                }, contentDescription = item.title
                            )
                        },
                        label = { Text(item.title) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") {
                Column(Modifier.fillMaxSize()) {
                    ExperienceListScreen( modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(16.dp))
                    // Botón de logout
                    Button(
                        onClick = { authViewModel.logout() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("CERRAR SESIÓN")
                    }
                }
            }
            composable("favorites") { FavoritesScreen() }
            composable("manage_experiences") { /* Pantalla de gestión */ }
            composable("bookings") { /* Pantalla de reservas */ }
            composable("queries") { /* Pantalla de consultas */ }
            composable("itineraries") { /* Pantalla de itinerarios */ }
            composable("profile") { /* Perfil de usuario */ }
        }
    }
}

@Composable
fun Home() {
    val authViewModel: AuthViewModel = PresentationModule.getAuthViewModel()
    val uiState by authViewModel.uiState.collectAsState()


    val currentScreen = if (uiState.currentUser != null) AuthFlowScreen.MainApp else AuthFlowScreen.Login

    when (currentScreen) {
        AuthFlowScreen.Login, AuthFlowScreen.Register -> {
            AuthScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { /* Compose se recompondrá automáticamente */ }
            )
        }
        AuthFlowScreen.MainApp -> {
            MainAppContent(authViewModel = authViewModel)
        }
    }
}

