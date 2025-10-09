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

// El AuthUiState no necesita cambios, está perfecto.
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val name: String = "",
    val number: String = "",
    val confirmPassword: String = "",
    val isAgency: Boolean = false,
    val role: String = "",
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

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _event = MutableStateFlow<AuthEvent?>(null)
    val event: StateFlow<AuthEvent?> = _event.asStateFlow()

    init {
        // Restaurar sesión al abrir la app
        viewModelScope.launch {
            val savedUser = repository.getCurrentUser()
            if (savedUser != null) {
                // Accedemos directamente a las propiedades del usuario, ¡sin reflexión!
                val isAgencyFlag = mapIsAgency(savedUser.role)
                val displayName = savedUser.agencyName ?: "${savedUser.firstName} ${savedUser.lastName}".trim()

                _uiState.update {
                    it.copy(
                        currentUser = savedUser,
                        role = savedUser.role,
                        isAgency = isAgencyFlag,
                        name = displayName,
                        email = savedUser.email // El email del usuario logueado
                    )
                }
            }
        }
    }

    // --- Setters de formulario (sin cambios, están bien) ---
    fun setEmail(value: String) { _uiState.update { it.copy(email = value, errorMessage = null, successMessage = null) } }
    fun setPassword(value: String) { _uiState.update { it.copy(password = value, errorMessage = null, successMessage = null) } }
    fun setName(value: String) { _uiState.update { it.copy(name = value, errorMessage = null, successMessage = null) } }
    fun setNumber(value: String) { _uiState.update { it.copy(number = value, errorMessage = null, successMessage = null) } }
    fun setConfirmPassword(value: String) { _uiState.update { it.copy(confirmPassword = value, errorMessage = null, successMessage = null) } }
    fun setIsAgency(value: Boolean) { _uiState.update { it.copy(isAgency = value, errorMessage = null, successMessage = null) } }

    // --- Acciones ---
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
                val isAgencyFlag = mapIsAgency(user.role)
                val displayName = user.agencyName ?: "${user.firstName} ${user.lastName}".trim()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        currentUser = user,
                        role = user.role,
                        isAgency = isAgencyFlag,
                        name = displayName,
                        email = "", // <-- SOLUCIÓN: Limpiamos el email del formulario
                        password = "", // <-- SOLUCIÓN: Limpiamos el password del formulario
                        errorMessage = null
                    )
                }
                _event.value = AuthEvent.SignInSuccess
            } catch (e: Exception) {
                val msg = e.message ?: "Credenciales inválidas. Intente de nuevo."
                _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
            }
        }
    }

    fun onSignUp() {
        // Tu lógica de onSignUp está bien, no necesita cambios mayores.
        // Solo asegúrate de limpiar los campos si lo deseas después del éxito.
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
                val desiredRole = if (state.isAgency) "agency" else "tourist"

                val command = SignUpCommand(
                    firstName = firstName,
                    lastName = lastName,
                    number = number,
                    email = email,
                    password = password,
                    rol = desiredRole,
                    agencyName = if (state.isAgency) name else null,
                    ruc = if (state.isAgency) "12345678901" else null
                )
                repository.signUp(command)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        successMessage = "Cuenta creada con éxito. Inicie sesión.",
                        // Opcional: limpiar campos tras registro exitoso
                        name = "", email = "", number = "", password = "", confirmPassword = ""
                    )
                }
                _event.value = AuthEvent.SignUpSuccess
            } catch (e: Exception) {
                val msg = e.message ?: "Error al registrar la cuenta."
                _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
            }
        }
    }

    fun logout() {
        repository.logout()
        // Resetea al estado inicial
        _uiState.value = AuthUiState()
    }

    // --- Limpieza de eventos y mensajes ---
    fun clearSuccessMessage() { _uiState.update { it.copy(successMessage = null) } }
    fun clearEvent() { _event.value = null }

    // --- Helpers simplificados ---
    private fun mapIsAgency(role: String?): Boolean {
        // Es más robusto y simple verificar si contiene la palabra clave
        return role?.contains("agency", ignoreCase = true) == true
    }

    // -----------------------------------------------------------
    // FUNCIONES ELIMINADAS:
    // - extractRole(user: Any?)
    // - extractName(user: Any?)
    // - extractEmail(user: Any?)
    // - tryGetString(target: Any, methodOrField: String)
    // Ya no son necesarias, hacemos acceso directo a las propiedades del objeto User.
    // -----------------------------------------------------------
}