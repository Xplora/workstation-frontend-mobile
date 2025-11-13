package pe.edu.upc.tripmatch.data.model

data class InquiryDto(
    val id: Int,
    val experienceId: Int,
    val experienceTitle: String,
    val userId: String,
    val travelerName: String,
    val travelerAvatarUrl: String?,
    val question: String,
    val askedAt: String,
    val response: ResponseDto?
)