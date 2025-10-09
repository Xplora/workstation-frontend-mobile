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
            _uiState.update { it.copy(isLoading = true) }
            val agencyId = authViewModel.uiState.value.currentUser?.id
            if (agencyId == null) {
                _uiState.update { it.copy(isLoading = false, error = "No se pudo verificar la agencia.") }
                return@launch
            }
            try {
                val experiences = experienceRepository.getExperiencesForAgency(agencyId)
                _uiState.update { it.copy(isLoading = false, experiences = experiences) }
            } catch (e: Exception) {
                // --- CAMBIO CLAVE AQUÍ ---
                // Imprimimos el error real en la consola de Logcat
                Log.e("ManageExpViewModel", "Error al cargar experiencias de la agencia", e)

                _uiState.update { it.copy(isLoading = false, error = "Error al cargar las experiencias.") }
            }
        }
    }

    fun onConfirmDelete() {
        val experience = _uiState.value.experienceToDelete
        println("Eliminando experiencia: ${experience?.title}")

        // Cerramos el diálogo y actualizamos la lista (simulado por ahora)
        _uiState.update { currentState ->
            currentState.copy(
                showDeleteDialog = false,
                experienceToDelete = null,
                experiences = currentState.experiences.filter { it.id != experience?.id }
            )
        }
    }

    fun openDeleteDialog(experience: Experience) {
        _uiState.update { it.copy(showDeleteDialog = true, experienceToDelete = experience) }
    }

    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false, experienceToDelete = null) }
    }
}