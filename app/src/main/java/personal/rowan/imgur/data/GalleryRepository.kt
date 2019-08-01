package personal.rowan.imgur.data

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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

    fun getPopulatedGalleries(): Observable<List<PopulatedGallery>> {
        // Observable.concatArrayEager will combine observables, preserving the order and executing in parallel
        return Observable.concatArrayEager(
            galleryDao.getGalleriesByDatetime().subscribeOn(Schedulers.io()),
            // Observable.defer will not create the Observable until it is subscribed to, and will create a fresh Observable for each observer
            Observable.defer {
                imgurWebService.getGallery(
                    "hot", "time", "day", 0,
                    showViral = true,
                    mature = true,
                    albumPreviews = true
                ).subscribeOn(Schedulers.io())
                    .flatMap { parseAndPersistGalleryResponse(it) }
            }
        ).observeOn(AndroidSchedulers.mainThread())
    }

    private fun parseAndPersistGalleryResponse(galleryResponse: GalleryResponse): Observable<List<PopulatedGallery>> {
        val persistedGalleries = mutableListOf<Gallery>()
        val persistedImages = mutableListOf<Image>()
        val populatedGalleries = galleryResponse.data.map { galleryDto ->
            val gallery = Gallery(galleryDto)
            val images = galleryDto.images.map { imageDto -> Image(imageDto, galleryDto.id) }
            persistedGalleries.add(gallery)
            persistedImages.addAll(images)
            PopulatedGallery(gallery, images)
        }

        return Completable.fromCallable {
            galleryDao.insertAllGalleries(persistedGalleries)
            galleryDao.insertAllImages(persistedImages)
        }.andThen(Observable.just(populatedGalleries))
    }

}