package personal.rowan.imgur.feed

import android.util.Log
import androidx.lifecycle.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import personal.rowan.imgur.data.GalleryRepository
import personal.rowan.imgur.data.db.model.PopulatedGallery

/**
 * Created by Rowan Hall
 */
class FeedViewModel internal constructor(private val galleryRepository: GalleryRepository) : ViewModel() {

    val feed: MutableLiveData<FeedViewState> = MutableLiveData()
    private val disposables = CompositeDisposable()

    init {
        feed.value = FeedViewState(arrayListOf())
    }

    fun loadFeed() {
        disposables.add(galleryRepository.getPopulatedGalleries()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { FeedViewState(it) }
            .subscribe(
                { feed.value = it },
                { Log.e("ERROR", it.message) })
        )
    }

    fun onDestroy() {
        disposables.dispose()
    }
}

data class FeedViewState(val galleries: List<PopulatedGallery>)

class FeedViewModelFactory(private val galleryRepository: GalleryRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>) = FeedViewModel(galleryRepository) as T
}