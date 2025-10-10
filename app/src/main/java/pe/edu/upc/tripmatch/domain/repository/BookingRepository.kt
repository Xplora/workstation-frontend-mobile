package pe.edu.upc.tripmatch.domain.repository

import pe.edu.upc.tripmatch.domain.model.Booking

interface BookingRepository {
    suspend fun getBookingsByAgency(agencyId: String, token: String): List<Booking>
}