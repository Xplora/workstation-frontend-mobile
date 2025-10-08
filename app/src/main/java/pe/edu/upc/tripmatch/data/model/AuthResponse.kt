package pe.edu.upc.tripmatch.data.model

data class AuthResponse(
    val token: String,
    val email: String,
    val rol: String,
    val id: String
)