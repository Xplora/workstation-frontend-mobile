package pe.edu.upc.tripmatch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upc.tripmatch.data.repository.AgencyRepository
import pe.edu.upc.tripmatch.domain.model.Booking

data class BookingsUiState(
    val isLoading: Boolean = true,
    val bookings: List<Booking> = emptyList(),
    val searchQuery: String = "",
    val errorMessage: String? = null
) {
    val totalIncome: Double
        get() = bookings.sumOf { it.totalPaid }

    val filteredBookings: List<Booking>
        get() = if (searchQuery.isBlank()) {
            bookings
        } else {
            bookings.filter {
                it.travelerName.contains(searchQuery, ignoreCase = true) ||
                        it.experienceName.contains(searchQuery, ignoreCase = true)
            }
        }
}

class BookingsViewModel(
    private val agencyRepository: AgencyRepository,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadBookings()
    }

    fun loadBookings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentUser = authViewModel.uiState.value.currentUser
            if (currentUser == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: Usuario no autenticado.") }
                return@launch
            }

            try {
                val bookings = agencyRepository.getBookingsForAgency(currentUser.id, currentUser.token)
                _uiState.update {
                    it.copy(isLoading = false, bookings = bookings)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "No se pudieron cargar las reservas.") }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }
}