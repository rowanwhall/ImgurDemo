package personal.rowan.imgur.data

import android.annotation.SuppressLint
import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import androidx.paging.toLiveData
import io.reactivex.schedulers.Schedulers
import personal.rowan.imgur.data.db.GalleryDao
import personal.rowan.imgur.data.network.ImgurWebService
import personal.rowan.imgur.data.network.NetworkState
import personal.rowan.imgur.feed.Feed
import personal.rowan.imgur.utils.parseAndPersistGalleryResponse
import java.util.concurrent.Executor

/**
 * Created by Rowan Hall
 */
class GalleryRepository private constructor(
    private val imgurWebService: ImgurWebService,
    private val galleryDao: GalleryDao,
    private val retryExecutor: Executor
) {

    companion object {

        private const val PAGE_SIZE = 60

        @Volatile
        private var instance: GalleryRepository? = null

        fun getInstance(imgurWebService: ImgurWebService, galleryDao: GalleryDao, ioExecutor: Executor) =
            instance ?: synchronized(this) {
                instance ?: GalleryRepository(imgurWebService, galleryDao, ioExecutor).also { instance = it }
            }
    }

    @MainThread
    fun getGalleries(arguments: GalleryArguments): Feed {
        val boundaryCallback = GalleryBoundaryCallback(imgurWebService, galleryDao, arguments, retryExecutor)
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) { refresh(arguments) }
        val livePagedList = galleryDao.getGalleries(arguments.section.requestString, arguments.sort.requestString)
            .toLiveData(
                config = PagedList.Config.Builder()
                    .setEnablePlaceholders(true)
                    .setPageSize(PAGE_SIZE)
                    .build(),
                boundaryCallback = boundaryCallback
            )
        return Feed(
            livePagedList,
            boundaryCallback.networkState,
            { boundaryCallback.helper.retryAllFailed() },
            { refreshTrigger.value = null },
            refreshState
        )
    }

    @SuppressLint("CheckResult")
    @MainThread
    private fun refresh(arguments: GalleryArguments): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING

        imgurWebService.getGallery(
            arguments.section.requestString,
            arguments.sort.requestString,
            page = 0
        )
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    parseAndPersistGalleryResponse(it, arguments, galleryDao)
                    networkState.postValue(NetworkState.LOADED)
                },
                {
                    networkState.value = NetworkState.error(it)
                }
            )
        return networkState
    }
}

data class GalleryArguments(val section: GallerySection, val sort: GallerySort)

enum class GallerySection(val requestString: String) {
    HOT("hot"),
    TOP("top")
}

enum class GallerySort(val requestString: String) {
    TOP("top"),
    TIME("time"),
}