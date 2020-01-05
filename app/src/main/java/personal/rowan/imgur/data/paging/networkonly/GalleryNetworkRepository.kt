package personal.rowan.imgur.data.paging.networkonly

import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import personal.rowan.imgur.data.GalleryArguments
import personal.rowan.imgur.data.paging.PagedListState
import personal.rowan.imgur.data.db.GalleryDao
import personal.rowan.imgur.data.db.model.PopulatedGallery
import personal.rowan.imgur.data.network.ImgurWebService
import java.util.concurrent.Executor

/**
 * Created by Rowan Hall
 */
class GalleryNetworkRepository(
    private val imgurWebService: ImgurWebService,
    private val galleryDao: GalleryDao,
    private val retryExecutor: Executor
) {

    companion object {
        private const val PAGE_SIZE = 60
    }

    fun getGalleries(galleryArguments: GalleryArguments) : PagedListState<PopulatedGallery> {
        val sourceFactory = GalleryNetworkDataSourceFactory(
            imgurWebService,
            galleryDao,
            galleryArguments,
            retryExecutor
        )
        val livePagedList = sourceFactory.toLiveData(pageSize = PAGE_SIZE, fetchExecutor = retryExecutor)
        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveData) { it.initialLoad }
        return PagedListState(
            pagedList = livePagedList,
            networkState = Transformations.switchMap(sourceFactory.sourceLiveData) {
                it.networkState
            },
            retry = {
                sourceFactory.sourceLiveData.value?.retryAllFailed()
            },
            refresh = {
                sourceFactory.sourceLiveData.value?.invalidate()
            },
            refreshState = refreshState
        )
    }

}