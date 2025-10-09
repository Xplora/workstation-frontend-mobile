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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _event = MutableStateFlow<AuthEvent?>(null)
    val event: StateFlow<AuthEvent?> = _event.asStateFlow()

    fun setEmail(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null, successMessage = null) }
    }

    fun setPassword(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null, successMessage = null) }
    }

    fun setName(value: String) {
        _uiState.update { it.copy(name = value, errorMessage = null, successMessage = null) }
    }

    fun setNumber(value: String) {
        _uiState.update { it.copy(number = value, errorMessage = null, successMessage = null) }
    }

    fun setConfirmPassword(value: String) {
        _uiState.update { it.copy(confirmPassword = value, errorMessage = null, successMessage = null) }
    }

    fun setIsAgency(value: Boolean) {
        _uiState.update { it.copy(isAgency = value, errorMessage = null, successMessage = null) }
    }

    fun onSignIn() {
        val current = uiState.value
        if (current.isLoading) return
        val email = current.email.trim()
        val password = current.password
        if (email.isEmpty() || password.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Completa email y contraseña.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val user = repository.signIn(SignInCommand(email, password))
                _uiState.update { it.copy(isLoading = false, currentUser = user) }
                _event.value = AuthEvent.SignInSuccess
            } catch (e: Exception) {
                val msg = e.message ?: "Credenciales inválidas. Intente de nuevo."
                _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
            }
        }
    }

    fun onSignUp() {
        val state = uiState.value
        if (state.isLoading) return

        val name = state.name.trim()
        val email = state.email.trim()
        val number = state.number.trim()
        val password = state.password
        val confirm = state.confirmPassword

        when {
            name.isEmpty() || email.isEmpty() || number.isEmpty() -> {
                _uiState.update { it.copy(errorMessage = "Complete todos los campos obligatorios.") }
                return
            }
            password.length < 6 -> {
                _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres.") }
                return
            }
            password != confirm -> {
                _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden.") }
                return
            }
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null, successMessage = null) }

        viewModelScope.launch {
            try {
                val parts = name.split(' ', limit = 2)
                val firstName = parts.getOrElse(0) { "" }
                val lastName = parts.getOrElse(1) { "Apellido" }

                val command = SignUpCommand(
                    firstName = firstName,
                    lastName = lastName,
                    number = number,
                    email = email,
                    password = password,
                    rol = if (state.isAgency) "agency" else "tourist",
                    agencyName = if (state.isAgency) name else null,
                    ruc = if (state.isAgency) "12345678901" else null
                )

                repository.signUp(command)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Cuenta creada con éxito. Inicie sesión."
                    )
                }
                _event.value = AuthEvent.SignUpSuccess
            } catch (e: Exception) {
                val msg = e.message ?: "Error al registrar la cuenta."
                _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
            }
        }
    }

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
