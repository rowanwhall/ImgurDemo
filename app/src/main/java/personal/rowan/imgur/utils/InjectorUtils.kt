package personal.rowan.imgur.utils

import android.content.Context
import okhttp3.OkHttpClient
import personal.rowan.imgur.data.paging.networkanddb.GalleryNetworkAndDbRepository
import personal.rowan.imgur.data.db.ImgurDatabase
import personal.rowan.imgur.data.network.ImgurWebService
import personal.rowan.imgur.data.network.createRetrofitService
import personal.rowan.imgur.data.paging.networkonly.GalleryNetworkRepository
import personal.rowan.imgur.feed.FeedViewModelFactory
import java.util.concurrent.Executors

/**
 * Created by Rowan Hall
 */

object InjectorUtils {

    private val IO_EXECUTOR = Executors.newFixedThreadPool(5)

    private val HTTP_CLIENT = OkHttpClient.Builder()
        .addInterceptor {
            it.proceed(it.request().newBuilder()
                .addHeader("Authorization", ImgurWebService.AUTHORIZATION).build())
        }
        .build()

    private fun provideImgurWebService(): ImgurWebService {
        return createRetrofitService(
            ImgurWebService::class.java,
            ImgurWebService.BASE_URL,
            HTTP_CLIENT
        )
    }

    private fun provideGalleryNetworkAndDbRepository(context: Context): GalleryNetworkAndDbRepository {
        return GalleryNetworkAndDbRepository.getInstance(provideImgurWebService(), ImgurDatabase.getInstance(context).galleryDao(), IO_EXECUTOR)
    }

    private fun provideGalleryNetworkRepository(context: Context): GalleryNetworkRepository {
        return GalleryNetworkRepository(
            provideImgurWebService(),
            ImgurDatabase.getInstance(context).galleryDao(),
            IO_EXECUTOR
        )
    }

    fun provideFeedViewModelFactory(context: Context): FeedViewModelFactory {
        return FeedViewModelFactory(provideGalleryNetworkAndDbRepository(context), provideGalleryNetworkRepository(context))
    }
}