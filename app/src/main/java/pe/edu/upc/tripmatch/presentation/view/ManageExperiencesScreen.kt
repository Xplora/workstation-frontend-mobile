package pe.edu.upc.tripmatch.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.tripmatch.presentation.di.PresentationModule
import pe.edu.upc.tripmatch.presentation.viewmodel.ManageExperiencesViewModel

@Composable
fun ManageExperiencesScreen(
    viewModel: ManageExperiencesViewModel = PresentationModule.getManageExperiencesViewModel(),
    onAddExperience: () -> Unit,
    onEditExperience: (Int) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAgencyExperiences()
    }

    uiState.successMessage?.let { message ->
        val snackbarHostState = remember { SnackbarHostState() }
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearMessages()
        }
    }


    if (uiState.showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteDialog() },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que quieres eliminar la experiencia '${uiState.experienceToDelete?.title}'?") },
            confirmButton = {
                Button(
                    onClick = { viewModel.onConfirmDelete() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Eliminar") }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.dismissDeleteDialog() }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        containerColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExperience,
                containerColor = Color(0xFF67B7B6)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Experiencia", tint = Color.White)
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = remember { SnackbarHostState() })
        }

    ) { padding ->

        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        if (uiState.error != null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${uiState.error!!}", color = Color.Red, modifier = Modifier.padding(24.dp))
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column {
                    Text(
                        "Gestión de Experiencias",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 40.sp
                    )
                    Text(
                        "Tus Experiencias Publicadas",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }

            if (uiState.experiences.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Aún no tienes experiencias publicadas. ¡Añade una!")
                    }
                }
            } else {
                items(uiState.experiences, key = { it.id }) { experience ->
                    ExperienceCard(
                        experience = experience,
                        onEdit = { onEditExperience(experience.id) },
                        onDelete = { viewModel.openDeleteDialog(experience) }
                    )
                }
            }
        }
    }
}