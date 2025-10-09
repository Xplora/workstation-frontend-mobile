@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package pe.edu.upc.tripmatch.presentation.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import pe.edu.upc.tripmatch.R
import pe.edu.upc.tripmatch.presentation.di.PresentationModule
import pe.edu.upc.tripmatch.presentation.view.AgencyDashboardScreen
import pe.edu.upc.tripmatch.presentation.view.AuthFlowScreen
import pe.edu.upc.tripmatch.presentation.view.AuthScreen
import pe.edu.upc.tripmatch.presentation.view.ExperienceListScreen
import pe.edu.upc.tripmatch.presentation.view.FavoritesScreen
import pe.edu.upc.tripmatch.presentation.viewmodel.AuthViewModel

data class NavigationItem(val title: String, val route: String)

@Composable
fun MainAppContent(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val ui by authViewModel.uiState.collectAsState()
    val isAgency = ui.isAgency

    val Teal = Color(0xFF318C8B)
    val TextSecondary = Color(0xFF58636A)

    val navigationItems = if (isAgency) {
        listOf(
            NavigationItem("Inicio", "home"),
            NavigationItem("Experiencias", "manage_experiences"),
            NavigationItem("Reservas", "bookings"),
            NavigationItem("Consultas", "queries"),
            NavigationItem("Perfil", "profile")
        )
    } else {
        listOf(
            NavigationItem("Inicio", "home"),
            NavigationItem("Favoritos", "favorites"),
            NavigationItem("Itinerarios", "itineraries"),
            NavigationItem("Perfil", "profile")
        )
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: navigationItems.first().route

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                modifier = Modifier.height(90.dp),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(R.drawable.ic_tripmatch_logo),
                            contentDescription = "TripMatch Logo",
                            modifier = Modifier
                                .size(60.dp)
                                .padding(end = 16.dp)
                        )
                        Text(
                            text = "TripMatch",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 25.sp,
                            letterSpacing = 0.2.sp,
                            color = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { authViewModel.logout() }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Cerrar Sesión",
                            tint = Color.Red.copy(alpha = 0.7f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            Column {
                Divider(color = Color(0x1F000000))
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Teal,
                    tonalElevation = 0.dp
                ) {
                    navigationItems.forEach { item ->
                        val selected = currentRoute == item.route
                        val icon = when (item.title) {
                            "Inicio" -> Icons.Filled.Home
                            "Favoritos" -> Icons.Filled.Favorite
                            "Itinerarios" -> Icons.Filled.CalendarMonth
                            "Experiencias" -> Icons.Filled.Edit
                            "Reservas" -> Icons.Filled.Event
                            "Consultas" -> Icons.Filled.QuestionAnswer
                            "Perfil" -> Icons.Filled.AccountCircle
                            else -> Icons.Filled.Home
                        }
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Teal,
                                selectedTextColor = Teal,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = Color(0x14318C8B)
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // HOME: cambia según rol
            composable("home") {
                if (isAgency) {
                    AgencyDashboardScreen(
                        agencyName = ui.name.ifBlank { "Tu Agencia" },
                        onAddExperience = { /* navController.navigate("manage_experiences") */ },
                        onViewAllReviews = { /* navController.navigate("profile") */ },
                        onViewAllBookings = { /* navController.navigate("bookings") */ }
                    )
                } else {
                    // Ahora la lista de experiencias ocupa todo el espacio disponible
                    ExperienceListScreen()
                }
            }

            // PESTAÑAS AGENCY
            if (isAgency) {
                composable("manage_experiences") { Text("Pantalla de Gestión de Experiencias") }
                composable("bookings") { Text("Pantalla de Reservas") }
                composable("queries") { Text("Pantalla de Consultas") }
                composable("profile") { Text("Pantalla de Perfil de Agencia") }
            } else {
                // PESTAÑAS TURISTA
                composable("favorites") { FavoritesScreen() }
                composable("itineraries") { Text("Pantalla de Itinerarios") }
                composable("profile") { Text("Pantalla de Perfil de Turista") }
            }
        }
    }
}

@Composable
fun Home() {
    val authViewModel: AuthViewModel = remember { PresentationModule.getAuthViewModel() }
    val uiState by authViewModel.uiState.collectAsState()

    // Decide qué flujo mostrar: Autenticación o la App Principal
    if (uiState.currentUser == null) {
        AuthScreen(
            authViewModel = authViewModel,
            onLoginSuccess = { /* La recomposición se encarga de cambiar de pantalla */ }
        )
    } else {
        MainAppContent(authViewModel = authViewModel)
    }
}