package personal.rowan.imgur.data.network

import io.reactivex.Observable
import personal.rowan.imgur.BuildConfig
import personal.rowan.imgur.data.network.model.GalleryResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Rowan Hall
 */
interface ImgurWebService {

    companion object {
        const val BASE_URL = "https://api.imgur.com/3/"
        const val AUTHORIZATION = BuildConfig.IMGUR_AUTHORIZATION
    }

    @GET("gallery/{section}/{sort}/{window}/{page}")
    fun getGallery(@Path("section") section: String,
                   @Path("sort") sort: String,
                   @Path("window") window: String = "day",
                   @Path("page") page: Int,
                   @Query("showViral") showViral: Boolean,
                   @Query("mature") mature: Boolean,
                   @Query("albumPreviews") albumPreviews: Boolean): Call<GalleryResponse>
}