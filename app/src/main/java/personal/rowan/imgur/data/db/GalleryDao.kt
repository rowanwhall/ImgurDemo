package personal.rowan.imgur.data.db

import androidx.room.*
import io.reactivex.Observable
import personal.rowan.imgur.data.db.model.Gallery
import personal.rowan.imgur.data.db.model.Image
import personal.rowan.imgur.data.db.model.PopulatedGallery

/**
 * Created by Rowan Hall
 */

@Dao
interface GalleryDao {

    @Transaction
    @Query("SELECT * FROM gallery ORDER BY points")
    fun getGalleriesByPoints(): Observable<List<PopulatedGallery>>

    @Transaction
    @Query("SELECT * FROM gallery ORDER BY datetime")
    fun getGalleriesByDatetime(): Observable<List<PopulatedGallery>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllGalleries(galleries: List<Gallery>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllImages(images: List<Image>)
}