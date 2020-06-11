package at.fhjoanneum.picturl.db

import android.net.Uri
import androidx.room.TypeConverter

object Converter {
    @TypeConverter
    @JvmStatic
    fun uriToString(uri: Uri) = uri.toString()

    @TypeConverter
    @JvmStatic
    fun stringToUri(value: String): Uri = Uri.parse(value)
}