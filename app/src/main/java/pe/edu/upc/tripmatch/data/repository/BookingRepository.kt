package pe.edu.upc.tripmatch.data.repository

import android.util.Log
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.async
import pe.edu.upc.tripmatch.data.model.ExperienceSummaryDto
import pe.edu.upc.tripmatch.data.model.UserDetailsDto
import pe.edu.upc.tripmatch.data.model.toDomain
import pe.edu.upc.tripmatch.data.remote.AgencyService
import pe.edu.upc.tripmatch.domain.model.Booking
import pe.edu.upc.tripmatch.domain.repository.BookingRepository

class BookingRepositoryImpl(
    private val agencyService: AgencyService
) : BookingRepository {

    override suspend fun getBookingsByAgency(agencyId: String, token: String): List<Booking> {
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

                    bookingDto.toDomain(
                        traveler = travelerDetails,
                        experience = experienceDetails ?: ExperienceSummaryDto(0, "Experiencia Desconocida", 0.0)
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("BookingRepository", "Error cargando las reservas de la agencia", e)
            throw e
        }
    }
}