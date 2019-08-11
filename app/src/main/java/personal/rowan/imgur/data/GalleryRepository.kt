package personal.rowan.imgur.data

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import androidx.paging.toLiveData
import personal.rowan.imgur.data.db.GalleryDao
import personal.rowan.imgur.data.network.ImgurWebService
import personal.rowan.imgur.data.network.NetworkState
import personal.rowan.imgur.data.network.model.GalleryResponse
import personal.rowan.imgur.feed.Feed
import personal.rowan.imgur.utils.parseGalleryResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

/**
 * Created by Rowan Hall
 */
class GalleryRepository private constructor(
    private val imgurWebService: ImgurWebService,
    private val galleryDao: GalleryDao,
    private val ioExecutor: Executor
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
    fun getGalleries(sort: GallerySort): Feed {
        val boundaryCallback = GalleryBoundaryCallback(imgurWebService, galleryDao, sort, ioExecutor)
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) { refresh(sort) }
        val livePagedList = galleryDao.getGalleriesByDatetime()
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

    @MainThread
    private fun refresh(sort: GallerySort): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        imgurWebService.getGallery(
            "hot", sort.requestString, "day", 0,
            showViral = true,
            mature = true,
            albumPreviews = true
        ).enqueue(
            object : Callback<GalleryResponse> {
                override fun onFailure(call: Call<GalleryResponse>, t: Throwable) {
                    // retrofit calls this on main thread so safe to call set value
                    networkState.value = NetworkState.error(t)
                }

                override fun onResponse(
                    call: Call<GalleryResponse>,
                    response: Response<GalleryResponse>
                ) {
                    ioExecutor.execute {
                        ioExecutor.execute {
                            parseGalleryResponse(response).persist(galleryDao)
                        }
                        networkState.postValue(NetworkState.LOADED)
                    }
                }
            }
        )
        return networkState
    }
}

enum class GallerySort(val requestString: String) {
    TOP("top"),
    TIME("time"),

}