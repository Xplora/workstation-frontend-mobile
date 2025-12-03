@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package pe.edu.upc.tripmatch.presentation.navigation
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.ViewModelStoreOwner
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import pe.edu.upc.tripmatch.presentation.viewmodel.AgencyProfileViewModel

data class NavigationItem(val title: String, val route: String)

@Composable
private fun AppBarCompact(
    onOpenProfile: () -> Unit,
    onLogout: () -> Unit,
    avatarUrl: String?
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
                    if (!avatarUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Perfil",
                            tint = Color(0xFF58636A),
                            modifier = Modifier.size(32.dp)
                        )
                    }
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
    val profileViewModel: AgencyProfileViewModel = viewModel(
        factory = PresentationModule.getAgencyProfileViewModelFactory()
    )
    val profileUiState by profileViewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        profileViewModel.loadProfileData()
    }
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
            if (currentRoute != "profile" && currentRoute != "edit_agency_profile") {
                Log.d("HomeDebug", "URL para AppBar: ${profileUiState.agencyProfile?.avatarUrl}")

                AppBarCompact(
                    onOpenProfile = {
                        navController.navigate("profile") {
                            launchSingleTop = true
                        }
                    },
                    onLogout = { authViewModel.logout() },
                    avatarUrl = profileUiState.agencyProfile?.avatarUrl
                )
            }
        },
        bottomBar = {
            if (currentRoute != "profile" && currentRoute != "edit_agency_profile") {
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
                    val bookingsViewModel = remember { PresentationModule.getBookingsViewModel() }
                    BookingsScreen(viewModel = bookingsViewModel)
                }
                composable("queries") {
                    QueriesScreen()
                }
                composable("create_experience") {
                    CreateExperienceScreen(
                        onExperienceCreated = {
                            navController.navigate("success_experience") {
                                popUpTo("manage_experiences") { inclusive = false }
                            }
                        },
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                composable("success_experience") {
                    SuccessScreen(
                        onContinueClick = {
                            navController.navigate("manage_experiences") {
                                popUpTo("manage_experiences") { inclusive = true }
                            }
                        }
                    )
                }
                composable("edit_experience/{experienceId}") { backStackEntry ->
                    val experienceId = backStackEntry.arguments?.getString("experienceId")?.toIntOrNull()
                    val manageViewModel = PresentationModule.getManageExperiencesViewModel()

                    val uiState by manageViewModel.uiState.collectAsState()

                    val experienceToEdit = uiState.experiences.find { it.id == experienceId }

                    experienceToEdit?.let {
                        CreateExperienceScreen(
                            experienceToEdit = it,
                            onExperienceCreated = { navController.popBackStack() },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
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
                val profileViewModel: AgencyProfileViewModel = viewModel(
                    factory = PresentationModule.getAgencyProfileViewModelFactory()
                )
                LaunchedEffect(navController.currentBackStackEntry) {
                    profileViewModel.loadProfileData()
                }

                if (isAgency) {
                    AgencyProfileScreen(
                        viewModel = profileViewModel,
                        onNavigateBack = {
                            navController.popBackStack()
                        },
                        onEditProfile = {
                            navController.navigate("edit_agency_profile")
                        }
                    )
                } else {
                    Text("Tourist Profile Screen")
                }
            }

            composable("edit_agency_profile") {
                EditAgencyProfileScreen(
                    onSaveSuccess = {
                        navController.popBackStack()
                    },
                    onCancel = {
                        navController.popBackStack()
                    }
                )
            }
        }
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