package pe.edu.upc.tripmatch.data.model

import pe.edu.upc.tripmatch.domain.model.Query

object InquiryMapper {
    fun InquiryDto.toDomain(): Query {
        return Query(
            id = this.id,
            travelerName = travelerName ?: "Unknown",
            travelerAvatarUrl = this.travelerAvatarUrl,
            experienceTitle = this.experienceTitle?: "Titulo desconocido",
            question = this.question,
            askedAt = this.askedAt,
            isAnswered = this.response != null,
            answer = this.response?.answer,
            answeredAt = this.response?.answeredAt
        )
    }
}