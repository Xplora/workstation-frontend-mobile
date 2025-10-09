package pe.edu.upc.tripmatch.presentation.view

import androidx.compose.runtime.*
import pe.edu.upc.tripmatch.presentation.viewmodel.AuthViewModel

sealed class AuthFlowScreen {
    object Login : AuthFlowScreen()
    object Register : AuthFlowScreen()
    object MainApp : AuthFlowScreen()
}

@Composable
fun AuthScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val uiState by authViewModel.uiState.collectAsState()
    val currentUser = uiState.currentUser

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

        AuthFlowScreen.MainApp -> {
            if (currentUser == null) currentFlowScreen = AuthFlowScreen.Login
        }
    }
}
