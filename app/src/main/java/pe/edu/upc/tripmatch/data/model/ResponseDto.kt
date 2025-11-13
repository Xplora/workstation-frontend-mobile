package pe.edu.upc.tripmatch.data.model

data class ResponseDto(
    val id: Int,
    val answer: String,
    val answeredAt: String,
    val responderId: String
)