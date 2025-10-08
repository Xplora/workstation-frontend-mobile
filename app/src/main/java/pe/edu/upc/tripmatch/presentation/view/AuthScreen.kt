package pe.edu.upc.tripmatch.presentation.view

import androidx.compose.runtime.*
import pe.edu.upc.tripmatch.presentation.di.PresentationModule
import pe.edu.upc.tripmatch.presentation.viewmodel.AuthViewModel

/**
 * Define los estados de navegación dentro del flujo de autenticación.
 * Esta clase sellada es esencial para que AuthScreen pueda alternar entre vistas.
 */
sealed class AuthFlowScreen {
    object Login : AuthFlowScreen()
    object Register : AuthFlowScreen()
    object MainApp : AuthFlowScreen()
}

/**
 * Componente raíz para el flujo de autenticación (Login/Register).
 * Maneja el estado de la sesión y la navegación entre las pantallas.
 */
@Composable
fun AuthScreen(
    authViewModel: AuthViewModel = PresentationModule.getAuthViewModel(),
    onLoginSuccess: () -> Unit
) {

    val currentUser = authViewModel.uiState.collectAsState().value.currentUser


    var currentFlowScreen by remember {
        mutableStateOf(if (currentUser != null) AuthFlowScreen.MainApp else AuthFlowScreen.Login)
    }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            currentFlowScreen = AuthFlowScreen.MainApp
            onLoginSuccess()
        }
    }

    when (currentFlowScreen) {

        AuthFlowScreen.Login -> SignInScreen(
            viewModel = authViewModel,
            onNavigateTo = { currentFlowScreen = it }
        )

        AuthFlowScreen.Register -> SignUpScreen(
            viewModel = authViewModel,
            onNavigateTo = { currentFlowScreen = it }
        )

        AuthFlowScreen.MainApp -> if (currentUser != null) {
            onLoginSuccess()
        } else {
            currentFlowScreen = AuthFlowScreen.Login
        }
    }
}
