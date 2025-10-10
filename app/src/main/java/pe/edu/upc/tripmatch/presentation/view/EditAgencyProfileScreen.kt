package pe.edu.upc.tripmatch.presentation.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.tripmatch.presentation.di.PresentationModule
import pe.edu.upc.tripmatch.presentation.viewmodel.EditAgencyProfileViewModel
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
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    TextButton(onClick = onCancel) {
                        Text("Cancel", color = TurquoiseDark)
                    }
                },
                actions = {
                    TextButton(onClick = { viewModel.saveChanges() }, enabled = !uiState.isSaving) {
                        Text("Save", color = TurquoiseDark, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (uiState.isSaving) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                uiState.errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, fontSize = 14.sp)
                }

                ProfileTextField(
                    value = uiState.agencyName,
                    onValueChange = { viewModel.onFieldChange("name", it) },
                    label = "Agency Name"
                )
                ProfileTextField(
                    value = uiState.ruc,
                    onValueChange = { viewModel.onFieldChange("ruc", it) },
                    label = "RUC"
                )
                ProfileTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.onFieldChange("description", it) },
                    label = "Description",
                    singleLine = false,
                    modifier = Modifier.height(120.dp)
                )
                ProfileTextField(
                    value = uiState.avatarUrl,
                    onValueChange = { viewModel.onFieldChange("avatar", it) },
                    label = "Avatar URL"
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text("Contact Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                ProfileTextField(
                    value = uiState.contactEmail,
                    onValueChange = { viewModel.onFieldChange("email", it) },
                    label = "Contact Email"
                )
                ProfileTextField(
                    value = uiState.contactPhone,
                    onValueChange = { viewModel.onFieldChange("phone", it) },
                    label = "Contact Phone"
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text("Social Media", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                ProfileTextField(
                    value = uiState.facebookUrl,
                    onValueChange = { viewModel.onFieldChange("facebook", it) },
                    label = "Facebook URL"
                )
                ProfileTextField(
                    value = uiState.instagramUrl,
                    onValueChange = { viewModel.onFieldChange("instagram", it) },
                    label = "Instagram URL"
                )
                ProfileTextField(
                    value = uiState.whatsappUrl,
                    onValueChange = { viewModel.onFieldChange("whatsapp", it) },
                    label = "WhatsApp URL"
                )
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
        singleLine = singleLine
    )
}