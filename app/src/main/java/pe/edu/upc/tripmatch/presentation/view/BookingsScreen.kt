package pe.edu.upc.tripmatch.presentation.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import pe.edu.upc.tripmatch.R
import pe.edu.upc.tripmatch.domain.model.Booking
import pe.edu.upc.tripmatch.presentation.viewmodel.BookingsViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun BookingsScreen(viewModel: BookingsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF8F9FA)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            HeaderSection(totalIncome = uiState.totalIncome)
            Spacer(Modifier.height(8.dp))
            Text(
                "Las reservas y detalles totales a tu agencia",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(Modifier.height(16.dp))
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::onSearchQueryChanged
            )
            Spacer(Modifier.height(20.dp))

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.errorMessage != null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = uiState.errorMessage!!, color = Color.Red)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(uiState.filteredBookings) { booking ->
                        BookingCard(booking = booking)
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderSection(totalIncome: Double) {
    val formattedIncome = NumberFormat.getNumberInstance(Locale("es", "PE")).format(totalIncome)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "Panel de\nReservas",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 36.sp
        )
        Spacer(Modifier.weight(1f))
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4E0))
        ) {
            Text(
                text = "Ingresos: S/${formattedIncome}",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                color = Color(0xFFD97706),
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Buscar por viajero o por experiencia", fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.LightGray,
            unfocusedBorderColor = Color(0xFFE2E8F0),
        ),
        singleLine = true
    )
}

@Composable
private fun BookingCard(booking: Booking) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = booking.travelerImage ?: R.drawable.ic_tripmatch_logo,
                    contentDescription = "Traveler Image",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(booking.travelerName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(booking.experienceName, color = Color.Gray, fontSize = 14.sp)
                }
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5))
                ) {
                    Text(
                        text = "S/ ${"%.2f".format(booking.totalPaid)}",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF065F46)
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Row {
                Text("Fecha: ${booking.date}", color = Color.Gray, fontSize = 14.sp)
                Spacer(Modifier.weight(1f))
                Text("Personas: ${booking.people} personas", color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}