package personal.rowan.imgur.feed

import androidx.lifecycle.*
import personal.rowan.imgur.data.*
import personal.rowan.imgur.data.db.model.PopulatedGallery

/**
 * Created by Rowan Hall
 */
class FeedViewModel internal constructor(private val galleryRepository: GalleryRepository) : ViewModel() {

    val feedData = PagedListLiveData<GalleryArguments, PopulatedGallery> { galleryRepository.getGalleries(it) }
}

class FeedViewModelFactory(private val galleryRepository: GalleryRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = FeedViewModel(galleryRepository) as T
}