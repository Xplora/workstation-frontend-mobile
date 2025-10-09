package pe.edu.upc.tripmatch.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.edu.upc.tripmatch.presentation.di.PresentationModule
import pe.edu.upc.tripmatch.presentation.viewmodel.AgencyDashboardViewModel
import pe.edu.upc.tripmatch.presentation.viewmodel.BookingUi
import pe.edu.upc.tripmatch.presentation.viewmodel.ReviewUi

private val BrandTeal = Color(0xFF67B7B6)
private val BrandYellow = Color(0xFFFFDCA4)
private val TealLight = Color(0xFFD9F2EF)
private val TextPrimary = Color(0xFF1A202C)
private val TextSecondary = Color(0xFF58636A)
private val BorderLight = Color(0xFFE2E8F0)

@Composable
fun AgencyDashboardScreen(
    viewModel: AgencyDashboardViewModel = PresentationModule.getAgencyDashboardViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    when {
        uiState.isLoading -> {
            LoadingState()
        }
        uiState.errorMessage != null -> {
            ErrorState(message = uiState.errorMessage!!)
        }
        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(horizontal = 16.dp)
            ) {
                item { Spacer(Modifier.height(20.dp)) }

                item {
                    Text(
                        text = "Hola, ${uiState.agencyName}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary

                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "¿Listo para conectar con nuevos viajeros?",
                        fontSize = 15.sp,
                        color = TextSecondary
                    )
                    Spacer(Modifier.height(24.dp))
                }

                item {
                    SectionHeader("Resumen")
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        KpiCard(
                            number = uiState.stats.confirmedBookings.toString(),
                            label = "Reservas Confirmadas",
                            container = BrandTeal,
                            modifier = Modifier.weight(1f)
                        )
                        KpiCard(
                            number = uiState.stats.newQueries.toString(),
                            label = "Consultas Nuevas",
                            container = BrandYellow,
                            textOn = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        KpiCard(
                            number = uiState.stats.totalExperiences.toString(),
                            label = "Experiencias Activas",
                            container = BrandTeal.copy(alpha = 0.85f),
                            modifier = Modifier.weight(1f)
                        )
                        KpiCard(
                            number = uiState.stats.totalEarnings,
                            label = "Ganancias Totales",
                            container = BrandYellow.copy(alpha = 0.85f),
                            textOn = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(Modifier.height(28.dp))
                }

                item {
                    SectionHeader("Reservas Recientes")
                    Spacer(Modifier.height(12.dp))
                }
                if (uiState.recentBookings.isEmpty()) {
                    item { EmptyState("No hay reservas recientes.") }
                } else {
                    item { RecentBookingsTable(items = uiState.recentBookings) }
                }
                item { Spacer(Modifier.height(28.dp)) }

                item {
                    SectionHeader("Últimas Reseñas")
                    Spacer(Modifier.height(12.dp))
                }
                if (uiState.recentReviews.isEmpty()) {
                    item { EmptyState("Aún no tienes reseñas.") }
                } else {
                    items(uiState.recentReviews) { review ->
                        ReviewQuoteCard(review = review)
                        Spacer(Modifier.height(12.dp))
                    }
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = TextPrimary
    )
}

@Composable
private fun KpiCard(
    number: String,
    label: String,
    container: Color,
    modifier: Modifier = Modifier,
    textOn: Color = Color.White
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = container),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(min = 72.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = number,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = textOn
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = textOn,
                lineHeight = 18.sp,
                minLines = 2
            )
        }
    }
}

@Composable
private fun RecentBookingsTable(items: List<BookingUi>) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = TealLight),
        border = BorderStroke(1.dp, BorderLight)
    ) {
        Column(Modifier.padding(vertical = 8.dp, horizontal = 12.dp)) {
            Row(Modifier.padding(bottom = 8.dp)) {
                TableHeaderCell("Viajero", Modifier.weight(1f))
                TableHeaderCell("Experiencia", Modifier.weight(1.5f))
                TableHeaderCell("Estado", Modifier.weight(0.8f))
            }
            items.forEach { booking ->
                Row(
                    Modifier.padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableBodyCell(booking.traveler, Modifier.weight(1f))
                    TableBodyCell(booking.experience, Modifier.weight(1.5f), maxLines = 2)
                    TableBodyCell(booking.status, Modifier.weight(0.8f))
                }
            }
        }
    }
}

@Composable
private fun TableHeaderCell(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = TextPrimary.copy(alpha = 0.8f)
    )
}

@Composable
private fun TableBodyCell(text: String, modifier: Modifier = Modifier, maxLines: Int = 1) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = 14.sp,
        color = TextPrimary,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
        lineHeight = 18.sp
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
                text = review.comment,
                fontSize = 15.sp,
                color = TextPrimary,
                lineHeight = 22.sp
            )
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "— ${review.author}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(Modifier.weight(1f))
                StarsRow(count = review.rating)
            }
        }
    }
}

@Composable
private fun StarsRow(count: Int) {
    Row {
        repeat(count) { Text("★", color = Color(0xFFFFC107), fontSize = 16.sp) }
        repeat(5 - count) { Text("★", color = BorderLight, fontSize = 16.sp) }
    }
}

@Composable
private fun LoadingState() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(message: String) {
    Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
        Text(text = message, color = Color.Red)
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, color = TextSecondary)
    }
}