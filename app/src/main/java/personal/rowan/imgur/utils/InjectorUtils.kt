package personal.rowan.imgur.utils

import android.content.Context
import okhttp3.OkHttpClient
import personal.rowan.imgur.data.GalleryRepository
import personal.rowan.imgur.data.db.ImgurDatabase
import personal.rowan.imgur.data.network.ImgurWebService
import personal.rowan.imgur.data.network.createRetrofitService

/**
 * Created by Rowan Hall
 */

object InjectorUtils {

    private val HTTP_CLIENT = OkHttpClient.Builder()
        .addInterceptor {
            it.proceed(it.request().newBuilder()
                .addHeader("Authorization", ImgurWebService.AUTHORIZATION).build())
        }
        .build()

    fun provideImgurWebService(): ImgurWebService {
        return createRetrofitService(
            ImgurWebService::class.java,
            ImgurWebService.BASE_URL,
            HTTP_CLIENT
        )
    }

    fun provideGalleryRepository(context: Context): GalleryRepository {
        return GalleryRepository.getInstance(provideImgurWebService(), ImgurDatabase.getInstance(context).galleryDao())
    }
}