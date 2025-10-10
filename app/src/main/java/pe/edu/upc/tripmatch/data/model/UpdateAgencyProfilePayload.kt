package pe.edu.upc.tripmatch.data.model

data class UpdateAgencyProfilePayload(
    val agencyName: String,
    val ruc: String?,
    val description: String?,
    val avatarUrl: String?,
    val contactEmail: String?,
    val contactPhone: String?,
    val socialLinkFacebook: String?,
    val socialLinkInstagram: String?,
    val socialLinkWhatsapp: String?
)