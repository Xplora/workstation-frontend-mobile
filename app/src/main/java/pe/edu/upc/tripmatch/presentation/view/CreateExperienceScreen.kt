package pe.edu.upc.tripmatch.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
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
import pe.edu.upc.tripmatch.domain.model.Experience
import pe.edu.upc.tripmatch.presentation.di.PresentationModule
import pe.edu.upc.tripmatch.presentation.viewmodel.CreateExperienceViewModel
import pe.edu.upc.tripmatch.ui.theme.AppBackground
import pe.edu.upc.tripmatch.ui.theme.DividerColor
import pe.edu.upc.tripmatch.ui.theme.TextPrimary
import pe.edu.upc.tripmatch.ui.theme.TextSecondary
import pe.edu.upc.tripmatch.ui.theme.TurquoiseDark
import pe.edu.upc.tripmatch.ui.theme.TurquoiseLight

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateExperienceScreen(
    viewModel: CreateExperienceViewModel = PresentationModule.getCreateExperienceViewModel(),
    onExperienceCreated: () -> Unit,
    onNavigateBack: () -> Unit,
    experienceToEdit: Experience? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var imageUrlInput by remember { mutableStateOf("") }
    var newIncludeInput by remember { mutableStateOf("") }

    val isEditing = experienceToEdit != null
    val titleText = if (isEditing) "Editar Experiencia" else "Nueva Experiencia"

    LaunchedEffect(experienceToEdit) {
        if (isEditing) {
            viewModel.loadExperienceForEditing(experienceToEdit!!)
        } else {
            viewModel.resetState()
        }
    }

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
                title = { Text(titleText, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppBackground,
                    titleContentColor = TextPrimary,
                    navigationIconContentColor = TextPrimary
                ),
                windowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = AppBackground
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        titleText,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Ingresa los detalles de tu nueva oferta.",
                        color = TextSecondary,
                        fontSize = 16.sp
                    )
                }

                item {
                    StyledOutlinedTextField(
                        value = uiState.title,
                        onValueChange = { viewModel.updateField("title", it) },
                        label = "Título",
                        isError = uiState.errorMessage != null && uiState.title.isBlank()
                    )
                }
                item {
                    StyledOutlinedTextField(
                        value = uiState.description,
                        onValueChange = { viewModel.updateField("description", it) },
                        label = "Descripción",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        singleLine = false
                    )
                }
                item {
                    StyledOutlinedTextField(
                        value = uiState.location,
                        onValueChange = { viewModel.updateField("location", it) },
                        label = "Ubicación"
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StyledOutlinedTextField(
                            value = uiState.duration,
                            onValueChange = { viewModel.updateField("duration", it) },
                            label = "Duración (horas)",
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                        StyledOutlinedTextField(
                            value = uiState.price,
                            onValueChange = { viewModel.updateField("price", it) },
                            label = "Precio (S/)",
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
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        var expandedCategory by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = expandedCategory,
                            onExpandedChange = { expandedCategory = !expandedCategory },
                            modifier = Modifier.weight(1f)
                        ) {
                            StyledOutlinedTextField(
                                value = viewModel.categoryOptions.find { it.first == uiState.categoryId }?.second
                                    ?: "",
                                onValueChange = {},
                                readOnly = true,
                                label = "Categoría",
                                placeholder = "Seleccionar",
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                                },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
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
                        ExposedDropdownMenuBox(
                            expanded = expandedFreq,
                            onExpandedChange = { expandedFreq = !expandedFreq },
                            modifier = Modifier.weight(1f)
                        ) {
                            StyledOutlinedTextField(
                                value = uiState.frequencies,
                                onValueChange = {},
                                readOnly = true,
                                label = "Frecuencia",
                                placeholder = "Seleccionar",
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFreq)
                                },
                                modifier = Modifier.menuAnchor()
                            )
                            ExposedDropdownMenu(
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
                    Text(
                        "Horarios Disponibles",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        viewModel.scheduleOptions.forEach { time ->
                            ScheduleChip(
                                time = time,
                                isSelected = uiState.schedules.contains(time),
                                onToggle = { viewModel.toggleSchedule(time) }
                            )
                        }
                    }
                }

                item {
                    Text(
                        "¿Qué incluye? (Máx. 3)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StyledOutlinedTextField(
                            value = newIncludeInput,
                            onValueChange = { newIncludeInput = it },
                            label = "Detalle de inclusión",
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
                            colors = ButtonDefaults.buttonColors(containerColor = TurquoiseDark),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Añadir")
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        uiState.includes.forEachIndexed { index, item ->
                            androidx.compose.material3.InputChip(
                                selected = true,
                                onClick = { viewModel.removeInclude(index) },
                                label = { Text(item) },
                                enabled = true,
                                colors = InputChipDefaults.inputChipColors(
                                    containerColor = TurquoiseLight,
                                    labelColor = TurquoiseDark,
                                    trailingIconColor = TurquoiseDark
                                ),
                                trailingIcon = {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Eliminar",
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }
                    }
                }

                item {
                    Text(
                        "Imágenes (URLs)",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StyledOutlinedTextField(
                            value = imageUrlInput,
                            onValueChange = { imageUrlInput = it },
                            label = "URL de Imagen",
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
                            colors = ButtonDefaults.buttonColors(containerColor = TurquoiseDark),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Añadir")
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Imágenes añadidas: ${uiState.images.size}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                item {
                    Spacer(Modifier.height(16.dp))
                    PrimaryActionButton(
                        text = if (isEditing) "Guardar Cambios" else "Guardar Experiencia",
                        isLoading = uiState.isLoading,
                        onClick = {
                            if (isEditing) {
                                viewModel.saveExperience(
                                    onSuccess = onExperienceCreated,
                                    existingExperienceId = experienceToEdit!!.id
                                )
                            } else {
                                viewModel.createExperience(onSuccess = onExperienceCreated)
                            }
                        }
                    )
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(AppBackground.copy(alpha = 0.5f))
                        .clickable(enabled = false, onClick = {}),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = TurquoiseDark)
                }
            }
        }
    }
}

@Composable
private fun StyledOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    isError: Boolean = false,
    placeholder: String? = null,
    readOnly: Boolean = false,
    enabled: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { placeholder?.let { Text(it) } },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        isError = isError,
        readOnly = readOnly,
        enabled = enabled,
        keyboardOptions = keyboardOptions,
        trailingIcon = trailingIcon,
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
            unfocusedLabelColor = TextSecondary,
            disabledBorderColor = DividerColor.copy(alpha = 0.6f),
            disabledLabelColor = TextSecondary.copy(alpha = 0.6f),
            disabledTextColor = TextPrimary.copy(alpha = 0.6f),
            disabledPlaceholderColor = TextSecondary.copy(alpha = 0.6f),
            disabledTrailingIconColor = TextSecondary.copy(alpha = 0.6f),
            disabledContainerColor = AppBackground.copy(alpha = 0.3f)
        )
    )
}

@Composable
private fun ScheduleChip(
    time: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    OutlinedButton(
        onClick = onToggle,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) TurquoiseDark else TurquoiseLight.copy(alpha = 0.5f),
            contentColor = if (isSelected) Color.White else TurquoiseDark
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) TurquoiseDark else TurquoiseLight
        )
    ) {
        Text(time)
    }
}

@Composable
private fun PrimaryActionButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TurquoiseDark,
            contentColor = Color.White,
            disabledContainerColor = TurquoiseDark.copy(alpha = 0.4f),
            disabledContentColor = Color.White.copy(alpha = 0.9f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp),
                color = Color.White
            )
        } else {
            Text(text, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}