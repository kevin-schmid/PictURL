package at.fhjoanneum.picturl.service

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ImgurApi {
    @POST("upload")
    suspend fun postImage(@Body body: RequestBody): PostImageResponse
}