package pe.edu.upc.tripmatch.domain.model

data class Booking(
    val travelerName: String,
    val travelerImage: String?,
    val experienceName: String,
    val date: String,
    val people: Int,
    val totalPaid: Double
)