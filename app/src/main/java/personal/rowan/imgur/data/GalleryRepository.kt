package personal.rowan.imgur.data

import io.reactivex.Observable
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

    fun getPopulatedGalleriesFromDb() = galleryDao.getGalleriesByDatetime()

    fun getPopulatedGalleriesFromWeb(): Observable<List<PopulatedGallery>> {
        return imgurWebService.getGallery(
            "hot", "time", "day", 0,
            showViral = true,
            mature = true,
            albumPreviews = true
        ).map { parseAndPersistGalleryResponse(it) }
    }

    private fun parseAndPersistGalleryResponse(galleryResponse: GalleryResponse): List<PopulatedGallery> {
        val persistedGalleries = mutableListOf<Gallery>()
        val persistedImages = mutableListOf<Image>()
        val populatedGalleries = galleryResponse.data.map { galleryDto ->
            val gallery = Gallery(galleryDto)
            val images = galleryDto.images.map { imageDto -> Image(imageDto, galleryDto.id) }
            persistedGalleries.add(gallery)
            persistedImages.addAll(images)
            PopulatedGallery(gallery, images)
        }
        galleryDao.insertAllGalleries(persistedGalleries)
        galleryDao.insertAllImages(persistedImages)
        return populatedGalleries
    }

}