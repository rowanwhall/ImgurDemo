package personal.rowan.imgur.feed

import androidx.lifecycle.*
import personal.rowan.imgur.data.db.model.PopulatedGallery
import personal.rowan.imgur.data.paging.PagedListLiveData
import personal.rowan.imgur.data.paging.networkanddb.GalleryArguments
import personal.rowan.imgur.data.paging.networkanddb.GalleryNetworkAndDbRepository
import personal.rowan.imgur.data.paging.networkonly.GalleryNetworkRepository

/**
 * Created by Rowan Hall
 */
class FeedViewModel internal constructor(private val networkAndDbRepository: GalleryNetworkAndDbRepository,
                                         private val networkRepository: GalleryNetworkRepository) : ViewModel() {

    val networkFeed = PagedListLiveData<GalleryArguments, PopulatedGallery> {
        networkRepository.getGalleries(it)
    }

    val networkAndDbFeed = PagedListLiveData<GalleryArguments, PopulatedGallery> {
        networkAndDbRepository.getGalleries(it)
    }
}

class FeedViewModelFactory(private val networkAndDbRepository: GalleryNetworkAndDbRepository,
                           private val networkRepository: GalleryNetworkRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = FeedViewModel(networkAndDbRepository, networkRepository) as T
}