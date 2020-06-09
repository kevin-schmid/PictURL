package at.fhjoanneum.picturl.service

import okhttp3.Interceptor
import okhttp3.Response

class OAuthInterceptor(private val tokenType: String, private val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request().newBuilder()
            .header("Authorization", "$tokenType $token")
            .build()
        )
    }
}