package personal.rowan.imgur.data.paging.networkonly

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import personal.rowan.imgur.data.paging.networkanddb.GalleryArguments
import personal.rowan.imgur.data.db.GalleryDao
import personal.rowan.imgur.data.db.model.PopulatedGallery
import personal.rowan.imgur.data.network.ImgurWebService
import java.util.concurrent.Executor

/**
 * Created by Rowan Hall
 */
class GalleryNetworkDataSourceFactory(
    private val imgurWebService: ImgurWebService,
    private val galleryDao: GalleryDao,
    private val arguments: GalleryArguments,
    private val retryExecutor: Executor
) : DataSource.Factory<Int, PopulatedGallery>() {
    val sourceLiveData = MutableLiveData<GalleryNetworkDataSource>()

    override fun create(): DataSource<Int, PopulatedGallery> {
        val source = GalleryNetworkDataSource(
            imgurWebService,
            galleryDao,
            arguments,
            retryExecutor
        )
        sourceLiveData.postValue(source)
        return source
    }
}