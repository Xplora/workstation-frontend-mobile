package pe.edu.upc.tripmatch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upc.tripmatch.data.repository.ExperienceRepository
import pe.edu.upc.tripmatch.domain.model.Experience
import android.util.Log

data class ManageExperiencesUiState(
    val isLoading: Boolean = true,
    val experiences: List<Experience> = emptyList(),
    val error: String? = null,
    val showDeleteDialog: Boolean = false,
    val successMessage: String? = null,
    val experienceToDelete: Experience? = null
)

class ManageExperiencesViewModel(
    private val experienceRepository: ExperienceRepository,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageExperiencesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadAgencyExperiences()
    }

    fun loadAgencyExperiences() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val agencyId = authViewModel.uiState.value.currentUser?.id
            val token = authViewModel.uiState.value.currentUser?.token

            if (agencyId.isNullOrEmpty() || token.isNullOrEmpty()) {
                _uiState.update { it.copy(isLoading = false, error = "Error de sesión: ID o Token de agencia no disponible.") }
                Log.e("ManageExpViewModel", "Agency ID is null or empty. Current ID: $agencyId")
                return@launch
            }


            try {
                val experiences = experienceRepository.getExperiencesForAgency(agencyId)
                _uiState.update { it.copy(isLoading = false, experiences = experiences) }
                Log.i("ManageExpViewModel", "Experiencias cargadas: ${experiences.size}")
            } catch (e: Exception) {
                Log.e("ManageExpViewModel", "Error al cargar experiencias de la agencia: ${e.message}", e)
                _uiState.update { it.copy(isLoading = false, error = "Error al cargar las experiencias. Detalles: ${e.message}") }
            }
        }
    }

    fun onConfirmDelete() {
        val experience = _uiState.value.experienceToDelete ?: return

        _uiState.update { it.copy(showDeleteDialog = false, experienceToDelete = null, isLoading = true, error = null, successMessage = null) }

        viewModelScope.launch {
            try {
                experienceRepository.deleteExperience(experience.id)

                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        experiences = currentState.experiences.filter { it.id != experience.id },
                        successMessage = "¡Experiencia '${experience.title}' eliminada con éxito!"
                    )
                }

            } catch (e: Exception) {
                Log.e("ManageExpViewModel", "Error al eliminar experiencia: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al eliminar. Detalle: ${e.message}",
                    )
                }
            }
        }
    }

    fun openDeleteDialog(experience: Experience) {
        _uiState.update { it.copy(showDeleteDialog = true, experienceToDelete = experience) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false, experienceToDelete = null) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }
}