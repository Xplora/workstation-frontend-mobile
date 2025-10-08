package pe.edu.upc.tripmatch.data.remote
import pe.edu.upc.tripmatch.data.model.ExperienceDto
import retrofit2.http.GET

interface ExperienceService {
    @GET("api/v1/design/Experience")
    suspend fun getExperiences(): List<ExperienceDto>

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