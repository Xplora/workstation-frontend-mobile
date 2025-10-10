package pe.edu.upc.tripmatch.data.remote

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response
import pe.edu.upc.tripmatch.data.repository.AuthRepository

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val authRepository = AuthRepository(null, context)
        val token = authRepository.getCurrentUser()?.token

        if (!token.isNullOrEmpty()) {
            val requestWithToken = originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
            return chain.proceed(requestWithToken)
        }

        return chain.proceed(originalRequest)
    }
}