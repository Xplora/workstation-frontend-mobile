package pe.edu.upc.tripmatch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upc.tripmatch.data.repository.AgencyRepository
import pe.edu.upc.tripmatch.data.model.AgencyProfileDto

data class AgencyProfileUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val agencyProfile: AgencyProfileDto? = null,
    val reviews: List<ReviewUi> = emptyList(),
    val rating: Float = 0.0f,
    val reviewCount: Int = 0
)

class AgencyProfileViewModel(
    private val agencyRepository: AgencyRepository,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(AgencyProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    fun loadProfileData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val currentUser = authViewModel.uiState.value.currentUser
            if (currentUser != null) {
                try {
                    val profile = agencyRepository.getAgencyProfile(currentUser.id, currentUser.token)
                    val dashboardData = agencyRepository.getDashboardData(currentUser.id, currentUser.token)

                    val reviews = dashboardData.recentReviews
                    val totalRating = reviews.sumOf { it.rating }.toFloat()
                    val averageRating = if (reviews.isNotEmpty()) totalRating / reviews.size else 0.0f

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            agencyProfile = profile,
                            reviews = reviews,
                            rating = averageRating,
                            reviewCount = reviews.size
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error al cargar el perfil: ${e.message}") }
                }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Usuario no autenticado.") }
            }
        }
    }
}