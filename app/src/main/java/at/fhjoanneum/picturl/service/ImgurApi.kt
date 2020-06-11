package at.fhjoanneum.picturl.service

import at.fhjoanneum.picturl.BuildConfig
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface ImgurApi {
    @Multipart
    @POST("upload")
    suspend fun postImage(
        @Part image: MultipartBody.Part,
        @Part title: MultipartBody.Part
    ): PostImageResponse

    @DELETE("image/{hash}")
    suspend fun deleteImage(@Path("hash") deleteHash: String)

    companion object {
        fun createService(): ImgurApi {
            if (!BuildConfig.IS_PRODUCTION) {
                return ImgurApiSimulation
            }
            return Retrofit.Builder()
                .baseUrl("https://api.imgur.com/3/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(
                    OkHttpClient.Builder()
                        .addInterceptor(OAuthInterceptor("Client-ID", BuildConfig.CLIENT_ID))
                        .build()
                )
                .build()
                .create(ImgurApi::class.java)
        }
    }
}