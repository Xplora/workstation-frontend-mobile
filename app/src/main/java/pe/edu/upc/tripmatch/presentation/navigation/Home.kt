@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package pe.edu.upc.tripmatch.presentation.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import pe.edu.upc.tripmatch.presentation.view.*
import pe.edu.upc.tripmatch.presentation.viewmodel.AuthViewModel

data class NavigationItem(val title: String, val route: String)

@Composable
private fun AppBarCompact(
    onOpenProfile: () -> Unit,
    onLogout: () -> Unit,
    avatarUrl: String? = null
) {
    Surface(
        color = Color.White,
        tonalElevation = 2.dp,
        shadowElevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 50.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_tripmatch_logo),
                    contentDescription = "TripMatch",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),

                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "TripMatch",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 22.sp,
                    color = Color.Black
                )
                Spacer(Modifier.weight(1f))

                IconButton(
                    onClick = onOpenProfile,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Perfil",
                        tint = Color(0xFF58636A),
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(onClick = onLogout) {
                    Icon(
                        imageVector = Icons.Filled.Logout,
                        contentDescription = "Cerrar sesión",
                        tint = Color(0xFFD32F2F)
                    )
                }
            }
            HorizontalDivider(color = Color(0x1F000000), thickness = 1.dp)
        }
    }
}

@Composable
fun MainAppContent(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val ui by authViewModel.uiState.collectAsState()
    val isAgency = ui.isAgency

    val Teal = Color(0xFF318C8B)
    val TextSecondary = Color(0xFF58636A)

    val bottomNavItems = if (isAgency) {
        listOf(
            NavigationItem("Inicio", "home"),
            NavigationItem("Experiencias", "manage_experiences"),
            NavigationItem("Reservas", "bookings"),
            NavigationItem("Consultas", "queries")
        )
    } else {
        listOf(
            NavigationItem("Inicio", "home"),
            NavigationItem("Favoritos", "favorites"),
            NavigationItem("Itinerarios", "itineraries")
        )
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: "home"

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            AppBarCompact(
                onOpenProfile = {
                    navController.navigate("profile") {
                        launchSingleTop = true
                    }
                },
                onLogout = { authViewModel.logout() }
            )
        },
        bottomBar = {
            if (currentRoute != "profile") {
                NavigationBar(
                    containerColor = Color.White,
                    contentColor = Teal,
                    tonalElevation = 3.dp
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = currentRoute == item.route
                        val icon = when (item.route) {
                            "home" -> Icons.Filled.Home
                            "favorites" -> Icons.Filled.Favorite
                            "itineraries" -> Icons.Filled.CalendarMonth
                            "manage_experiences" -> Icons.Filled.Edit
                            "bookings" -> Icons.Filled.Event
                            "queries" -> Icons.Filled.QuestionAnswer
                            else -> Icons.Filled.Home
                        }
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(item.route) {
                                        popUpTo("home") { saveState = true }
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
                .fillMaxSize()
                .padding(padding)
        ) {
            composable("home") {
                if (isAgency) {
                    AgencyDashboardScreen()
                } else {
                    TouristDashboardScreen()
                }
            }

            if (isAgency) {
                composable("manage_experiences") {
                    ManageExperiencesScreen(
                        onAddExperience = { navController.navigate("create_experience") },
                        onEditExperience = { experienceId ->
                            navController.navigate("edit_experience/$experienceId")
                        }
                    )
                }
                composable("bookings") {
                    Text(
                        "Pantalla de Reservas",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                composable("queries") {
                    Text(
                        "Pantalla de Consultas",
                        modifier = Modifier.padding(16.dp)
                    )
                }
                composable("create_experience") {
                    CreateExperienceScreen(
                        onExperienceCreated = {
                            navController.popBackStack()
                        }
                    )
                }
                composable("edit_experience/{experienceId}") { backStackEntry ->
                    val experienceId = backStackEntry.arguments?.getString("experienceId")
                    Text("Aquí irá el formulario para EDITAR la experiencia con ID: $experienceId")
                }
            } else {
                composable("favorites") { FavoritesScreen() }
                composable("itineraries") {
                    Text(
                        "Pantalla de Itinerarios",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            composable("profile") {
                ProfileScreen(
                    isAgency = isAgency,
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileScreen(
    isAgency: Boolean,
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color(0xFF318C8B)
                )
            }
            Text(
                text = "Mi Perfil",
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(Modifier.height(24.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = Color(0xFF318C8B)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    text = if (isAgency) "Perfil de Agencia" else "Perfil de Turista",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Aquí irá la información del usuario",
                    color = Color(0xFF58636A),
                    fontSize = 14.sp
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text("Configuración y opciones adicionales...", color = Color.Gray)
    }
}

@Composable
fun Home() {
    val authViewModel: AuthViewModel = remember { PresentationModule.getAuthViewModel() }
    val uiState by authViewModel.uiState.collectAsState()

    if (uiState.currentUser == null) {
        AuthScreen(
            authViewModel = authViewModel,
            onLoginSuccess = {  }
        )
    } else {
        MainAppContent(authViewModel = authViewModel)
    }
}