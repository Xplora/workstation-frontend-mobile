package pe.edu.upc.tripmatch.domain.model

data class User(
    val id: String,
    val email: String,
    val role: String, // "tourist" o "agency"
    val token: String,
    val firstName: String? = null,
    val lastName: String? = null,
    val agencyName: String? = null
)
