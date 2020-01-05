package personal.rowan.imgur.feed

import androidx.lifecycle.*
import personal.rowan.imgur.data.GalleryArguments
import personal.rowan.imgur.data.GallerySource
import personal.rowan.imgur.data.db.model.PopulatedGallery
import personal.rowan.imgur.data.paging.PagedListLiveData
import personal.rowan.imgur.data.paging.networkanddb.GalleryNetworkAndDbRepository
import personal.rowan.imgur.data.paging.networkonly.GalleryNetworkRepository

/**
 * Created by Rowan Hall
 */
class FeedViewModel internal constructor(private val networkAndDbRepository: GalleryNetworkAndDbRepository,
                                         private val networkRepository: GalleryNetworkRepository) : ViewModel() {

    val feed = PagedListLiveData<GalleryArguments, PopulatedGallery> {
        when(it.source) {
            GallerySource.NETWORK_ONLY -> networkRepository.getGalleries(it)
            GallerySource.NETWORK_AND_DB -> networkAndDbRepository.getGalleries(it)
        }
    }
}

class FeedViewModelFactory(private val networkAndDbRepository: GalleryNetworkAndDbRepository,
                           private val networkRepository: GalleryNetworkRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = FeedViewModel(networkAndDbRepository, networkRepository) as T
}