package at.fhjoanneum.picturl.service

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

object UploadService {
    private val service: ImgurApi = ImgurApi.createService()

    suspend fun upload(dto: UploadDto) =
        service.postImage(
            MultipartBody.Part.createFormData(
                "image", null,
                RequestBody.create(
                    MediaType.parse("image/*"),
                    dto.image
                )
            ), MultipartBody.Part.createFormData(
                "title", null,
                RequestBody.create(
                    MediaType.parse("text/plain"),
                    dto.title
                )
            )
        )

    suspend fun delete(deleteHash: String) = service.deleteImage(deleteHash)
}