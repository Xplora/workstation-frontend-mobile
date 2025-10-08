package pe.edu.upc.tripmatch.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pe.edu.upc.tripmatch.domain.model.Experience

@Composable
fun ExperienceListItemView(experience: Experience, toggleFavorite: () -> Unit) {

    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(experience.title)
                Text(experience.description, maxLines = 2)
                Text("Price: $${experience.price}")
            }


            IconButton(onClick = {

                toggleFavorite()
            }) {
                Icon(
                    if (experience.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (experience.isFavorite) "Quitar de favoritos" else "Añadir a favoritos"
                )
            }
        }
    }
}
