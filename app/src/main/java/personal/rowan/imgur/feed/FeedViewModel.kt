package personal.rowan.imgur.feed

import android.util.Log
import androidx.lifecycle.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import personal.rowan.imgur.data.GalleryDataSource
import personal.rowan.imgur.data.GalleryRepository
import personal.rowan.imgur.data.GallerySort
import personal.rowan.imgur.data.db.DataSource
import personal.rowan.imgur.data.db.model.PopulatedGallery

/**
 * Created by Rowan Hall
 */
class FeedViewModel internal constructor(private val galleryRepository: GalleryRepository) : ViewModel() {

    val feed: MutableLiveData<FeedViewState> = MutableLiveData()
    private val disposables = CompositeDisposable()

    init {
        feed.value = FeedViewState.starting()
    }

    fun loadFeed() {
        val viewState = feed.value!!
        disposables.add(galleryRepository.getPopulatedGalleries(GallerySort.TIME)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { viewState.onDataSource(it) }
            .onErrorReturn { viewState.onError(it) }
            .startWith(viewState.onProgress())
            .subscribe(
                { feed.value = it },
                { feed.value = viewState.onError(it) })
        )
    }

    fun onDestroy() {
        disposables.dispose()
    }
}

class FeedViewState private constructor(var galleries: List<PopulatedGallery>, var showProgress: Boolean, var error: Throwable?) {
    companion object {
        fun starting(): FeedViewState {
            return FeedViewState(arrayListOf(), false, null)
        }
    }

    fun onDataSource(dataSource: GalleryDataSource): FeedViewState {
        galleries = dataSource.galleries
        //if (dataSource.dataSource == DataSource.NETWORK) {
            showProgress = false
            error = null
        //}
        return this
    }

    fun onProgress(): FeedViewState {
        showProgress = true
        return this
    }

    fun onError(error: Throwable): FeedViewState {
        this.error = error
        showProgress = false
        return this
    }
}

class FeedViewModelFactory(private val galleryRepository: GalleryRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = FeedViewModel(galleryRepository) as T
}