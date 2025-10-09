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
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
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


private val TealDark = Color(0xFF318C8B)
private val TealLight = Color(0xFFD9F2EF)
private val TextPrimary = Color(0xFF111111)
private val TextSecondary = Color(0xFF58636A)

@Composable
fun SignInScreen(
    viewModel: AuthViewModel,
    onNavigateTo: (AuthFlowScreen) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
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
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearSuccessMessage()
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
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Image(
                    painter = painterResource(id = R.drawable.ic_tripmatch_logo),
                    contentDescription = "TripMatch",
                    modifier = Modifier
                        .size(72.dp)
                        .padding(bottom = 8.dp),
                    contentScale = ContentScale.Fit
                )
                Text(
                    text = "TripMatch",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TealDark
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Log in to your account",
                    fontSize = 16.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(20.dp))


            LoginSignUpToggle(
                selected = 0,
                onSelectLogin = { /* ya estás en login */ },
                onSelectSignUp = { onNavigateTo(AuthFlowScreen.Register) }
            )

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(Modifier.padding(20.dp)) {

                    uiState.errorMessage?.let {
                        Text(
                            it,
                            color = Color(0xFFD11A2A),
                            modifier = Modifier.padding(bottom = 12.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }


                    AuthInputField(
                        value = uiState.email,
                        onValueChange = { viewModel.setEmail(it) },
                        label = "Email",
                        icon = Icons.Default.Email,
                        keyboardType = KeyboardType.Email
                    )

                    AuthInputField(
                        value = uiState.password,
                        onValueChange = { viewModel.setPassword(it) },
                        label = "Password",
                        icon = Icons.Default.Lock,
                        keyboardType = KeyboardType.Password,
                        isPassword = true
                    )

                    Spacer(Modifier.height(18.dp))

                    Button(
                        onClick = { viewModel.onSignIn() },
                        enabled = uiState.email.isNotBlank() && uiState.password.isNotBlank() && !uiState.isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TealDark,
                            contentColor = Color.White,
                            disabledContainerColor = TealDark.copy(alpha = 0.4f),
                            disabledContentColor = Color.White.copy(alpha = 0.9f)
                        )
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp),
                                color = Color.White
                            )
                        } else {
                            Text("Log In", fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Are you new here? ", color = TextSecondary)
                        Text(
                            "Create an account",
                            color = TealDark,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onNavigateTo(AuthFlowScreen.Register) }
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
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
            .height(40.dp)
            .widthIn(min = 240.dp)
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
                fontWeight = FontWeight.SemiBold
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
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
