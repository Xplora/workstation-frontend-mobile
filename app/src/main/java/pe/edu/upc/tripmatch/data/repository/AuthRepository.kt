package pe.edu.upc.tripmatch.data.repository

import android.content.Context
import android.content.SharedPreferences
import pe.edu.upc.tripmatch.data.model.SignInCommand
import pe.edu.upc.tripmatch.data.model.SignUpCommand
import pe.edu.upc.tripmatch.data.remote.AuthService
import pe.edu.upc.tripmatch.domain.model.User

class AuthRepository(
    private val authService: AuthService?,
    context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_TOKEN = "auth_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_ROLE = "user_role"
        const val KEY_USER_EMAIL = "user_email"
    }

    suspend fun signIn(command: SignInCommand): User {
        val service = authService ?: throw IllegalStateException("AuthService no disponible para signIn.")
        val response = service.signIn(command)

        val user = User(
            id = response.id,
            email = response.email,
            role = response.rol,
            token = response.token
        )

        saveAuthData(user)
        return user
    }

    suspend fun signUp(command: SignUpCommand) {
        val service = authService ?: throw IllegalStateException("AuthService no disponible para signUp.")
        service.signUp(command)
    }

    private fun saveAuthData(user: User) {
        prefs.edit().apply {
            putString(KEY_TOKEN, user.token)
            putString(KEY_USER_ID, user.id)
            putString(KEY_USER_ROLE, user.role)
            putString(KEY_USER_EMAIL, user.email)
            apply()
        }
    }

    fun getCurrentUser(): User? {
        val token = prefs.getString(KEY_TOKEN, null)
        val id = prefs.getString(KEY_USER_ID, null)
        val role = prefs.getString(KEY_USER_ROLE, null)
        val email = prefs.getString(KEY_USER_EMAIL, null)

        return if (!token.isNullOrEmpty() && id != null && role != null && email != null) {
            User(id, email, role, token)
        } else {
            null
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}