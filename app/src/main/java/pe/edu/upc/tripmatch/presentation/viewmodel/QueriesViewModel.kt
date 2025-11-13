package pe.edu.upc.tripmatch.presentation.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upc.tripmatch.data.model.CreateResponseCommand
import pe.edu.upc.tripmatch.data.repository.InquiryRepository
import pe.edu.upc.tripmatch.domain.model.Query
import java.time.Instant
import retrofit2.HttpException
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay

data class QueriesUiState(
    val inquiries: List<Query> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val successMessage: String? = null,
    val showResponseDialog: Boolean = false,
    val selectedQuery: Query? = null,
    val responseText: String = "",
    val isSendingResponse: Boolean = false
)

class QueriesViewModel(
    private val repository: InquiryRepository,
    private val authViewModel: AuthViewModel
) : ViewModel() {

    private val _uiState = MutableStateFlow(QueriesUiState())
    val uiState = _uiState.asStateFlow()

    private val TAG = "QueriesViewModel"

    init {
        Log.d(TAG, "ViewModel inicializado. Iniciando carga de consultas...")
        loadAgencyInquiries()
    }

    fun loadAgencyInquiries() {
        if (_uiState.value.isLoading) {
            Log.d(TAG, "Carga ya en progreso, omitiendo.")
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            try {
                val agencyId = authViewModel.uiState.value.currentUser?.id
                Log.d(TAG, "ID de agencia obtenido: $agencyId")

                if (agencyId.isNullOrBlank()) {
                    Log.e(TAG, "Error: ID de agencia es nulo o vacío.")
                    _uiState.update {
                        it.copy(
                            error = "No se encontró el ID de la agencia. Verifica la sesión.",
                            isLoading = false
                        )
                    }
                    return@launch
                }

                val inquiries = repository.getAgencyInquiries(agencyId)
                Log.d(TAG, "Consultas recibidas: ${inquiries.size}")
                inquiries.forEach {
                    Log.d(TAG, "Inquiry ${it.id} -> isAnswered = ${it.isAnswered} (Respuesta: ${it.answer})")
                }

                _uiState.update {
                    it.copy(
                        inquiries = inquiries,
                        isLoading = false
                    )
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error al cargar consultas: ${e.message}", e)
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
                responseText = "",
                isSendingResponse = false
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

        if (query == null) {
            Log.w(TAG, "sendResponse() llamado sin consulta seleccionada.")
            return
        }

        if (agencyId.isNullOrBlank()) {
            Log.e(TAG, "sendResponse() sin agencyId. Revisa el AuthViewModel.")
            _uiState.update {
                it.copy(
                    error = "No se encontró el ID de la agencia. Vuelve a iniciar sesión.",
                    isSendingResponse = false
                )
            }
            return
        }

        viewModelScope.launch {
            Log.d(TAG, "sendResponse() iniciado. inquiryId=${query.id}, agencyId=$agencyId")

            _uiState.update {
                it.copy(
                    isSendingResponse = true,
                    error = null,
                    successMessage = null
                )
            }

            try {
                val secureAnsweredAt = OffsetDateTime.now(ZoneOffset.UTC)
                    .minusMinutes(5)
                    .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)

                val command = CreateResponseCommand(
                    inquiryId = query.id,
                    responderId = agencyId,
                    answer = state.responseText,
                    answeredAt = secureAnsweredAt
                )

                Log.d(TAG, "Payload a enviar: $command")
                repository.sendResponse(command)

                Log.d(TAG, "Respuesta OK del backend. ESPERANDO 1 SEGUNDO...")
                delay(1000)
                Log.d(TAG, "Espera terminada. Refrescando consultas...")

                val refreshedInquiries = repository.getAgencyInquiries(agencyId)

                _uiState.update {
                    it.copy(
                        inquiries = refreshedInquiries,
                        isSendingResponse = false,
                        showResponseDialog = false,
                        successMessage = "Respuesta enviada correctamente.",
                        selectedQuery = null,
                        responseText = ""
                    )
                }

            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e(
                    TAG,
                    "HTTP ${e.code()} al enviar respuesta. Body: $errorBody",
                    e
                )

                _uiState.update {
                    it.copy(
                        isSendingResponse = false,
                        error = "Error al enviar la respuesta (${e.code()}): ${errorBody ?: e.message()}"
                    )
                }

            } catch (e: Exception) {
                Log.e(TAG, "Error inesperado al enviar la respuesta: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        isSendingResponse = false,
                        error = "Error inesperado al enviar la respuesta: ${e.message}"
                    )
                }
            }
        }
    }


    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, error = null) }
    }
}