package pe.edu.upc.tripmatch.data.remote

import android.content.Context
import okhttp3.OkHttpClient
import pe.edu.upc.tripmatch.common.ApiConstants
import pe.edu.upc.tripmatch.data.model.Category
import retrofit2.http.GET

interface CategoryService {
    @GET("api/v1/design/Category")
    suspend fun getAllCategories(): List<Category>

    companion object {
        fun create(context: Context): CategoryService {

            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context))
                .build()

            val retrofit = retrofit2.Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .client(client)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()
            return retrofit.create(CategoryService::class.java)
        }
    }
}