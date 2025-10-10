package pe.edu.upc.tripmatch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upc.tripmatch.data.model.CreateExperienceCommand
import pe.edu.upc.tripmatch.data.model.ExperienceImage
import pe.edu.upc.tripmatch.data.model.Include
import pe.edu.upc.tripmatch.data.model.Schedule
import pe.edu.upc.tripmatch.data.repository.ExperienceRepository

data class CreateExperienceUiState(
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val duration: String = "",
    val price: String = "",
    val frequencies: String = "",
    val schedules: List<String> = emptyList(),
    val images: List<String> = emptyList(),
    val includes: List<String> = emptyList(),
    val categoryId: Int? = null,
    val isLoading: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class CreateExperienceViewModel(
    private val repository: ExperienceRepository,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateExperienceUiState())
    val uiState = _uiState.asStateFlow()

    val frequencyOptions = listOf("weekdays", "weekends", "daily")
    val scheduleOptions = listOf("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00")
    val categoryOptions = listOf(Pair(1, "Aventura"), Pair(2, "Cultural"), Pair(3, "Gastronómica"))

    fun updateField(field: String, value: Any) {
        _uiState.update {
            when (field) {
                "title" -> it.copy(title = value as String, errorMessage = null)
                "description" -> it.copy(description = value as String, errorMessage = null)
                "location" -> it.copy(location = value as String, errorMessage = null)
                "duration" -> it.copy(duration = value as String, errorMessage = null)
                "price" -> it.copy(price = value as String, errorMessage = null)
                "frequencies" -> it.copy(frequencies = value as String, errorMessage = null)
                "categoryId" -> it.copy(categoryId = value as Int, errorMessage = null)
                else -> it
            }
        }
    }

    fun toggleSchedule(time: String) {
        _uiState.update {
            val newSchedules = if (it.schedules.contains(time)) {
                it.schedules - time
            } else {
                it.schedules + time
            }
            it.copy(schedules = newSchedules.sorted())
        }
    }

    fun addInclude(include: String) {
        if (include.isNotBlank() && _uiState.value.includes.size < 3) {
            _uiState.update { it.copy(includes = it.includes + include.trim()) }
        }
    }

    fun removeInclude(index: Int) {
        _uiState.update {
            it.copy(includes = it.includes.toMutableList().apply { removeAt(index) })
        }
    }

    fun addImage(url: String) {
        if (url.isNotBlank()) {
            _uiState.update { it.copy(images = it.images + url.trim()) }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }


    fun createExperience(onSuccess: () -> Unit) {
        val state = _uiState.value
        val agencyId = authViewModel.uiState.value.currentUser?.id

        if (agencyId.isNullOrBlank()) {
            _uiState.update { it.copy(errorMessage = "Error de sesión: ID de agencia no disponible.") }
            return
        }

        if (state.title.isBlank() || state.description.isBlank() || state.price.toDoubleOrNull() == null || state.schedules.isEmpty() || state.images.isEmpty() || state.categoryId == null) {
            _uiState.update { it.copy(errorMessage = "Por favor, completa todos los campos requeridos (título, descripción, precio, categoría, horarios e imágenes).") }
            return
        }


        val command = CreateExperienceCommand(
            title = state.title,
            description = state.description,
            location = state.location,
            duration = state.duration.toIntOrNull() ?: 1,
            price = state.price.toDoubleOrNull() ?: 0.0,
            frequencies = state.frequencies,
            schedules = state.schedules.map { Schedule(it) },
            experienceImages = state.images.map { ExperienceImage(it) },
            includes = state.includes.map { Include(it) },
            categoryId = state.categoryId!!,
            agencyUserId = agencyId
        )

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                repository.createExperience(command)
                _uiState.update {
                    CreateExperienceUiState(
                        successMessage = "¡Experiencia '${command.title}' creada con éxito!",
                        isLoading = false
                    )
                }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error al crear experiencia: ${e.message}"
                    )
                }
            }
        }
    }
}