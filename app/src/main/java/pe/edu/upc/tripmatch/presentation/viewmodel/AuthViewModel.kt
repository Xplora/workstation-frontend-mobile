package pe.edu.upc.tripmatch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.edu.upc.tripmatch.data.model.SignInCommand
import pe.edu.upc.tripmatch.data.model.SignUpCommand
import pe.edu.upc.tripmatch.data.repository.AuthRepository
import pe.edu.upc.tripmatch.domain.model.User

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val number: String = "",
    val confirmPassword: String = "",
    val isAgency: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val currentUser: User? = null
)

sealed class AuthEvent {
    object SignUpSuccess : AuthEvent()
    object SignInSuccess : AuthEvent()
}

class AuthViewModel(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AuthUiState(
            currentUser = repository.getCurrentUser()
        )
    )
    val uiState: StateFlow<AuthUiState> get() = _uiState.asStateFlow()

    private val _event = MutableStateFlow<AuthEvent?>(null)
    val event: StateFlow<AuthEvent?> get() = _event.asStateFlow()

    fun onSignIn(email: String, password: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            try {
                val user = repository.signIn(SignInCommand(email.trim(), password))
                _event.value = AuthEvent.SignInSuccess
                _uiState.update { it.copy(isLoading = false, currentUser = user) }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Credenciales inválidas. Intente de nuevo."
                _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                println("Error during sign-in: $errorMsg")
            }
        }
    }

    fun onSignUp(state: AuthUiState) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }
        viewModelScope.launch {
            try {
                if (state.password != state.confirmPassword) {
                    throw Exception("Las contraseñas no coinciden.")
                }

                val parts = state.name.trim().split(' ', limit = 2)
                val firstName = parts.getOrElse(0) { "" }
                val lastName = parts.getOrElse(1) { "Apellido" }

                val command = SignUpCommand(
                    firstName = firstName,
                    lastName = lastName,
                    number = state.number.trim(),
                    email = state.email.trim(),
                    password = state.password,
                    rol = if (state.isAgency) "agency" else "tourist",
                    agencyName = if (state.isAgency) state.name.trim() else null,
                    ruc = if (state.isAgency) "12345678901" else null
                )

                repository.signUp(command)

                _event.value = AuthEvent.SignUpSuccess
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Cuenta creada con éxito. Inicie sesión.",
                        name = "", email = "", number = "", password = "", confirmPassword = ""
                    )
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Error al registrar la cuenta."
                _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
                println("Error during sign-up: $errorMsg")
            }
        }
    }

    /**
     * Limpia el mensaje de éxito después de que ha sido mostrado en la UI.
     */
    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }

    fun clearEvent() {
        _event.value = null
    }

    fun logout() {
        repository.logout()
        _uiState.update { it.copy(currentUser = null) }
    }
}
