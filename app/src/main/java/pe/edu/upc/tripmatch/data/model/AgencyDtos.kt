package pe.edu.upc.tripmatch.data.model

data class AgencyProfileDto(
    val agencyName: String,
    val ruc: String?,
    val description: String?,
    val avatarUrl: String?
)

data class ReviewDto(
    val id: Int,
    val touristUserId: String,
    val agencyUserId: String,
    val comment: String,
    val rating: Double
)

data class InquiryDto(
    val id: Int,
    val isAnswered: Boolean? = null
)

data class BookingDto(
    val id: Int,
    val touristId: String,
    val experienceId: Int,
    val price: Double,
    val bookingDate: String,
    val numberOfPeople: Int
)

data class ExperienceSummaryDto(
    val id: Int,
    val title: String,
    val price: Double
)

data class UserDetailsDto(
    val firstName: String,
    val lastName: String,
    val number: String?,
    val avatarUrl: String?
)