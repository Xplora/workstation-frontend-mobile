package pe.edu.upc.tripmatch.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import pe.edu.upc.tripmatch.domain.model.Experience
import pe.edu.upc.tripmatch.presentation.di.PresentationModule
import pe.edu.upc.tripmatch.presentation.viewmodel.CreateExperienceViewModel
import pe.edu.upc.tripmatch.ui.theme.*

private val BrandColor = Color(0xFF67B7B6)
private val BackgroundColor = Color(0xFFF8F9FA)
private val SurfaceColor = Color.White
private val TextDark = Color(0xFF1A202C)
private val TextGrey = Color(0xFF718096)

@OptIn(ExperimentalMaterial3Api::class)
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
    val titleText = if (isEditing) "Editar Experiencia" else "Crear Experiencia"

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
                duration = SnackbarDuration.Short,
                withDismissAction = true
            )
            viewModel.clearMessages()
            if (uiState.successMessage != null) {
                onExperienceCreated()
            }
        }
    }

    Scaffold(
        containerColor = BackgroundColor,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        titleText,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        color = TextDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = TextDark,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundColor,
                    scrolledContainerColor = BackgroundColor
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            Surface(
                color = SurfaceColor,
                shadowElevation = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(Modifier.padding(vertical = 16.dp, horizontal = 24.dp)) {
                    PrimaryActionButton(
                        text = if (isEditing) "Guardar Cambios" else "Publicar Experiencia",
                        isLoading = uiState.isLoading,
                        onClick = {
                            if (isEditing) {
                                viewModel.saveExperience(onSuccess = onExperienceCreated, existingExperienceId = experienceToEdit!!.id)
                            } else {
                                viewModel.createExperience(onSuccess = onExperienceCreated)
                            }
                        }
                    )
                }
            }
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                FormSection(title = "Información General", icon = Icons.Outlined.Info) {
                    StyledOutlinedTextField(
                        value = uiState.title,
                        onValueChange = { viewModel.updateField("title", it) },
                        label = "Título de la experiencia",
                        placeholder = "Ej. Caminata por los Andes",
                        leadingIcon = Icons.Outlined.Title,
                        isError = uiState.errorMessage != null && uiState.title.isBlank()
                    )

                    Spacer(Modifier.height(16.dp))

                    StyledOutlinedTextField(
                        value = uiState.description,
                        onValueChange = { viewModel.updateField("description", it) },
                        label = "Descripción detallada",
                        placeholder = "Describe qué hace única a esta experiencia...",
                        leadingIcon = Icons.Outlined.Description,
                        singleLine = false,
                        modifier = Modifier.height(120.dp)
                    )
                }
            }
            item {
                FormSection(title = "Ubicación y Precio", icon = Icons.Outlined.Place) {
                    StyledOutlinedTextField(
                        value = uiState.location,
                        onValueChange = { viewModel.updateField("location", it) },
                        label = "Dirección o punto de encuentro",
                        leadingIcon = Icons.Outlined.LocationOn
                    )

                    Spacer(Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StyledOutlinedTextField(
                            value = uiState.duration,
                            onValueChange = { viewModel.updateField("duration", it) },
                            label = "Duración (h)",
                            modifier = Modifier.weight(0.8f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = Icons.Outlined.Schedule
                        )
                        StyledOutlinedTextField(
                            value = uiState.price,
                            onValueChange = { viewModel.updateField("price", it) },
                            label = "Precio (S/)",
                            modifier = Modifier.weight(1.2f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            leadingIcon = Icons.Outlined.AttachMoney,
                            isError = uiState.errorMessage != null && uiState.price.toDoubleOrNull() == null
                        )
                    }
                }
            }
            item {
                FormSection(title = "Detalles", icon = Icons.Outlined.Category) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(Modifier.weight(1f)) {
                            CustomDropdown(
                                label = "Categoría",
                                selectedOption = viewModel.categoryOptions.find { it.first == uiState.categoryId }?.second,
                                options = viewModel.categoryOptions.map { it.second },
                                onOptionSelected = { name ->
                                    val id = viewModel.categoryOptions.find { it.second == name }?.first
                                    if (id != null) viewModel.updateField("categoryId", id)
                                }
                            )
                        }

                        Box(Modifier.weight(1f)) {
                            CustomDropdown(
                                label = "Frecuencia",
                                selectedOption = uiState.frequencies.takeIf { it.isNotBlank() },
                                options = viewModel.frequencyOptions,
                                onOptionSelected = { viewModel.updateField("frequencies", it) }
                            )
                        }
                    }
                }
            }
            item {
                FormSection(title = "Horarios de inicio", icon = Icons.Outlined.AccessTime) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        viewModel.scheduleOptions.forEach { time ->
                            val isSelected = uiState.schedules.contains(time)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.toggleSchedule(time) },
                                label = { Text(time, fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandColor,
                                    selectedLabelColor = Color.White,
                                    containerColor = SurfaceColor,
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = if(isSelected) BrandColor else DividerColor,
                                    borderWidth = if(isSelected) 2.dp else 1.dp
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.height(40.dp)
                            )
                        }
                    }
                }
            }
            item {
                FormSection(title = "¿Qué incluye?", icon = Icons.Outlined.CheckCircle) {
                    AddItemRow(
                        value = newIncludeInput,
                        onValueChange = { newIncludeInput = it },
                        onAdd = {
                            viewModel.addInclude(newIncludeInput)
                            newIncludeInput = ""
                        },
                        placeholder = "Ej. Equipo de seguridad...",
                        enabled = uiState.includes.size < 5
                    )

                    if (uiState.includes.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            uiState.includes.forEachIndexed { index, item ->
                                RemovableChip(label = item, onRemove = { viewModel.removeInclude(index) })
                            }
                        }
                    }
                }
            }
            item {
                FormSection(title = "Galería de Fotos", icon = Icons.Outlined.Image) {
                    AddItemRow(
                        value = imageUrlInput,
                        onValueChange = { imageUrlInput = it },
                        onAdd = {
                            viewModel.addImage(imageUrlInput)
                            imageUrlInput = ""
                        },
                        placeholder = "https://ejemplo.com/imagen.jpg",
                        icon = Icons.Outlined.Link,
                        buttonIcon = Icons.Default.AddPhotoAlternate
                    )
                    if (uiState.images.isNotEmpty()) {
                        Spacer(Modifier.height(16.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            itemsIndexed(uiState.images) { index, url ->
                                ImagePreviewItem(
                                    imageUrl = url,
                                    onRemove = {
                                        viewModel.removeImage(index)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun ImagePreviewItem(
    imageUrl: String,
    onRemove: () -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = "Previsualización",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        IconButton(
            onClick = onRemove,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(24.dp)
                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Eliminar imagen",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}


@Composable
fun FormSection(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = BrandColor, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

@Composable
fun StyledOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    singleLine: Boolean = true,
    isError: Boolean = false,
    readOnly: Boolean = false,
    leadingIcon: ImageVector? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { placeholder?.let { Text(it, color = TextGrey.copy(alpha = 0.5f)) } },
        modifier = modifier.fillMaxWidth(),
        singleLine = singleLine,
        isError = isError,
        readOnly = readOnly,
        keyboardOptions = keyboardOptions,
        leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null, tint = TextGrey) } },
        trailingIcon = trailingIcon,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = BrandColor,
            unfocusedBorderColor = Color(0xFFE2E8F0),
            focusedContainerColor = SurfaceColor,
            unfocusedContainerColor = SurfaceColor,
            cursorColor = BrandColor,
            focusedLabelColor = BrandColor,
            unfocusedLabelColor = TextGrey,
            focusedTextColor = TextDark,
            unfocusedTextColor = TextDark,
            errorBorderColor = MaterialTheme.colorScheme.error,
            errorLabelColor = MaterialTheme.colorScheme.error
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(
    label: String,
    selectedOption: String?,
    options: List<String>,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        StyledOutlinedTextField(
            value = selectedOption ?: "",
            onValueChange = {},
            label = label,
            readOnly = true,
            placeholder = "Seleccionar",
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(SurfaceColor)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, color = TextDark) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun AddItemRow(
    value: String,
    onValueChange: (String) -> Unit,
    onAdd: () -> Unit,
    placeholder: String,
    icon: ImageVector = Icons.Default.Add,
    buttonIcon: ImageVector = Icons.Default.Add,
    enabled: Boolean = true
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        StyledOutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = "Añadir nuevo",
            placeholder = placeholder,
            modifier = Modifier.weight(1f),
            singleLine = true,
            leadingIcon = icon
        )
        Spacer(Modifier.width(12.dp))
        FilledIconButton(
            onClick = onAdd,
            enabled = value.isNotBlank() && enabled,
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = BrandColor, disabledContainerColor = BrandColor.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.size(56.dp)
        ) {
            Icon(buttonIcon, contentDescription = "Añadir", tint = Color.White)
        }
    }
}

@Composable
fun RemovableChip(label: String, onRemove: () -> Unit) {
    InputChip(
        selected = true,
        onClick = { },
        label = { Text(label, maxLines = 1, fontWeight = FontWeight.Medium) },
        trailingIcon = {
            Icon(
                Icons.Default.Close,
                contentDescription = "Borrar",
                modifier = Modifier.size(18.dp).clickable { onRemove() }
            )
        },
        colors = InputChipDefaults.inputChipColors(
            selectedContainerColor = BrandColor.copy(alpha = 0.1f),
            selectedLabelColor = BrandColor,
            selectedTrailingIconColor = BrandColor
        ),
        border = BorderStroke(1.dp, BrandColor.copy(alpha = 0.3f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.height(36.dp)
    )
}

@Composable
private fun PrimaryActionButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = BrandColor,
            contentColor = Color.White,
            disabledContainerColor = BrandColor.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(14.dp),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp, pressedElevation = 2.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                strokeWidth = 3.dp,
                modifier = Modifier.size(24.dp),
                color = Color.White
            )
        } else {
            Text(text, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}