package personal.rowan.imgur.data.paging.networkanddb

import androidx.annotation.MainThread
import androidx.paging.PagedList
import io.reactivex.schedulers.Schedulers
import personal.rowan.imgur.data.PagingRequestHelper
import personal.rowan.imgur.data.db.GalleryDao
import personal.rowan.imgur.data.db.model.PopulatedGallery
import personal.rowan.imgur.data.network.ImgurWebService
import personal.rowan.imgur.utils.createStatusLiveData
import personal.rowan.imgur.utils.parseAndPersistGalleryResponse
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by Rowan Hall
 */
class GalleryBoundaryCallback(
    private val imgurWebService: ImgurWebService,
    private val galleryDao: GalleryDao,
    private val arguments: GalleryArguments,
    retryExecutor: Executor
) : PagedList.BoundaryCallback<PopulatedGallery>() {

    val helper = PagingRequestHelper(retryExecutor)
    val networkState = helper.createStatusLiveData()
    private val currentPage = AtomicInteger(0)

    @MainThread
    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            imgurWebService.getGallery(
                arguments.section.requestString, arguments.sort.requestString,
                page = 0
            )
                .subscribeOn(Schedulers.io())
                .subscribe { response -> parseAndPersistGalleryResponse(response, arguments, galleryDao) }
        }
    }

    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: PopulatedGallery) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            imgurWebService.getGallery(
                arguments.section.requestString, arguments.sort.requestString,
                page = currentPage.incrementAndGet()
            )
                .subscribeOn(Schedulers.io())
                .subscribe { response -> parseAndPersistGalleryResponse(response, arguments, galleryDao) }
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: PopulatedGallery) {
        // no-op
    }
}