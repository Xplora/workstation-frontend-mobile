package pe.edu.upc.tripmatch.data.repository

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import pe.edu.upc.tripmatch.data.remote.AgencyService
import pe.edu.upc.tripmatch.presentation.viewmodel.AgencyStatsUi
import pe.edu.upc.tripmatch.presentation.viewmodel.BookingUi
import pe.edu.upc.tripmatch.presentation.viewmodel.ReviewUi
import java.text.NumberFormat
import java.util.Locale

data class AgencyDashboardData(
    val agencyName: String,
    val stats: AgencyStatsUi,
    val recentBookings: List<BookingUi>,
    val recentReviews: List<ReviewUi>
)

class AgencyRepository(private val agencyService: AgencyService) {

    suspend fun getDashboardData(agencyId: String, token: String): AgencyDashboardData = coroutineScope {
        val bearerToken = "Bearer $token"

        val profileDeferred = async { agencyService.getAgencyProfile(agencyId, bearerToken) }
        val reviewsDeferred = async { agencyService.getReviewsByAgencyId(agencyId, bearerToken) }
        val inquiriesDeferred = async {
            try {
                agencyService.getAllInquiries(bearerToken)
            } catch (e: Exception) {
                Log.w("AgencyRepository", "La llamada a /inquiry falló con 404. Se asumirá 0 consultas. Error: ${e.message}")
                emptyList()
            }
        }

        val bookingsDeferred = async { agencyService.getAllBookings(bearerToken) }
        val experiencesDeferred = async { agencyService.getExperiencesByAgencyId(agencyId, bearerToken) }

        val agencyProfile = profileDeferred.await()
        val allReviews = reviewsDeferred.await()
        val allInquiries = inquiriesDeferred.await()
        val allBookings = bookingsDeferred.await()
        val agencyExperiences = experiencesDeferred.await()

        val agencyExperienceIds = agencyExperiences.map { it.id }.toSet()
        val agencyBookings = allBookings.filter { agencyExperienceIds.contains(it.experienceId) }

        val newQueries = allInquiries.count { it.isAnswered == false }
        val totalEarnings = agencyBookings.sumOf { it.price }
        val formattedEarnings = NumberFormat.getCurrencyInstance(Locale("es", "PE")).format(totalEarnings)

        val stats = AgencyStatsUi(
            confirmedBookings = agencyBookings.size,
            newQueries = newQueries,
            totalExperiences = agencyExperiences.size,
            totalEarnings = formattedEarnings
        )

        val recentReviews = allReviews.take(2).map { review ->
            async {
                val touristDetails = agencyService.getUserDetails(review.touristUserId, bearerToken)
                ReviewUi(
                    author = "${touristDetails.firstName} ${touristDetails.lastName}".trim(),
                    comment = review.comment,
                    rating = review.rating.toInt()
                )
            }
        }.awaitAll()

        val recentBookings = agencyBookings.take(2).map { booking ->
            async {
                val touristDetails = agencyService.getUserDetails(booking.touristId, bearerToken)
                val experience = agencyExperiences.find { it.id == booking.experienceId }
                BookingUi(
                    traveler = "${touristDetails.firstName} ${touristDetails.lastName}".trim(),
                    experience = experience?.title ?: "Experiencia Desconocida",
                    date = "N/A",
                    status = "Confirmado"
                )
            }
        }.awaitAll()

        return@coroutineScope AgencyDashboardData(
            agencyName = agencyProfile.agencyName,
            stats = stats,
            recentBookings = recentBookings,
            recentReviews = recentReviews
        )
    }
}