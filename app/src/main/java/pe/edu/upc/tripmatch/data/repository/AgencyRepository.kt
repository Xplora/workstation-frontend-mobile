package pe.edu.upc.tripmatch.data.repository

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import pe.edu.upc.tripmatch.data.model.AgencyProfileDto
import pe.edu.upc.tripmatch.data.model.UpdateAgencyProfilePayload
import pe.edu.upc.tripmatch.data.model.UserDetailsDto
import pe.edu.upc.tripmatch.data.remote.AgencyService
import pe.edu.upc.tripmatch.presentation.viewmodel.AgencyStatsUi
import pe.edu.upc.tripmatch.presentation.viewmodel.BookingUi
import pe.edu.upc.tripmatch.presentation.viewmodel.ReviewUi
import retrofit2.HttpException
import java.text.NumberFormat
import java.util.Locale

data class AgencyDashboardData(
    val agencyName: String,
    val stats: AgencyStatsUi,
    val recentBookings: List<BookingUi>,
    val recentReviews: List<ReviewUi>
)

class AgencyRepository(private val agencyService: AgencyService) {

    private fun isNotFoundException(e: Throwable) = e is HttpException && e.code() == 404

    suspend fun getAgencyProfile(userId: String, token: String): AgencyProfileDto {
        return try {
            val bearerToken = "Bearer $token"
            agencyService.getAgencyProfile(userId, bearerToken)
        } catch (e: Exception) {
            if (isNotFoundException(e)) {
                AgencyProfileDto("Nueva Agencia", null, "Completa tu descripción...", null, null, null, null, null, null)
            } else {
                throw e
            }
        }
    }

    suspend fun getDashboardData(agencyId: String, token: String): AgencyDashboardData {
        val bearerToken = "Bearer $token"
        return try {
            coroutineScope {
                val profileDeferred = async { getAgencyProfile(agencyId, token) }
                val reviewsDeferred = async {
                    try { agencyService.getReviewsByAgencyId(agencyId, bearerToken) } catch (e: Exception) { if(isNotFoundException(e)) emptyList() else throw e }
                }
                val inquiriesDeferred = async {
                    try { agencyService.getAllInquiries(bearerToken) } catch (e: Exception) { if(isNotFoundException(e)) emptyList() else throw e }
                }
                val bookingsDeferred = async {
                    try { agencyService.getAllBookings(bearerToken) } catch (e: Exception) { if(isNotFoundException(e)) emptyList() else throw e }
                }
                val experiencesDeferred = async {
                    try { agencyService.getExperiencesByAgencyId(agencyId, bearerToken) } catch (e: Exception) { if(isNotFoundException(e)) emptyList() else throw e }
                }

                val agencyProfile = profileDeferred.await()
                val allReviewsDto = reviewsDeferred.await()
                val allInquiries = inquiriesDeferred.await()
                val allBookingsDto = bookingsDeferred.await()
                val agencyExperiences = experiencesDeferred.await()
                val agencyExperienceIds = agencyExperiences.map { it.id }.toSet()
                val agencyBookingsDto = allBookingsDto.filter { agencyExperienceIds.contains(it.experienceId) }
                val newQueries = allInquiries.count { it.isAnswered == false }
                val totalEarnings = agencyBookingsDto.sumOf { it.price }
                val formattedEarnings = NumberFormat.getCurrencyInstance(Locale("es", "PE")).format(totalEarnings)

                val stats = AgencyStatsUi(
                    confirmedBookings = agencyBookingsDto.size,
                    newQueries = newQueries,
                    totalExperiences = agencyExperiences.size,
                    totalEarnings = formattedEarnings
                )

                val mappedReviews = allReviewsDto.take(5).map { reviewDto ->
                    val touristDetails = try {
                        agencyService.getUserDetails(reviewDto.touristUserId, bearerToken)
                    } catch (e: Exception) {
                        UserDetailsDto(
                            "Viajero",
                            "Anónimo",
                            null,
                            null
                        )
                    }
                    ReviewUi(
                        author = "${touristDetails.firstName} ${touristDetails.lastName}",
                        comment = reviewDto.comment,
                        rating = reviewDto.rating.toInt()
                    )
                }

                val mappedBookings = agencyBookingsDto.take(5).map { bookingDto ->
                    val travelerDetails = try {
                        agencyService.getUserDetails(bookingDto.touristId, bearerToken)
                    } catch (e: Exception) {
                        UserDetailsDto("Viajero", "Anónimo", null, null)
                    }
                    val experienceDetails = agencyExperiences.find { it.id == bookingDto.experienceId }

                    BookingUi(
                        traveler = "${travelerDetails.firstName} ${travelerDetails.lastName}",
                        experience = experienceDetails?.title ?: "Experiencia Desconocida",
                        date = bookingDto.bookingDate,
                        status = "Confirmada"
                    )
                }

                AgencyDashboardData(
                    agencyName = agencyProfile.agencyName,
                    stats = stats,
                    recentBookings = mappedBookings,
                    recentReviews = mappedReviews
                )
            }
        } catch (e: Exception) {
            Log.e("AgencyRepository", "Error cargando datos del dashboard", e)
            if (isNotFoundException(e)) {
                AgencyDashboardData("Agencia", AgencyStatsUi(), emptyList(), emptyList())
            } else {
                throw e
            }
        }
    }
    suspend fun getBookingsForAgency(agencyId: String, token: String): List<pe.edu.upc.tripmatch.domain.model.Booking> {
        val bearerToken = "Bearer $token"
        return try {
            coroutineScope {
                val bookingsDeferred = async { agencyService.getAllBookings(bearerToken) }
                val experiencesDeferred = async { agencyService.getExperiencesByAgencyId(agencyId, bearerToken) }

                val allBookingsDto = bookingsDeferred.await()
                val agencyExperiences = experiencesDeferred.await()
                val agencyExperienceIds = agencyExperiences.map { it.id }.toSet()

                val agencyBookingsDto = allBookingsDto.filter { agencyExperienceIds.contains(it.experienceId) }

                agencyBookingsDto.map { bookingDto ->
                    val travelerDetails = try {
                        agencyService.getUserDetails(bookingDto.touristId, bearerToken)
                    } catch (e: Exception) {
                        UserDetailsDto("Viajero", "Anónimo", null, null)
                    }
                    val experienceDetails = agencyExperiences.find { it.id == bookingDto.experienceId }

                    pe.edu.upc.tripmatch.domain.model.Booking(
                        travelerName = "${travelerDetails.firstName} ${travelerDetails.lastName}",
                        travelerImage = travelerDetails.avatarUrl,
                        experienceName = experienceDetails?.title ?: "Experiencia Desconocida",
                        date = bookingDto.bookingDate,
                        people = bookingDto.numberOfPeople,
                        totalPaid = bookingDto.price
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("AgencyRepository", "Error cargando las reservas", e)
            throw e
        }
    }
    suspend fun updateProfile(userId: String, token: String, payload: UpdateAgencyProfilePayload): AgencyProfileDto {
        val bearerToken = "Bearer $token"
        return agencyService.updateAgencyProfile(userId, bearerToken, payload)
    }
}