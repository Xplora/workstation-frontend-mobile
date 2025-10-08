package pe.edu.upc.tripmatch.data.remote

import pe.edu.upc.tripmatch.common.ApiConstants
import pe.edu.upc.tripmatch.data.model.AuthResponse
import pe.edu.upc.tripmatch.data.model.SignInCommand
import pe.edu.upc.tripmatch.data.model.SignUpCommand
import pe.edu.upc.tripmatch.data.model.SignUpSuccessResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/v1/iam/auth/signin")
    suspend fun signIn(@Body command: SignInCommand): AuthResponse

    @POST("api/v1/iam/auth/signup")
    suspend fun signUp(@Body command: SignUpCommand): SignUpSuccessResponse

    companion object {
        fun create(): AuthService {
            val retrofit = retrofit2.Retrofit.Builder()
                .baseUrl(ApiConstants.BASE_URL)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build()
            return retrofit.create(AuthService::class.java)
        }
    }
}