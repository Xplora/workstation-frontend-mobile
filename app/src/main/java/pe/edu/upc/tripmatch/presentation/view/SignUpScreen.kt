package pe.edu.upc.tripmatch.presentation.view

import pe.edu.upc.tripmatch.R
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest
import pe.edu.upc.tripmatch.presentation.viewmodel.AuthEvent
import pe.edu.upc.tripmatch.presentation.viewmodel.AuthViewModel

private val TealDark = PrimaryColor
private val TealLight = Color(0xFFD9F2EF)
private val TextSecondary = Color(0xFF58636A)

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onNavigateTo: (AuthFlowScreen) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()



    val context = LocalContext.current
    val canSubmit = remember(uiState) {
        uiState.name.isNotBlank() &&
                uiState.email.isNotBlank() &&
                uiState.number.isNotBlank() &&
                uiState.password.length >= 6 &&
                uiState.password == uiState.confirmPassword
    }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest { event ->
            if (event == AuthEvent.SignUpSuccess) {
                Toast.makeText(context, "Cuenta creada con éxito. Inicia sesión.", Toast.LENGTH_LONG).show()
                onNavigateTo(AuthFlowScreen.Login)
                viewModel.clearEvent()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.ic_tripmatch_logo),
                    contentDescription = "TripMatch",
                    modifier = Modifier
                        .size(56.dp)
                        .padding(bottom = 6.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = "TripMatch",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TealDark
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Crea tu cuenta",
                    fontSize = 15.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(14.dp))

            LoginSignUpToggle(
                selected = 1,
                onSelectLogin = { onNavigateTo(AuthFlowScreen.Login) },
                onSelectSignUp = { /* login */ }
            )

            Spacer(Modifier.height(14.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(16.dp)) {

                    uiState.errorMessage?.let {
                        Text(
                            it,
                            color = Color(0xFFD11A2A),
                            modifier = Modifier.padding(bottom = 8.dp),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }

                    AuthInputField(
                        value = uiState.name,
                        onValueChange = { viewModel.setName(it) },
                        label = "Nombre completo",
                        icon = Icons.Default.Person
                    )

                    AuthInputField(
                        value = uiState.email,
                        onValueChange = { viewModel.setEmail(it) },
                        label = "Correo electrónico",
                        icon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email
                    )

                    AuthInputField(
                        value = uiState.number,
                        onValueChange = { viewModel.setNumber(it) },
                        label = "Teléfono",
                        icon = Icons.Default.Phone,
                        keyboardType = KeyboardType.Phone
                    )

                    AuthInputField(
                        value = uiState.password,
                        onValueChange = { viewModel.setPassword(it) },
                        label = "Contraseña (min. 6)",
                        icon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true
                    )

                    AuthInputField(
                        value = uiState.confirmPassword,
                        onValueChange = { viewModel.setConfirmPassword(it) },
                        label = "Confirmar contraseña",
                        icon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true
                    )

                    Spacer(Modifier.height(8.dp))
                    Text("Tipo de cuenta", fontWeight = FontWeight.SemiBold, color = SecondaryColor, fontSize = 14.sp)
                    Spacer(Modifier.height(4.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = !uiState.isAgency,
                                onClick = { viewModel.setIsAgency(false) },
                                colors = RadioButtonDefaults.colors(selectedColor = TealDark)
                            )
                            Text("Turista", modifier = Modifier.clickable { viewModel.setIsAgency(false) }, fontSize = 14.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = uiState.isAgency,
                                onClick = { viewModel.setIsAgency(true) },
                                colors = RadioButtonDefaults.colors(selectedColor = TealDark)
                            )
                            Text("Agencia", modifier = Modifier.clickable { viewModel.setIsAgency(true) }, fontSize = 14.sp)
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    PrimaryButton(
                        text = "CREAR CUENTA",
                        onClick = { viewModel.onSignUp() },
                        enabled = canSubmit && !uiState.isLoading,
                        isLoading = uiState.isLoading
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("¿Ya tienes cuenta? ", color = TextSecondary, fontSize = 14.sp)
                        Text(
                            "Inicia sesión",
                            color = TealDark,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateTo(AuthFlowScreen.Login) },
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
        }
    }
}

@Composable
private fun LoginSignUpToggle(
    selected: Int,
    onSelectLogin: () -> Unit,
    onSelectSignUp: () -> Unit
) {
    val containerShape = CircleShape
    Row(
        modifier = Modifier
            .clip(containerShape)
            .background(TealLight)
            .padding(4.dp)
            .height(36.dp)
            .widthIn(min = 220.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(containerShape)
                .background(if (selected == 0) TealDark else Color.Transparent)
                .clickable { onSelectLogin() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Log In",
                color = if (selected == 0) Color.White else TealDark,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .clip(containerShape)
                .background(if (selected == 1) TealDark else Color.Transparent)
                .clickable { onSelectSignUp() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Sign Up",
                color = if (selected == 1) Color.White else TealDark,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}
