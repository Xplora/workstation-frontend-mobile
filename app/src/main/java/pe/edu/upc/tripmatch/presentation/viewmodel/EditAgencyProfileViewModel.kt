package pe.edu.upc.tripmatch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upc.tripmatch.data.model.UpdateAgencyProfilePayload
import pe.edu.upc.tripmatch.data.repository.AgencyRepository


data class EditProfileUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false,

    val agencyName: String = "",
    val ruc: String = "",
    val description: String = "",
    val avatarUrl: String = "",
    val contactEmail: String = "",
    val contactPhone: String = "",
    val facebookUrl: String = "",
    val instagramUrl: String = "",
    val whatsappUrl: String = ""
)

class EditAgencyProfileViewModel(
    private val agencyRepository: AgencyRepository,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val currentUser = authViewModel.uiState.value.currentUser
            if (currentUser != null) {
                try {
                    val profile = agencyRepository.getAgencyProfile(currentUser.id, currentUser.token)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            agencyName = profile.agencyName,
                            ruc = profile.ruc ?: "",
                            description = profile.description ?: "",
                            avatarUrl = profile.avatarUrl ?: "",
                            contactEmail = profile.contactEmail ?: "",
                            contactPhone = profile.contactPhone ?: "",
                            facebookUrl = profile.socialLinkFacebook ?: "",
                            instagramUrl = profile.socialLinkInstagram ?: "",
                            whatsappUrl = profile.socialLinkWhatsapp ?: ""
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to load data: ${e.message}") }
                }
            }
        }
    }
    fun resetSaveSuccessFlag() {
        _uiState.update { it.copy(saveSuccess = false) }
    }
    fun onFieldChange(field: String, value: String) {
        _uiState.update {
            when (field) {
                "name" -> it.copy(agencyName = value)
                "ruc" -> it.copy(ruc = value)
                "description" -> it.copy(description = value)
                "avatar" -> it.copy(avatarUrl = value)
                "email" -> it.copy(contactEmail = value)
                "phone" -> it.copy(contactPhone = value)
                "facebook" -> it.copy(facebookUrl = value)
                "instagram" -> it.copy(instagramUrl = value)
                "whatsapp" -> it.copy(whatsappUrl = value)
                else -> it
            }
        }
    }

    fun saveChanges() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val state = _uiState.value
            val currentUser = authViewModel.uiState.value.currentUser

            if (currentUser != null) {
                val payload = UpdateAgencyProfilePayload(
                    agencyName = state.agencyName,
                    ruc = state.ruc.ifEmpty { null },
                    description = state.description.ifEmpty { null },
                    avatarUrl = state.avatarUrl.ifEmpty { null },
                    contactEmail = state.contactEmail.ifEmpty { null },
                    contactPhone = state.contactPhone.ifEmpty { null },
                    socialLinkFacebook = state.facebookUrl.ifEmpty { null },
                    socialLinkInstagram = state.instagramUrl.ifEmpty { null },
                    socialLinkWhatsapp = state.whatsappUrl.ifEmpty { null }
                )
                try {
                    agencyRepository.updateProfile(currentUser.id, currentUser.token, payload)
                    _uiState.update { it.copy(isSaving = false, saveSuccess = true) }
                } catch (e: Exception) {
                    _uiState.update { it.copy(isSaving = false, errorMessage = "Error saving: ${e.message}") }
                }
            } else {
                _uiState.update { it.copy(isSaving = false, errorMessage = "Authentication error.") }
            }
        }
    }
}