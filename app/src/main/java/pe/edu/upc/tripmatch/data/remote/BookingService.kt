package pe.edu.upc.tripmatch.data.remote

import pe.edu.upc.tripmatch.data.model.BookingDto
import retrofit2.http.GET
import retrofit2.http.Header

interface BookingService {
    @GET("api/v1/assets/booking")
    suspend fun getAllBookings(@Header("Authorization") token: String): List<BookingDto>
}