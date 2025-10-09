package pe.edu.upc.tripmatch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upc.tripmatch.data.repository.AgencyRepository

data class AgencyDashboardUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val agencyName: String = "",
    val stats: AgencyStatsUi = AgencyStatsUi(),
    val recentBookings: List<BookingUi> = emptyList(),
    val recentReviews: List<ReviewUi> = emptyList()
)

data class AgencyStatsUi(
    val confirmedBookings: Int = 0,
    val newQueries: Int = 0,
    val totalExperiences: Int = 0,
    val totalEarnings: String = "S/ 0.00"
)

data class BookingUi(
    val traveler: String,
    val experience: String,
    val date: String,
    val status: String
)

data class ReviewUi(
    val author: String,
    val comment: String,
    val rating: Int
)

class AgencyDashboardViewModel(
    private val agencyRepository: AgencyRepository,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(AgencyDashboardUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val currentUser = authViewModel.uiState.value.currentUser
            if (currentUser == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: Usuario no autenticado.") }
                return@launch
            }

            try {
                val dashboardData = agencyRepository.getDashboardData(currentUser.id, currentUser.token)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        agencyName = dashboardData.agencyName,
                        stats = dashboardData.stats,
                        recentBookings = dashboardData.recentBookings,
                        recentReviews = dashboardData.recentReviews
                    )
                }
            } catch (e: Exception) {
                Log.e("DashboardViewModel", "Falló la carga de datos del dashboard", e)

                _uiState.update { it.copy(isLoading = false, errorMessage = "No se pudieron cargar los datos del dashboard.") }
            }
        }
    }
}