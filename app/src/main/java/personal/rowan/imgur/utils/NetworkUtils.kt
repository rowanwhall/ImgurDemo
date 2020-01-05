package personal.rowan.imgur.utils

import personal.rowan.imgur.data.GalleryArguments
import personal.rowan.imgur.data.db.GalleryDao
import personal.rowan.imgur.data.db.model.Gallery
import personal.rowan.imgur.data.db.model.Image
import personal.rowan.imgur.data.db.model.PopulatedGallery
import personal.rowan.imgur.data.network.model.GalleryResponse

/**
 * Created by Rowan Hall
 */

fun parseAndPersistGalleryResponse(response: GalleryResponse, galleryArguments: GalleryArguments, galleryDao: GalleryDao): List<PopulatedGallery> {
    val populatedGalleries = mutableListOf<PopulatedGallery>()
    val galleries = mutableListOf<Gallery>()
    val images = mutableListOf<Image>()
    response.data.map { galleryDto ->
        val gallery = Gallery(galleryDto, galleryArguments)
        val galleryImages = galleryDto.images?.map { imageDto -> Image(imageDto, galleryDto.id) } ?: ArrayList()
        if (galleryImages.isNotEmpty()) {
            galleries.add(gallery)
            images.addAll(galleryImages)
            populatedGalleries.add(PopulatedGallery(gallery, galleryImages))
        }
    }
    galleryDao.insertGalleries(galleries)
    galleryDao.insertImages(images)
    return populatedGalleries
}