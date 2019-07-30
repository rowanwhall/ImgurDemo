package personal.rowan.imgur.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import personal.rowan.imgur.data.db.model.Gallery
import personal.rowan.imgur.data.db.model.Image

/**
 * Created by Rowan Hall
 */

@Database(entities = [Gallery::class, Image::class], version = 1, exportSchema = false)
abstract class ImgurDatabase : RoomDatabase() {

    abstract fun galleryDao(): GalleryDao

    companion object {

        private const val DATABASE_NAME = "IMGUR_DATABASE"

        @Volatile
        private var instance: ImgurDatabase? = null

        fun getInstance(context: Context) =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(context, ImgurDatabase::class.java, DATABASE_NAME)
                    .build()
                    .also { instance = it }
            }
    }
}