package pe.edu.upc.tripmatch.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
private val TurquoiseLight = Color(0xFFD9F2EF)
private val TextGrey = Color(0xFF58636A)
private val BorderGrey = Color(0xFFE2E8F0)

@Composable
fun ExperienceCard(
    experience: Experience,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, BorderGrey),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = experience.experienceImages.firstOrNull(),
                    contentDescription = experience.title,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(
                        text = experience.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "S/ ${"%.2f".format(experience.price)} • ${experience.duration} horas",
                        color = TextGrey,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(

                        text = "${experience.categoryName} • ${experience.frequencies}",
                        color = TextGrey,
                        fontSize = 14.sp
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = experience.description,
                color = TextGrey,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = TurquoiseDark
                    ),
                    border = BorderStroke(1.dp, TurquoiseLight)
                ) {
                    Text("Eliminar")
                }
                Button(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TurquoiseDark,
                        contentColor = Color.White
                    )
                ) {
                    Text("Editar")
                }
            }
        }
    }
}