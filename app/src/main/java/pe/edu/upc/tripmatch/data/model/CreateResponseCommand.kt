package pe.edu.upc.tripmatch.data.model

data class CreateResponseCommand(
    val inquiryId: Int,
    val responderId: String,
    val answer: String,
    val answeredAt: String
)