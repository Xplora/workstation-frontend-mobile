package pe.edu.upc.tripmatch.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import pe.edu.upc.tripmatch.presentation.viewmodel.AuthEvent
import pe.edu.upc.tripmatch.presentation.viewmodel.AuthViewModel

/**
 * Pantalla de Registro (Sign Up).
 * Usa componentes definidos en AuthComponents.kt (PrimaryColor, AuthInputField, PrimaryButton).
 */
@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onNavigateTo: (AuthFlowScreen) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var name by remember { mutableStateOf(uiState.name) }
    var email by remember { mutableStateOf(uiState.email) }
    var number by remember { mutableStateOf(uiState.number) }
    var password by remember { mutableStateOf(uiState.password) }
    var confirmPassword by remember { mutableStateOf(uiState.confirmPassword) }
    var isAgency by remember { mutableStateOf(uiState.isAgency) }


    val canSubmit = name.isNotBlank() && email.isNotBlank() && number.isNotBlank() && password.length >= 6 && password == confirmPassword

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            if (event == AuthEvent.SignUpSuccess) {
                onNavigateTo(AuthFlowScreen.Login)
                viewModel.clearEvent()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onNavigateTo(AuthFlowScreen.Login) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver al Login")
            }
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Crear Cuenta",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = SecondaryColor,
            )
        }

        Text(
            text = "Únete a la plataforma TripMatch.",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )

        if (uiState.errorMessage != null) {
            Text(uiState.errorMessage!!, color = Color.Red, modifier = Modifier.padding(vertical = 8.dp))
        }

        AuthInputField(name, { name = it }, "Nombre Completo (Agencia/Persona)", Icons.Default.Person)
        AuthInputField(email, { email = it }, "Correo Electrónico", Icons.Default.Email, KeyboardType.Email)
        AuthInputField(number, { number = it }, "Número de Teléfono", Icons.Default.Phone, KeyboardType.Phone)
        AuthInputField(password, { password = it }, "Contraseña (min. 6)", Icons.Default.Lock, KeyboardType.Password, isPassword = true)
        AuthInputField(confirmPassword, { confirmPassword = it }, "Confirmar Contraseña", Icons.Default.Lock, KeyboardType.Password, isPassword = true)

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Tipo de Cuenta:", fontWeight = FontWeight.SemiBold, color = SecondaryColor)
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = !isAgency,
                    onClick = { isAgency = false },
                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryColor)
                )
                Text("Turista", modifier = Modifier.clickable { isAgency = false }, fontSize = 14.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(
                    selected = isAgency,
                    onClick = { isAgency = true },
                    colors = RadioButtonDefaults.colors(selectedColor = PrimaryColor)
                )
                Text("Agencia", modifier = Modifier.clickable { isAgency = true }, fontSize = 14.sp)
            }
        }

        PrimaryButton(
            text = "REGISTRAR CUENTA",
            onClick = {
                val signUpState = viewModel.uiState.value.copy(
                    name = name,
                    email = email,
                    number = number,
                    password = password,
                    confirmPassword = confirmPassword,
                    isAgency = isAgency
                )
                viewModel.onSignUp(signUpState)
            },
            enabled = canSubmit,
            isLoading = uiState.isLoading
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(modifier = Modifier.padding(top = 16.dp)) {
            Text("¿Ya tienes una cuenta? ", color = Color.Gray)
            Text(
                "Inicia Sesión",
                color = PrimaryColor,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = { onNavigateTo(AuthFlowScreen.Login) })
            )
        }
    }
}
