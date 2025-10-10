package pe.edu.upc.tripmatch.presentation.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upc.tripmatch.presentation.di.PresentationModule
import pe.edu.upc.tripmatch.presentation.viewmodel.TouristDashboardViewModel

@Composable
fun FavoritesScreen(
    viewModel: TouristDashboardViewModel = PresentationModule.getTouristDashboardViewModel()
) {

    LaunchedEffect(Unit) { viewModel.loadFavorites() }
    val favorites = viewModel.favorites.collectAsState()

    if (favorites.value.isEmpty()) {

        Text(
            "No tienes experiencias favoritas guardadas.",
            modifier = Modifier.padding(24.dp)
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(favorites.value, key = { it.id }) { experience ->
                ExperienceCardTourits(
                    experience = experience,
                    onToggleFavorite = { viewModel.toggleFavorite(experience) }
                )
            }
        }
    }
}
