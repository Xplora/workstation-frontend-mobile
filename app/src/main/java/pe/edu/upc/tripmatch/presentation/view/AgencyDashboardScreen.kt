package pe.edu.upc.tripmatch.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import pe.edu.upc.tripmatch.R

// ======================
// Paleta (según tu guía)
// ======================
private val BrandTeal = Color(0xFF67B7B6)     // teal principal
private val BrandTealDark = Color(0xFF318C8B) // acento más oscuro (para énfasis)
private val BrandYellow = Color(0xFFFFDCA4)   // naranja/amarillo suave
private val TealLight = Color(0xFFD9F2EF)     // fondo celeste-verdoso pálido (tabla)
private val TextPrimary = Color(0xFF000000)
private val TextSecondary = Color(0xFF58636A)
private val BorderLight = Color(0xFFE2E8F0)

// ======================
// Datos mock
// ======================
data class AgencyStats(
    val confirmedBookings: Int,
    val newQueries: Int,
    val localExperiences: Int,
    val localEarnings: String
)

data class BookingUi(
    val traveler: String,
    val experience: String,
    val date: String,
    val status: String
)

data class ReviewUi(
    val id: String,
    val author: String,
    val comment: String,
    val rating: Int, // 0..5
    val avatarRes: Int = R.drawable.ic_tripmatch_logo
)

// ======================
// Pantalla principal
// ======================
@Composable
fun AgencyDashboardScreen(
    agencyName: String = "NickName",
    onAddExperience: () -> Unit = {},
    onViewAllReviews: () -> Unit = {},
    onViewAllBookings: () -> Unit = {}
) {
    var isLoading by remember { mutableStateOf(true) }

    // Mock inicial (reemplazar luego con repo/VM)
    var stats by remember {
        mutableStateOf(
            AgencyStats(
                confirmedBookings = 6,
                newQueries = 2,
                localExperiences = 12,
                localEarnings = "S/ 12,450"
            )
        )
    }
    var recent by remember {
        mutableStateOf(
            listOf(
                BookingUi("Ana López", "City Tour Arequipa", "20/04/25", "Activo"),
                BookingUi("Ana López", "City Tour Arequipa", "20/04/25", "Activo"),
                BookingUi("Ana López", "City Tour Arequipa", "20/04/25", "Activo")
            )
        )
    }
    var reviews by remember {
        mutableStateOf(
            listOf(
                ReviewUi("1", "Carlos Méndez",
                    "“Una experiencia inolvidable, todo estuvo muy bien organizado.”", 5),
                ReviewUi("2", "Carlos Méndez",
                    "“Una experiencia inolvidable, todo estuvo muy bien organizado.”", 5)
            )
        )
    }

    LaunchedEffect(Unit) {
        // simula carga corta
        delay(300)
        isLoading = false
    }

    if (isLoading) {
        LoadingState()
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        // Encabezado de marca (coincide con tu AppBar visual; si ya lo tienes arriba, puedes omitir este bloque)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
        }

        Spacer(Modifier.height(20.dp))

        // Bloque de bienvenida
        Text(
            text = "Hola, $agencyName",
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "¿Listo para conectar con nuevos viajeros?",
            fontSize = 14.sp,
            color = TextSecondary
        )

        Spacer(Modifier.height(20.dp))

        // Sección: Resumen (2 filas de KPIs: Reservas/Consultas y Locales)
        Text(
            text = "Resumen",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(Modifier.height(10.dp))

        // Fila 1: Reservas confirmadas / Consultas nuevas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiCard(
                number = stats.confirmedBookings.toString(),
                line1 = "Reservas",
                line2 = "Confirmadas",
                container = BrandTeal,
                textOn = Color.White,
                modifier = Modifier.weight(1f)
            )
            KpiCard(
                number = stats.newQueries.toString(),
                line1 = "Consultas",
                line2 = "Nuevas",
                container = BrandYellow,
                textOn = TextPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(12.dp))

        // Fila 2: Experiencias locales / Ganancias locales
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            KpiCard(
                number = stats.localExperiences.toString(),
                line1 = "Experiencias",
                line2 = "Locales",
                container = BrandTeal.copy(alpha = 0.85f),
                textOn = Color.White,
                modifier = Modifier.weight(1f)
            )
            KpiCard(
                number = stats.localEarnings,
                line1 = "Ganancias",
                line2 = "Locales",
                container = BrandYellow.copy(alpha = 0.85f),
                textOn = TextPrimary,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(22.dp))

        // Sección: Reservas recientes (tabla dentro de card celeste pálido)
        Text(
            text = "Reservas recientes",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(Modifier.height(8.dp))

        RecentBookingsTable(
            items = recent,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(22.dp))

        // Sección: Últimas reseñas
        Text(
            text = "Últimas reseñas",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
        Spacer(Modifier.height(10.dp))

        // Dos tarjetas apiladas
        reviews.take(2).forEachIndexed { i, r ->
            ReviewQuoteCard(review = r)
            if (i == 0) Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(22.dp))
    }
}

// ======================
// Componentes
// ======================

@Composable
private fun KpiCard(
    number: String,
    line1: String,
    line2: String,
    container: Color,
    textOn: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        modifier = modifier.heightIn(min = 84.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = number,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textOn
            )
            Spacer(Modifier.width(14.dp))
            Column {
                Text(line1, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = textOn)
                Text(line2, fontSize = 13.sp, color = textOn.copy(alpha = 0.9f))
            }
        }
    }
}

@Composable
private fun RecentBookingsTable(
    items: List<BookingUi>,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = TealLight),
        border = BorderStroke(1.dp, BorderLight),
        modifier = modifier
    ) {
        Column(Modifier.padding(12.dp)) {
            // encabezados
            Row(Modifier.fillMaxWidth()) {
                TableHeaderCell("Viajero", Modifier.weight(1f))
                TableHeaderCell("Experiencia", Modifier.weight(1.2f))
                TableHeaderCell("Fecha", Modifier.weight(0.8f))
                TableHeaderCell("Estado", Modifier.weight(0.8f))
            }
            Spacer(Modifier.height(6.dp))

            items.forEach { b ->
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    TableBodyCell(b.traveler, Modifier.weight(1f))
                    TableBodyCell(b.experience, Modifier.weight(1.2f), maxLines = 2)
                    TableBodyCell(b.date, Modifier.weight(0.8f))
                    TableBodyCell(b.status, Modifier.weight(0.8f))
                }
            }
        }
    }
}

@Composable
private fun TableHeaderCell(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier.padding(horizontal = 6.dp),
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
    )
}

@Composable
private fun TableBodyCell(
    text: String,
    modifier: Modifier = Modifier,
    maxLines: Int = 1
) {
    Text(
        text = text,
        modifier = modifier.padding(horizontal = 6.dp),
        fontSize = 13.sp,
        color = TextPrimary,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun ReviewQuoteCard(review: ReviewUi) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = review.comment, // ya viene con comillas “”
                fontSize = 14.sp,
                color = TextPrimary
            )
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "— ${review.author}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(Modifier.width(8.dp))
                StarsRow(count = review.rating)
            }
        }
    }
}

@Composable
private fun StarsRow(count: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(count.coerceIn(0,5)) { Text("★", color = Color(0xFFFFC107)) }
        repeat((5 - count.coerceIn(0,5))) { Text("☆", color = Color(0xFFFFC107)) }
    }
}

@Composable
private fun LoadingState() {
    Box(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            androidx.compose.material3.CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text("Cargando panel de control...", color = TextSecondary)
        }
    }
}
