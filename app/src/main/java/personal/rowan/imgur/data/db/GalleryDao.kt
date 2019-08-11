package personal.rowan.imgur.data.db

import androidx.paging.DataSource
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
    @Query("SELECT * FROM gallery ORDER BY points DESC")
    fun getGalleriesByPoints(): DataSource.Factory<Int, PopulatedGallery>

    @Transaction
    @Query("SELECT * FROM gallery ORDER BY datetime DESC")
    fun getGalleriesByDatetime(): DataSource.Factory<Int, PopulatedGallery>

    @Query("SELECT * FROM image WHERE id = :imageId")
    fun getImageById(imageId: String): Observable<Image>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllGalleries(galleries: List<Gallery>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllImages(images: List<Image>)
}