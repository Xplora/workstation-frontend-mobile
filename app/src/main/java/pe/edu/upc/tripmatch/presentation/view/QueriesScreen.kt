package pe.edu.upc.tripmatch.presentation.view

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.tripmatch.domain.model.Query
import pe.edu.upc.tripmatch.presentation.di.PresentationModule
import pe.edu.upc.tripmatch.presentation.viewmodel.QueriesViewModel

private val Teal = Color(0xFF318C8B)
private val BackgroundGrey = Color(0xFFF5F5F5)
private val TextSecondary = Color(0xFF58636A)
private val BorderGrey = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QueriesScreen(
    viewModel: QueriesViewModel = PresentationModule.getQueriesViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()
    val TAG = "QueriesScreen"

    var searchText by remember { mutableStateOf("") }
    var filterState by remember { mutableStateOf("Sin responder") }

    val filteredQueries = uiState.inquiries.filter { query ->
        val stateMatch = when (filterState) {
            "Sin responder" -> !query.isAnswered
            "Respondidas" -> query.isAnswered
            else -> true
        }
        val searchMatch = searchText.isEmpty() ||
                (query.travelerName?.contains(searchText, ignoreCase = true) == true) ||
                (query.experienceTitle?.contains(searchText, ignoreCase = true) == true)

        stateMatch && searchMatch
    }


    val snackbarHostState = remember { SnackbarHostState() }

    uiState.successMessage?.let { message ->
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message = message)
            viewModel.clearMessages()
        }
    }

    uiState.error?.let { error ->
        LaunchedEffect(error) {
            snackbarHostState.showSnackbar(message = error, actionLabel = "Cerrar")
            viewModel.clearMessages()
        }
    }

    if (uiState.showResponseDialog && uiState.selectedQuery != null) {
        ResponseDialog(
            query = uiState.selectedQuery!!,
            currentAnswer = uiState.responseText,
            onAnswerChange = viewModel::updateResponseText,
            onConfirm = viewModel::sendResponse,
            onDismiss = viewModel::dismissResponseDialog,
            isSending = uiState.isSendingResponse
        )
    }

    Scaffold(
        containerColor = BackgroundGrey,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()

                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Bandeja de Consultas",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 28.sp,
                color = Color.Black
            )
            Text(
                text = "Gestiona todas las preguntas que recibes de los viajeros.",
                color = TextSecondary,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Buscar por viajero o por experiencia") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Buscar") },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Teal,
                    unfocusedBorderColor = BorderGrey,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FilterChip(
                    label = "Sin responder",
                    isSelected = filterState == "Sin responder",
                    onClick = { filterState = "Sin responder" },
                    selectedColor = Teal,
                    unselectedColor = BorderGrey,
                )
                Spacer(Modifier.width(8.dp))
                FilterChip(
                    label = "Respondidas",
                    isSelected = filterState == "Respondidas",
                    onClick = { filterState = "Respondidas" },
                    selectedColor = Teal,
                    unselectedColor = BorderGrey,
                )
            }

            Spacer(Modifier.height(16.dp))

            when {
                uiState.isLoading && uiState.inquiries.isEmpty() -> {
                    Log.d(TAG, "Mostrando estado: Carga Inicial")
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Teal)
                    }
                }
                uiState.error != null && uiState.inquiries.isEmpty() -> {
                    Log.e(TAG, "Mostrando estado: Error de carga inicial - ${uiState.error}")
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${uiState.error}", color = Color.Red, modifier = Modifier.padding(24.dp))
                    }
                }
                else -> {
                    if (filteredQueries.isEmpty()) {
                        Log.d(TAG, "Mostrando estado: Lista vacía / Sin resultados de filtro")
                        Box(modifier = Modifier.fillMaxSize().padding(top = 32.dp), contentAlignment = Alignment.TopCenter) {
                            Text(
                                text = if (searchText.isNotEmpty()) "No hay resultados para la búsqueda."
                                else if (uiState.inquiries.isEmpty()) "No tienes ninguna consulta."
                                else "No hay consultas en el estado '${filterState}'.",
                                color = TextSecondary
                            )
                        }
                    } else {
                        Log.d(TAG, "Mostrando estado: Lista con ${filteredQueries.size} items")
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredQueries, key = { it.id }) { query ->
                                QueryItemCard(
                                    query = query,
                                    onActionClick = { viewModel.openResponseDialog(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterChip(label: String, isSelected: Boolean, onClick: () -> Unit, selectedColor: Color, unselectedColor: Color) {
    val containerColor = if (isSelected) selectedColor else unselectedColor
    val contentColor = if (isSelected) Color.White else TextSecondary

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = if (isSelected) null else BorderStroke(1.dp, TextSecondary.copy(alpha = 0.5f))
    ) {
        Text(
            text = label,
            color = contentColor,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun ResponseDialog(
    query: Query,
    currentAnswer: String,
    onAnswerChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    isSending: Boolean
) {
    val isReadOnly = query.isAnswered && query.answer == currentAnswer

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (query.isAnswered) "Ver/Editar Respuesta" else "Responder Consulta")
        },
        text = {
            Column {
                Text(
                    "Pregunta de ${query.travelerName.orEmpty()} en ${query.experienceTitle.orEmpty()}:",
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    query.question,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
                    fontStyle = MaterialTheme.typography.bodyMedium.fontStyle
                )


                OutlinedTextField(
                    value = currentAnswer,
                    onValueChange = onAnswerChange,
                    label = { Text(if (isReadOnly) "Respuesta Enviada" else "Tu Respuesta") },
                    readOnly = false,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 100.dp),
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Teal,
                        unfocusedBorderColor = BorderGrey,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color(0xFFEFEFEF)
                    )
                )
                if (query.isAnswered && query.answeredAt != null) {
                    Text(
                        text = "Respondida el: ${formatDisplayDate(query.answeredAt)}",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    Log.d("QueriesScreen", "Click en botón CONFIRMAR del diálogo")
                    onConfirm()
                },
                enabled = currentAnswer.isNotBlank() && !isSending,
                colors = ButtonDefaults.buttonColors(containerColor = Teal)
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(if (query.isAnswered) "Actualizar" else "Enviar")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}
private fun formatDisplayDate(dateTimeString: String?): String {
    if (dateTimeString.isNullOrBlank()) return "Fecha desconocida"

    return try {
        val instant = java.time.Instant.parse(dateTimeString)
        val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
            .withZone(java.time.ZoneId.systemDefault())
        formatter.format(instant)
    } catch (e: java.time.format.DateTimeParseException) {
        dateTimeString.take(10)
    } catch (e: Exception) {
        dateTimeString.take(10)
    }
}