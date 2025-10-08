package pe.edu.upc.tripmatch.presentation.view

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import pe.edu.upc.tripmatch.presentation.viewmodel.AuthEvent
import pe.edu.upc.tripmatch.presentation.viewmodel.AuthViewModel

/**
 * Pantalla de Inicio de Sesión (Sign In).
 * Usa componentes definidos en AuthComponents.kt (PrimaryColor, AuthInputField, PrimaryButton).
 */
@Composable
fun SignInScreen(
    viewModel: AuthViewModel,
    onNavigateTo: (AuthFlowScreen) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf(uiState.email) }
    var password by remember { mutableStateOf(uiState.password) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            if (event == AuthEvent.SignInSuccess) {
                onNavigateTo(AuthFlowScreen.MainApp)
                viewModel.clearEvent()
            }
        }
    }

    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage != null) {
            Toast.makeText(context, uiState.successMessage, Toast.LENGTH_LONG).show()
            viewModel.clearSuccessMessage()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "TripMatch",
            fontSize = 42.sp,
            fontWeight = FontWeight.ExtraBold,
            color = PrimaryColor,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        Text(
            text = "Inicio de Sesión",
            fontSize = 32.sp,
            fontWeight = FontWeight.Black,
            color = SecondaryColor,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
        Text(
            text = "¡Bienvenido de vuelta! Accede a tu cuenta.",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )


        if (uiState.errorMessage != null) {
            Text(uiState.errorMessage!!, color = Color.Red, modifier = Modifier.padding(vertical = 12.dp))
        }

        AuthInputField(email, { email = it }, "Correo Electrónico", Icons.Default.Email, KeyboardType.Email)
        AuthInputField(password, { password = it }, "Contraseña", Icons.Default.Lock, KeyboardType.Password, isPassword = true)

        PrimaryButton(
            text = "INICIAR SESIÓN",
            onClick = { viewModel.onSignIn(email, password) },
            enabled = email.isNotBlank() && password.isNotBlank(),
            isLoading = uiState.isLoading
        )

        Row(modifier = Modifier.padding(top = 32.dp)) {
            Text("¿Eres nuevo aquí? ", color = Color.Gray)
            Text(
                "Crea una cuenta",
                color = PrimaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = { onNavigateTo(AuthFlowScreen.Register) })
            )
        }
    }
}
