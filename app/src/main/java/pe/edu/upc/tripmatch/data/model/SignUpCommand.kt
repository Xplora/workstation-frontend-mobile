package pe.edu.upc.tripmatch.data.model

data class SignUpCommand(
    val firstName: String,
    val lastName: String,
    val number: String,
    val email: String,
    val password: String,
    val rol: String,
    val agencyName: String? = null,
    val ruc: String? = null
)
