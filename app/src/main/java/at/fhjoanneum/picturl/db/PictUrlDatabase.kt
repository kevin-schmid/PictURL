package at.fhjoanneum.picturl.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import at.fhjoanneum.picturl.model.PictUrlImage

@Database(entities = [PictUrlImage::class], version = 1)
abstract class PictUrlDatabase : RoomDatabase() {

    abstract fun imageDao(): PictUrlImageDao

    companion object {
        @Volatile
        private var INSTANCE: PictUrlDatabase? = null

        fun getDatabase(context: Context): PictUrlDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PictUrlDatabase::class.java,
                    "pictUrl_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}