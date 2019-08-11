package personal.rowan.imgur.utils

import personal.rowan.imgur.data.PagingRequestHelper
import personal.rowan.imgur.data.db.GalleryDao
import personal.rowan.imgur.data.db.model.Gallery
import personal.rowan.imgur.data.db.model.Image
import personal.rowan.imgur.data.network.model.GalleryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

/**
 * Created by Rowan Hall
 */

fun createWebserviceCallback(
    callback: PagingRequestHelper.Request.Callback,
    galleryDao: GalleryDao,
    ioExecutor: Executor
): Callback<GalleryResponse> {
    return object : Callback<GalleryResponse> {
        override fun onFailure(call: Call<GalleryResponse>, t: Throwable) {
            callback.recordFailure(t)
        }

        override fun onResponse(call: Call<GalleryResponse>, response: Response<GalleryResponse>) {
            ioExecutor.execute {
                parseGalleryResponse(response).persist(galleryDao)
                callback.recordSuccess()
            }
        }
    }
}

fun parseGalleryResponse(response: Response<GalleryResponse>): ParsedGalleryResponse {
    val galleries = mutableListOf<Gallery>()
    val images = mutableListOf<Image>()
    response.body()!!.data.map { galleryDto ->
        val gallery = Gallery(galleryDto)
        val galleryImages = galleryDto.images?.map { imageDto -> Image(imageDto, galleryDto.id) } ?: ArrayList()
        galleries.add(gallery)
        images.addAll(galleryImages)
    }
    return ParsedGalleryResponse(galleries, images)
}

data class ParsedGalleryResponse(private val galleries: List<Gallery>, private val images: List<Image>) {
    fun persist(galleryDao: GalleryDao) {
        galleryDao.insertAllGalleries(galleries)
        galleryDao.insertAllImages(images)
    }
}