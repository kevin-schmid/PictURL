package at.fhjoanneum.picturl.service

import at.fhjoanneum.picturl.BuildConfig
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UploadService {
    private val service: ImgurApi = Retrofit.Builder()
        .baseUrl("https://api.imgur.com/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(
            OkHttpClient.Builder()
                .addInterceptor(OAuthInterceptor("Client-ID", BuildConfig.CLIENT_ID)).build()
        )
        .build()
        .create(ImgurApi::class.java)

    suspend fun upload(imageBinary: ByteArray): PostImageResponse =
        service.postImage(RequestBody.create(MediaType.parse("image/*"), imageBinary))

}