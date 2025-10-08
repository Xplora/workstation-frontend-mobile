package pe.edu.upc.tripmatch.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upc.tripmatch.presentation.di.PresentationModule
import pe.edu.upc.tripmatch.presentation.viewmodel.ExperienceListViewModel

@Composable
fun ExperienceListScreen(
    viewModel: ExperienceListViewModel = PresentationModule.getExperienceListViewModel(),
    modifier: Modifier = Modifier // <-- nuevo parámetro
) {
    LaunchedEffect(Unit) {
        viewModel.loadExperiences()
    }

    val experiencesState = viewModel.experiences.collectAsState()

    if (experiencesState.value.isEmpty()) {
        CircularProgressIndicator(Modifier.padding(16.dp))
    } else {
        LazyColumn(
            modifier = modifier.padding(8.dp), // <-- usamos el modifier
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(experiencesState.value, key = { it.id }) { experience ->
                ExperienceListItemView(experience) {
                    viewModel.toggleFavorite(experience)
                }
            }
        }
    }
}