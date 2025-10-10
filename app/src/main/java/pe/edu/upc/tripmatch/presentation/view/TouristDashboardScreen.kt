package pe.edu.upc.tripmatch.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.tripmatch.presentation.di.PresentationModule
import pe.edu.upc.tripmatch.presentation.viewmodel.TouristDashboardViewModel


private val TurquoiseDark = Color(0xFF67B7B6)
private val TextGrey = Color(0xFF58636A)
private val BorderGrey = Color(0xFFE2E8F0)


@Composable
fun TouristDashboardScreen(
    viewModel: TouristDashboardViewModel = PresentationModule.getTouristDashboardViewModel(),
    modifier: Modifier = Modifier
) {
    LaunchedEffect(Unit) {
        viewModel.loadExperiences()
    }


    val experiences by viewModel.experiences.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val categoryOptions by viewModel.categories.collectAsState()


    var selectedDay by remember { mutableStateOf("Todos los Días") }
    var selectedCategory by remember { mutableStateOf("Todas") }

    val dayOptions = listOf("Día de Semana", "Fin de Semana", "Todos los Días")

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color.White
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            item {

                Text(
                    "Hola, $userName",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "Encontremos tu próxima experiencia única",
                    fontSize = 16.sp,
                    color = TextGrey,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }


            item {

                OutlinedTextField(
                    value = "Lima, Perú",
                    onValueChange = {  },
                    label = { Text("Destino") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = "Seleccionar Destino") }
                )
                Spacer(Modifier.height(16.dp))


                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    DropdownTextField(
                        label = "Día",
                        selectedValue = selectedDay,
                        options = dayOptions,
                        onOptionSelected = { selectedDay = it },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = "S/ 100 - S/ 300",
                        onValueChange = { },
                        label = { Text("Presupuesto") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(16.dp))


                DropdownTextField(
                    label = "Tipo de experiencia",
                    selectedValue = selectedCategory,
                    options = listOf("Todas") + categoryOptions,
                    onOptionSelected = { selectedCategory = it },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(24.dp))


                Button(
                    onClick = {  },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TurquoiseDark)
                ) {
                    Text("Buscar", fontSize = 18.sp)
                }
                Spacer(Modifier.height(24.dp))
            }


            item {

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    items(categoryOptions) { tag ->
                        AssistChip(
                            onClick = {  },
                            label = { Text(tag) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color.White,
                                labelColor = TextGrey
                            ),
                            border = BorderStroke(1.dp, BorderGrey)
                        )
                    }
                }
            }


            item {
                Text(
                    "Recomendaciones para ti",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            if (experiences.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = TurquoiseDark)
                    }
                }
            } else {
                items(experiences, key = { it.id }) { experience ->
                    ExperienceCardTourits(
                        experience = experience,
                        onToggleFavorite = { viewModel.toggleFavorite(experience) }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun DropdownTextField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {

        OutlinedTextField(
            value = selectedValue,
            onValueChange = { },
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )


        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onOptionSelected(selectionOption)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}