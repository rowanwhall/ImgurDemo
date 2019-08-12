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
    @Query("SELECT * FROM gallery WHERE sectionArgument = :section ORDER BY :sort DESC")
    fun getGalleries(section: String, sort: String): DataSource.Factory<Int, PopulatedGallery>

    @Query("SELECT * FROM image WHERE id = :imageId")
    fun getImageById(imageId: String): Observable<Image>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGalleries(galleries: List<Gallery>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertImages(images: List<Image>)
}