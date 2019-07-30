package personal.rowan.imgur.data.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import personal.rowan.imgur.data.db.model.PopulatedGallery

/**
 * Created by Rowan Hall
 */

@Dao
interface GalleryDao {

    @Transaction
    @Query("SELECT * FROM gallery ORDER BY datetime")
    fun getGalleriesByDatetime(): List<PopulatedGallery>
}