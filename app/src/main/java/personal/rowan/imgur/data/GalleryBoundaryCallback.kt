package personal.rowan.imgur.data

import androidx.annotation.MainThread
import androidx.paging.PagedList
import personal.rowan.imgur.data.db.GalleryDao
import personal.rowan.imgur.data.db.model.PopulatedGallery
import personal.rowan.imgur.data.network.ImgurWebService
import personal.rowan.imgur.utils.createStatusLiveData
import personal.rowan.imgur.utils.createGalleryCallback
import java.util.concurrent.Executor

/**
 * Created by Rowan Hall
 */
class GalleryBoundaryCallback(
    private val imgurWebService: ImgurWebService,
    private val galleryDao: GalleryDao,
    private val arguments: GalleryArguments,
    private val ioExecutor: Executor
) : PagedList.BoundaryCallback<PopulatedGallery>() {

    val helper = PagingRequestHelper(ioExecutor)
    val networkState = helper.createStatusLiveData()
    private var currentPage = 0

    @MainThread
    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            imgurWebService.getGallery(
                arguments.section.requestString, arguments.sort.requestString,
                page = 0,
                showViral = true,
                mature = true,
                albumPreviews = true
            ).enqueue(createGalleryCallback(it, galleryDao, arguments, ioExecutor))
        }
    }

    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: PopulatedGallery) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            imgurWebService.getGallery(
                arguments.section.requestString, arguments.sort.requestString,
                page = ++currentPage,
                showViral = true,
                mature = true,
                albumPreviews = true
            ).enqueue(createGalleryCallback(it, galleryDao, arguments, ioExecutor))
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: PopulatedGallery) {
        // no-op
    }
}