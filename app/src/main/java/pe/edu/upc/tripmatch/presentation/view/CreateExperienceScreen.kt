package pe.edu.upc.tripmatch.presentation.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.tripmatch.presentation.di.PresentationModule
import pe.edu.upc.tripmatch.presentation.viewmodel.CreateExperienceViewModel

private val TurquoiseDark = Color(0xFF67B7B6)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExperienceScreen(
    viewModel: CreateExperienceViewModel = PresentationModule.getCreateExperienceViewModel(),
    onExperienceCreated: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var imageUrlInput by remember { mutableStateOf("") }
    var newIncludeInput by remember { mutableStateOf("") }


    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        val message = uiState.successMessage ?: uiState.errorMessage
        if (message != null) {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearMessages()
            if (uiState.successMessage != null) {
                onExperienceCreated()
            }
        }
    }

    Scaffold(
        topBar = {

            TopAppBar(

                title = { Text("Nueva Experiencia") },

                navigationIcon = {

                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )

                    }

                },

                colors = TopAppBarDefaults.topAppBarColors(

                    containerColor = Color.White,

                    titleContentColor = Color.Black,

                    navigationIconContentColor = Color.Black

                )

            )

        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color.White
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = TurquoiseDark)
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Crear Nueva Experiencia", fontSize = 28.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Ingresa los detalles de tu nueva oferta.", color = Color.Gray)
            }


            item {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = { viewModel.updateField("title", it) },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = uiState.errorMessage != null && uiState.title.isBlank()
                )
            }
            item {
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = { viewModel.updateField("description", it) },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    singleLine = false
                )
            }
            item {
                OutlinedTextField(
                    value = uiState.location,
                    onValueChange = { viewModel.updateField("location", it) },
                    label = { Text("Ubicación") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.duration,
                        onValueChange = { viewModel.updateField("duration", it) },
                        label = { Text("Duración (horas)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = uiState.price,
                        onValueChange = { viewModel.updateField("price", it) },
                        label = { Text("Precio (S/)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        isError = uiState.errorMessage != null && uiState.price.toDoubleOrNull() == null
                    )
                }
            }


            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    var expandedCategory by remember { mutableStateOf(false) }
                    Box(Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = viewModel.categoryOptions.find { it.first == uiState.categoryId }?.second ?: "Seleccionar Categoría",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Categoría") },
                            trailingIcon = { Icon(Icons.Filled.Add, contentDescription = "Seleccionar", Modifier.clickable { expandedCategory = true }) }
                        )
                        DropdownMenu(
                            expanded = expandedCategory,
                            onDismissRequest = { expandedCategory = false }
                        ) {
                            viewModel.categoryOptions.forEach { (id, name) ->
                                DropdownMenuItem(
                                    text = { Text(name) },
                                    onClick = {
                                        viewModel.updateField("categoryId", id)
                                        expandedCategory = false
                                    }
                                )
                            }
                        }
                    }

                    var expandedFreq by remember { mutableStateOf(false) }
                    Box(Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = uiState.frequencies.ifBlank { "Seleccionar Frecuencia" },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Frecuencia") },
                            trailingIcon = { Icon(Icons.Filled.Add, contentDescription = "Seleccionar", Modifier.clickable { expandedFreq = true }) }
                        )
                        DropdownMenu(
                            expanded = expandedFreq,
                            onDismissRequest = { expandedFreq = false }
                        ) {
                            viewModel.frequencyOptions.forEach { freq ->
                                DropdownMenuItem(
                                    text = { Text(freq) },
                                    onClick = {
                                        viewModel.updateField("frequencies", freq)
                                        expandedFreq = false
                                    }
                                )
                            }
                        }
                    }
                }
            }


            item {
                Text("Horarios Disponibles", style = MaterialTheme.typography.titleMedium)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    viewModel.scheduleOptions.forEach { time ->
                        val isSelected = uiState.schedules.contains(time)
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.toggleSchedule(time) },
                            label = { Text(time) },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = Color.LightGray.copy(alpha = 0.3f),
                                labelColor = Color.DarkGray,
                                iconColor = Color.DarkGray,
                                selectedContainerColor = TurquoiseDark,
                                selectedLabelColor = Color.White,
                                selectedLeadingIconColor = Color.White
                            )
                        )
                    }
                }
            }

            item {
                Text("¿Qué incluye? (Máx. 3)", style = MaterialTheme.typography.titleMedium)
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newIncludeInput,
                        onValueChange = { newIncludeInput = it },
                        label = { Text("Detalle de inclusión") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        enabled = uiState.includes.size < 3
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            viewModel.addInclude(newIncludeInput)
                            newIncludeInput = ""
                        },
                        enabled = newIncludeInput.isNotBlank() && uiState.includes.size < 3,
                        colors = ButtonDefaults.buttonColors(containerColor = TurquoiseDark)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir")
                    }
                }
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    uiState.includes.forEachIndexed { index, item ->
                        InputChip(
                            selected = true,
                            onClick = { /* No-op */ },
                            label = { Text(item) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Eliminar",
                                    modifier = Modifier.size(18.dp).clickable { viewModel.removeInclude(index) }
                                )
                            }
                        )
                    }
                }
            }


            item {
                Text("Imágenes (URLs)", style = MaterialTheme.typography.titleMedium)
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = imageUrlInput,
                        onValueChange = { imageUrlInput = it },
                        label = { Text("URL de Imagen") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            viewModel.addImage(imageUrlInput)
                            imageUrlInput = ""
                        },
                        enabled = imageUrlInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = TurquoiseDark)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir")
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("Imágenes añadidas: ${uiState.images.size}", style = MaterialTheme.typography.bodySmall)
            }


            item {
                Button(
                    onClick = { viewModel.createExperience(onExperienceCreated) },
                    modifier = Modifier.fillMaxWidth().height(50.dp).padding(vertical = 4.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TurquoiseDark),
                    enabled = !uiState.isLoading
                ) {
                    Text("Guardar Experiencia", fontSize = 18.sp)
                }
            }
        }
    }
}

