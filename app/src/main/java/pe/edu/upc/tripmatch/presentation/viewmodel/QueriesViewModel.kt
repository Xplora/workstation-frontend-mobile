package pe.edu.upc.tripmatch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upc.tripmatch.data.model.CreateResponseCommand
import pe.edu.upc.tripmatch.data.repository.InquiryRepository
import pe.edu.upc.tripmatch.domain.model.Query

data class QueriesUiState(
    val inquiries: List<Query> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,

    val showResponseDialog: Boolean = false,
    val selectedQuery: Query? = null,
    val responseText: String = ""
)

class QueriesViewModel(
    private val repository: InquiryRepository,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(QueriesUiState())
    val uiState = _uiState.asStateFlow()

    fun loadAgencyInquiries() {
        if (_uiState.value.isLoading) return

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val agencyId = authViewModel.uiState.value.currentUser?.id

                if (agencyId.isNullOrBlank()) {
                    _uiState.update {
                        it.copy(
                            error = "No se encontró el ID de la agencia. Verifica la sesión.",
                            isLoading = false
                        )
                    }
                    return@launch
                }

                val inquiries = repository.getAgencyInquiries(agencyId)
                _uiState.update { it.copy(inquiries = inquiries, isLoading = false) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = "Error al cargar consultas: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }


    fun openResponseDialog(query: Query) {
        _uiState.update {
            it.copy(
                showResponseDialog = true,
                selectedQuery = query,
                responseText = if (query.isAnswered) query.answer ?: "" else ""
            )
        }
    }

    fun dismissResponseDialog() {
        _uiState.update {
            it.copy(
                showResponseDialog = false,
                selectedQuery = null,
                responseText = ""
            )
        }
    }

    fun updateResponseText(text: String) {
        _uiState.update { it.copy(responseText = text) }
    }

    fun sendResponse() {
        val state = _uiState.value
        val query = state.selectedQuery
        val agencyId = authViewModel.uiState.value.currentUser?.id

        if (query == null || agencyId.isNullOrBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val command = CreateResponseCommand(
                    inquiryId = query.id,
                    responderId = agencyId,
                    answer = state.responseText,
                    answeredAt = java.time.LocalDateTime.now().toString()
                )

                repository.sendResponse(command)

                val refreshed = repository.getAgencyInquiries(agencyId)

                _uiState.update {
                    it.copy(
                        inquiries = refreshed,
                        isLoading = false,
                        showResponseDialog = false,
                        successMessage = "Respuesta enviada correctamente.",
                        selectedQuery = null,
                        responseText = ""
                    )
                }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al enviar la respuesta: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }
}