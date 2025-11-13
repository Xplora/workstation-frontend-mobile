package pe.edu.upc.tripmatch.data.remote

import android.content.Context
import okhttp3.OkHttpClient
import pe.edu.upc.tripmatch.common.ApiConstants
import pe.edu.upc.tripmatch.data.model.CreateResponseCommand
import pe.edu.upc.tripmatch.data.model.InquiryDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface InquiryService {
    @GET("api/v1/inquiry/agency/{agencyId}")
    suspend fun getAgencyInquiries(
        @retrofit2.http.Path("agencyId") agencyId: String
    ): List<InquiryDto>

    @POST("api/v1/Response")
    suspend fun createResponse(@Body command: CreateResponseCommand): Response<Unit>

    companion object {
        fun create(context: Context): InquiryService {

            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .build()

            val retrofit = retrofit2.Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(client)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()
            return retrofit.create(InquiryService::class.java)
        }
    }
}