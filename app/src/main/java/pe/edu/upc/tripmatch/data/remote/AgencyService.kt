package pe.edu.upc.tripmatch.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pe.edu.upc.tripmatch.common.ApiConstants
import pe.edu.upc.tripmatch.data.model.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.PUT

interface AgencyService {

    @GET("api/v1/profile/user/agency/{userId}")
    suspend fun getAgencyProfile(@Path("userId") userId: String, @Header("Authorization") token: String): AgencyProfileDto

    @GET("api/v1/user/review/agency/{agencyUserId}")
    suspend fun getReviewsByAgencyId(@Path("agencyUserId") agencyUserId: String, @Header("Authorization") token: String): List<ReviewDto>

    @GET("api/v1/inquiry")
    suspend fun getAllInquiries(@Header("Authorization") token: String): List<InquiryDto>

    @GET("api/v1/assets/booking")
    suspend fun getAllBookings(@Header("Authorization") token: String): List<BookingDto>

    @GET("api/v1/design/experience/agency/{agencyUserId}")
    suspend fun getExperiencesByAgencyId(@Path("agencyUserId") agencyUserId: String, @Header("Authorization") token: String): List<ExperienceSummaryDto>

    @GET("api/v1/profile/user/{userId}")
    suspend fun getUserDetails(@Path("userId") userId: String, @Header("Authorization") token: String): UserDetailsDto

    @PUT("api/v1/profile/user/agency/{userId}")
    suspend fun updateAgencyProfile(@Path("userId") userId: String, @Header("Authorization") token: String, @Body payload: UpdateAgencyProfilePayload): AgencyProfileDto

    companion object {
        fun create(): AgencyService {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            val retrofit = retrofit2.Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(client)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()
            return retrofit.create(AgencyService::class.java)
        }
    }
}