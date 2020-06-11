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
    var id: String = ""
    var deleteHash: String = ""
    var link: String = ""
    var title: String = ""
    var localUri: Uri = Uri.EMPTY

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