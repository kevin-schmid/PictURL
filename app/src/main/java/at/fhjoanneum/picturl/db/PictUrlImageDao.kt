package at.fhjoanneum.picturl.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import at.fhjoanneum.picturl.PICTURL_IMAGE_TABLE_NAME
import at.fhjoanneum.picturl.model.PictUrlImage

@Dao
interface PictUrlImageDao {
    @Query("select * from $PICTURL_IMAGE_TABLE_NAME")
    fun getAll(): List<PictUrlImage>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(image: PictUrlImage)
}