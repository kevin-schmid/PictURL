package at.fhjoanneum.picturl.service

import okhttp3.MultipartBody

object ImgurApiSimulation : ImgurApi {
    override suspend fun postImage(
        image: MultipartBody.Part,
        title: MultipartBody.Part
    ) = PostImageResponse().apply {
        success = true
        data = PostImageResponseData().apply {
            this.id = "" + System.currentTimeMillis()
            this.title = title.body().toString()
            this.link = "https://i.imgur.com/Kvd00L6.jpg"
            this.deletehash = "delete_me"
        }
    }

    override suspend fun deleteImage(deleteHash: String) {}
}