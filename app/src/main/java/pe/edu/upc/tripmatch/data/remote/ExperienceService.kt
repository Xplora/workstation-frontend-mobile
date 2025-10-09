package pe.edu.upc.tripmatch.data.remote

import pe.edu.upc.tripmatch.data.model.ExperienceDto
import retrofit2.http.GET
import retrofit2.http.Path

interface ExperienceService {

    @GET("api/v1/design/Experience")
    suspend fun getExperiences(): List<ExperienceDto>

    @GET("api/v1/design/experience/agency/{agencyUserId}")
    suspend fun getExperiencesByAgencyId(@Path("agencyUserId") agencyUserId: String): List<ExperienceDto>

    companion object {
        fun create(): ExperienceService {
            val retrofit = retrofit2.Retrofit.Builder()
                .baseUrl(pe.edu.upc.tripmatch.common.ApiConstants.BASE_URL)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()
            return retrofit.create(ExperienceService::class.java)
        }
    }
}