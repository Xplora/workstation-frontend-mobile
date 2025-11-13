package pe.edu.upc.tripmatch.data.model

import pe.edu.upc.tripmatch.domain.model.Query


object InquiryMapper {
    fun InquiryDto.toDomain(): Query {
        return Query(
            id = this.id,
            travelerName = this.travelerName.ifBlank { "Unknown" },
            travelerAvatarUrl = this.travelerAvatarUrl,
            experienceTitle = this.experienceTitle.ifBlank { "Título desconocido" },
            question = this.question,
            askedAt = this.askedAt,
            isAnswered = this.isAnswered,
            answer = this.answer,
            answeredAt = null
        )
    }
}
