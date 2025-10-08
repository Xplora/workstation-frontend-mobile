package pe.edu.upc.tripmatch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.edu.upc.tripmatch.data.model.ExperienceDto
import pe.edu.upc.tripmatch.data.model.ExperienceMapper.toDomain
import pe.edu.upc.tripmatch.data.repository.ExperienceRepository
import pe.edu.upc.tripmatch.domain.model.Experience

class ExperienceListViewModel(
    private val repository: ExperienceRepository
) : ViewModel() {

    private val _experiences = MutableStateFlow<List<Experience>>(emptyList())
    val experiences: StateFlow<List<Experience>> get() = _experiences.asStateFlow()

    private val _favorites = MutableStateFlow<List<Experience>>(emptyList())
    val favorites: StateFlow<List<Experience>> get() = _favorites.asStateFlow()

    fun loadExperiences() {
        viewModelScope.launch {
            try {
                val experienceList = repository.getExperiences()
                _experiences.value = experienceList
            } catch (e: Exception) {
                println("Error loading experiences: ${e.message}")
            }
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                _favorites.value = repository.getFavorites()
            } catch (e: Exception) {
                println("Error loading favorites: ${e.message}")
            }
        }
    }

    fun toggleFavorite(experience: Experience) {
        viewModelScope.launch {
            val isCurrentlyFavorite = repository.isFavorite(experience.id)

            if (isCurrentlyFavorite) {
                repository.removeFavorite(experience)
            } else {
                repository.addFavorite(experience)
            }

            experience.isFavorite = !isCurrentlyFavorite

            _experiences.value = _experiences.value.map { exp ->
                if (exp.id == experience.id) {
                    exp.copy(isFavorite = experience.isFavorite)
                } else {
                    exp
                }
            }

            loadFavorites()
        }
    }

    fun loadExperiencesFromApi(apiExperiences: List<ExperienceDto>) {
        viewModelScope.launch {
            val experienceList = apiExperiences.map { dto ->
                val domain = dto.toDomain()
                domain.isFavorite = repository.isFavorite(domain.id)
                domain
            }
            _experiences.value = experienceList
        }
    }
}
