package pe.edu.upc.tripmatch.data.remote

import android.content.Context
import okhttp3.OkHttpClient
import pe.edu.upc.tripmatch.common.ApiConstants
import pe.edu.upc.tripmatch.data.model.CreateExperienceCommand
import pe.edu.upc.tripmatch.data.model.ExperienceDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ExperienceService {

    @GET("api/v1/design/Experience")
    suspend fun getExperiences(): List<ExperienceDto>

    @GET("api/v1/design/experience/agency/{agencyUserId}")
    suspend fun getExperiencesByAgencyId(@Path("agencyUserId") agencyUserId: String): List<ExperienceDto>

    @DELETE("api/v1/design/Experience/{id}")
    suspend fun deleteExperience(@Path("id") id: Int): Response<Unit>

    @POST("api/v1/design/Experience")
    suspend fun createExperience(@Body command: CreateExperienceCommand): Response<Unit>
    companion object {
        fun create(context: Context): ExperienceService {

            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .build()

            val retrofit = retrofit2.Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(client)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()
            return retrofit.create(ExperienceService::class.java)
        }
    }
}