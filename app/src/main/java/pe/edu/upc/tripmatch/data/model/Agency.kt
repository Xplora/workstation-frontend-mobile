package pe.edu.upc.tripmatch.data.model

data class Agency(
    val agencyName: String,
    val avatarUrl: String,
    val contactEmail: String,
    val contactPhone: String,
    val description: String,
    val rating: Int,
    val reservationCount: Int,
    val reviewCount: Int,
    val ruc: String,
    val socialLinkFacebook: String,
    val socialLinkInstagram: String,
    val socialLinkWhatsapp: String,
    val userId: String
)