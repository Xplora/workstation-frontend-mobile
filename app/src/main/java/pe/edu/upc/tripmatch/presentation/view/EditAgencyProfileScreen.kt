package pe.edu.upc.tripmatch.presentation.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.tripmatch.presentation.di.PresentationModule
import pe.edu.upc.tripmatch.presentation.viewmodel.EditAgencyProfileViewModel
import pe.edu.upc.tripmatch.ui.theme.AppBackground
import pe.edu.upc.tripmatch.ui.theme.DividerColor
import pe.edu.upc.tripmatch.ui.theme.TextPrimary
import pe.edu.upc.tripmatch.ui.theme.TextSecondary
import pe.edu.upc.tripmatch.ui.theme.TurquoiseDark


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAgencyProfileScreen(
    viewModel: EditAgencyProfileViewModel = PresentationModule.getEditAgencyProfileViewModel(),
    onSaveSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            Toast.makeText(context, "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show()
            onSaveSuccess()
            viewModel.resetSaveSuccessFlag()
        }
    }
    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
    }

    Scaffold(
        containerColor = AppBackground,
        topBar = {
            TopAppBar(
                title = { Text("Editar Perfil", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Cancelar"
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.saveChanges() }, enabled = !uiState.isSaving) {
                        Text("Guardar", color = TurquoiseDark, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppBackground,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                ),
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TurquoiseDark)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (uiState.isSaving) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)),
                        color = TurquoiseDark,
                        trackColor = DividerColor
                    )
                }

                uiState.errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                }
                ProfileTextField(
                    value = uiState.agencyName,
                    onValueChange = { viewModel.onFieldChange("name", it) },
                    label = "Nombre de la Agencia"
                )
                ProfileTextField(
                    value = uiState.ruc,
                    onValueChange = { viewModel.onFieldChange("ruc", it) },
                    label = "RUC"
                )
                ProfileTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.onFieldChange("description", it) },
                    label = "Descripción",
                    singleLine = false,
                    modifier = Modifier.height(120.dp)
                )
                ProfileTextField(
                    value = uiState.avatarUrl,
                    onValueChange = { viewModel.onFieldChange("avatar", it) },
                    label = "URL del Avatar"
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Información de Contacto",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextPrimary
                )
                ProfileTextField(
                    value = uiState.contactEmail,
                    onValueChange = { viewModel.onFieldChange("email", it) },
                    label = "Email de Contacto"
                )
                ProfileTextField(
                    value = uiState.contactPhone,
                    onValueChange = { viewModel.onFieldChange("phone", it) },
                    label = "Teléfono de Contacto"
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Redes Sociales",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextPrimary
                )
                ProfileTextField(
                    value = uiState.facebookUrl,
                    onValueChange = { viewModel.onFieldChange("facebook", it) },
                    label = "URL de Facebook"
                )
                ProfileTextField(
                    value = uiState.instagramUrl,
                    onValueChange = { viewModel.onFieldChange("instagram", it) },
                    label = "URL de Instagram"
                )
                ProfileTextField(
                    value = uiState.whatsappUrl,
                    onValueChange = { viewModel.onFieldChange("whatsapp", it) },
                    label = "URL de WhatsApp (ej: 519...)"
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = AppBackground,
            unfocusedContainerColor = AppBackground,
            focusedBorderColor = TurquoiseDark,
            unfocusedBorderColor = DividerColor,
            cursorColor = TurquoiseDark,
            focusedLabelColor = TextSecondary,
            unfocusedLabelColor = TextSecondary
        )
    )
}