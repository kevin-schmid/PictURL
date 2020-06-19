package at.fhjoanneum.picturl.model

import android.net.Uri
import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import at.fhjoanneum.picturl.PICTURL_IMAGE_TABLE_NAME
import at.fhjoanneum.picturl.service.PostImageResponseData

@Entity(tableName = PICTURL_IMAGE_TABLE_NAME)
class PictUrlImage {
    @PrimaryKey
    @NonNull
    lateinit var id: String
    @NonNull
    lateinit var deleteHash: String
    @NonNull
    lateinit var link: String
    @NonNull
    lateinit var title: String
    lateinit var localUri: Uri

    companion object {
        @JvmStatic
        fun from(data: PostImageResponseData) =
            PictUrlImage().apply {
                id = data.id
                deleteHash = data.deletehash
                link = data.link
                title = data.title
            }
    }
}