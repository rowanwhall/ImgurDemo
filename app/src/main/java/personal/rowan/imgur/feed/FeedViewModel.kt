package personal.rowan.imgur.feed

import androidx.lifecycle.*
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.paging.PagedList
import personal.rowan.imgur.data.GalleryRepository
import personal.rowan.imgur.data.GallerySort
import personal.rowan.imgur.data.db.model.PopulatedGallery
import personal.rowan.imgur.data.network.NetworkState

/**
 * Created by Rowan Hall
 */
class FeedViewModel internal constructor(private val galleryRepository: GalleryRepository) : ViewModel() {

    private val galleryArguments = MutableLiveData<GallerySort>()
    private val repoResult = map(galleryArguments) { galleryRepository.getGalleries(GallerySort.TIME) }
    val feed = switchMap(repoResult) { it.pagedList }
    val networkState = switchMap(repoResult) { it.networkState }
    val refreshState = switchMap(repoResult) { it.refreshState }

    fun loadFeed(gallerySort: GallerySort): Boolean {
        if (galleryArguments.value == gallerySort) {
            return false
        }
        galleryArguments.value = gallerySort
        return true
    }

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun retry() {
        repoResult.value?.retry?.invoke()
    }
}

data class Feed(val pagedList: LiveData<PagedList<PopulatedGallery>>,
                val networkState: LiveData<NetworkState>,
                val retry: Function0<Unit>,
                val refresh: Function0<Unit>,
                val refreshState: LiveData<NetworkState>)

class FeedViewModelFactory(private val galleryRepository: GalleryRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = FeedViewModel(galleryRepository) as T
}