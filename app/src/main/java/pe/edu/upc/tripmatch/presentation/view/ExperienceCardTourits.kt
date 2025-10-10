package pe.edu.upc.tripmatch.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import pe.edu.upc.tripmatch.domain.model.Experience

private val TurquoiseDark = Color(0xFF67B7B6)
private val TextGrey = Color(0xFF58636A)
private val BorderGrey = Color(0xFFE2E8F0)
private val WhiteTransparent = Color(0x99FFFFFF)

@Composable
fun ExperienceCardTourits(
    experience: Experience,
    onToggleFavorite: (Experience) -> Unit,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BorderGrey),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(10.dp))
            ) {
                AsyncImage(
                    model = experience.experienceImages.firstOrNull(),
                    contentDescription = experience.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .size(28.dp)
                ) {
                    IconButton(
                        onClick = { onToggleFavorite(experience) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (experience.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (experience.isFavorite) Color.Red else TextGrey.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.width(12.dp))


            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = experience.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    maxLines = 1,
                    color = Color.Black
                )
                Spacer(Modifier.height(4.dp))


                val scheduleText = experience.schedule.joinToString(" | ")
                Text(
                    text = if (scheduleText.isBlank()) "Horario no especificado" else scheduleText,
                    color = TextGrey,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(2.dp))

                Text(
                    text = "${experience.duration} horas • ${experience.frequencies} • ${experience.categoryName}",
                    color = TextGrey,
                    fontSize = 13.sp
                )
            }

            Text(
                text = "S/ ${"%.0f".format(experience.price)}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = TurquoiseDark,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}
