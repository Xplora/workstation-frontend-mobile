package pe.edu.upc.tripmatch.data.model

data class InquiryDto(
    val id: Int,
    val experienceId: Int,
    val userId: String,
    val travelerName: String,
    val travelerAvatarUrl: String?,
    val experienceTitle: String,
    val question: String,
    val askedAt: String,
    val answer: String?,
    val isAnswered: Boolean
)
