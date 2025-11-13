package pe.edu.upc.tripmatch.domain.model

data class Query(
    val id: Int,
    val travelerName: String?=null,
    val travelerAvatarUrl: String?,
    val experienceTitle: String?,
    val question: String,
    val askedAt: String,
    val isAnswered: Boolean,
    val answer: String?,
    val answeredAt: String?
)