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
    suspend fun getAll(): List<PictUrlImage>

    @Query("select count(*) from $PICTURL_IMAGE_TABLE_NAME")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: PictUrlImage)

    @Query("DELETE FROM $PICTURL_IMAGE_TABLE_NAME WHERE id = :id")
    suspend fun delete(id: String)

    @Query("select * from $PICTURL_IMAGE_TABLE_NAME where title like :title")
    suspend fun getFiltered(title: String): List<PictUrlImage>
}