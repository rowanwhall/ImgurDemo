package personal.rowan.imgur.data.paging.networkonly

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.reactivex.schedulers.Schedulers
import personal.rowan.imgur.data.paging.networkanddb.GalleryArguments
import personal.rowan.imgur.data.db.GalleryDao
import personal.rowan.imgur.data.db.model.PopulatedGallery
import personal.rowan.imgur.data.network.ImgurWebService
import personal.rowan.imgur.data.network.NetworkState
import personal.rowan.imgur.utils.parseAndPersistGalleryResponse
import java.util.concurrent.Executor

/**
 * Created by Rowan Hall
 */
class GalleryNetworkDataSource(
    private val imgurWebService: ImgurWebService,
    private val galleryDao: GalleryDao,
    private val arguments: GalleryArguments,
    private val retryExecutor: Executor
) : PageKeyedDataSource<Int, PopulatedGallery>() {

    private var retry: (() -> Any)? = null

    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()

    fun retryAllFailed() {
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, PopulatedGallery>) {
        // no-op
    }

    @SuppressLint("CheckResult")
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, PopulatedGallery>) {
        networkState.postValue(NetworkState.LOADING)
        val nextPage = params.key
        imgurWebService.getGallery(arguments.section.requestString, arguments.sort.requestString, page = nextPage)
            .subscribeOn(Schedulers.io())
            .subscribe({
                networkState.postValue(NetworkState.LOADED)
                initialLoad.postValue(NetworkState.LOADED)
                val populatedGalleries = parseAndPersistGalleryResponse(it, arguments, galleryDao)
                callback.onResult(populatedGalleries, nextPage + 1)
            }, {
                val error = NetworkState.error(it)
                networkState.postValue(error)
                initialLoad.postValue(error)
            })
    }

    @SuppressLint("CheckResult")
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, PopulatedGallery>) {
        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        imgurWebService.getGallery(arguments.section.requestString, arguments.sort.requestString, page = 0)
            .subscribeOn(Schedulers.io())
            .subscribe({
                networkState.postValue(NetworkState.LOADED)
                initialLoad.postValue(NetworkState.LOADED)
                val populatedGalleries = parseAndPersistGalleryResponse(it, arguments, galleryDao)
                callback.onResult(populatedGalleries, 0, 1)
            }, {
                val error = NetworkState.error(it)
                networkState.postValue(error)
                initialLoad.postValue(error)
            })
    }
}