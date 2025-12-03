package pe.edu.upc.tripmatch.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.DeleteForever
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.tripmatch.presentation.di.PresentationModule
import pe.edu.upc.tripmatch.presentation.viewmodel.ManageExperiencesViewModel

private val DangerColor = Color(0xFFEF4444)
private val TextDark = Color(0xFF1A202C)
private val TextGrey = Color(0xFF718096)
private val BackgroundLight = Color(0xFFF8F9FA)

@OptIn(ExperimentalMaterial3Api::class)
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
            icon = {
                Icon(
                    imageVector = Icons.Outlined.DeleteForever,
                    contentDescription = null,
                    tint = DangerColor,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "Eliminar Experiencia",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = TextDark,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "¿Estás seguro de que deseas eliminar permanentemente esta experiencia?",
                        textAlign = TextAlign.Center,
                        color = TextGrey,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = BackgroundLight,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = uiState.experienceToDelete?.title ?: "Experiencia",
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            color = TextDark,
                            modifier = Modifier.padding(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Esta acción no se puede deshacer.",
                        style = MaterialTheme.typography.bodySmall,
                        color = DangerColor.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onConfirmDelete() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DangerColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text("Sí, eliminar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.dismissDeleteDialog() },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TextGrey
                    ),
                    border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar")
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp),
            tonalElevation = 8.dp
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