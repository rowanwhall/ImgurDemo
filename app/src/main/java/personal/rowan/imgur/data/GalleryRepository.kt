package personal.rowan.imgur.data

import io.reactivex.Completable
import io.reactivex.Observable
import personal.rowan.imgur.data.db.DataSource
import personal.rowan.imgur.data.db.GalleryDao
import personal.rowan.imgur.data.db.model.Gallery
import personal.rowan.imgur.data.db.model.Image
import personal.rowan.imgur.data.db.model.PopulatedGallery
import personal.rowan.imgur.data.network.ImgurWebService
import personal.rowan.imgur.data.network.model.GalleryResponse

/**
 * Created by Rowan Hall
 */
class GalleryRepository private constructor(
    private val imgurWebService: ImgurWebService,
    private val galleryDao: GalleryDao
) {

    companion object {

        @Volatile
        private var instance: GalleryRepository? = null

        fun getInstance(imgurWebService: ImgurWebService, galleryDao: GalleryDao) =
            instance ?: synchronized(this) {
                instance ?: GalleryRepository(imgurWebService, galleryDao).also { instance = it }
            }

    }

    fun getPopulatedGalleries(sort: GallerySort): Observable<GalleryDataSource> {
        return Observable.mergeDelayError(
            getPopulatedGalleriesFromDb(sort),
            getPopulatedGalleriesFromWeb(sort)
        )
    }

    private fun getPopulatedGalleriesFromDb(sort: GallerySort, dataSource: DataSource = DataSource.DEVICE): Observable<GalleryDataSource> {
        return when(sort) {
            GallerySort.TOP -> galleryDao.getGalleriesByPoints()
            GallerySort.TIME -> galleryDao.getGalleriesByDatetime()
        }.map { GalleryDataSource(it, dataSource) }
    }

    private fun getPopulatedGalleriesFromWeb(sort: GallerySort): Observable<GalleryDataSource> {
        return imgurWebService.getGallery(
            "hot", sort.requestString, "day", 0,
            showViral = true,
            mature = true,
            albumPreviews = true
        ).flatMap { parseAndPersistGalleryResponse(it, sort) }
    }

    private fun parseAndPersistGalleryResponse(galleryResponse: GalleryResponse, sort: GallerySort): Observable<GalleryDataSource> {
        val persistedGalleries = mutableListOf<Gallery>()
        val persistedImages = mutableListOf<Image>()
        galleryResponse.data.map { galleryDto ->
            val gallery = Gallery(galleryDto)
            val images = galleryDto.images?.map { imageDto -> Image(imageDto, galleryDto.id) } ?: ArrayList()
            persistedGalleries.add(gallery)
            persistedImages.addAll(images)
        }

        return Observable.fromCallable {
            galleryDao.insertAllGalleries(persistedGalleries)
            galleryDao.insertAllImages(persistedImages)
        }.flatMap { getPopulatedGalleriesFromDb(sort, DataSource.NETWORK) }
    }
}

data class GalleryDataSource(val galleries: List<PopulatedGallery>, val dataSource: DataSource)

enum class GallerySort(val requestString: String) {
    TOP("top"),
    TIME("time"),

}