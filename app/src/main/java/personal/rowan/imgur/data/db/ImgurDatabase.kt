package personal.rowan.imgur.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
                    //.addMigrations(MIGRATION_1_2)
                    .build()
                    .also { instance = it }
            }

        /*val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {

            }
        }*/
    }
}