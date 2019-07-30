package personal.rowan.imgur.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Rowan Hall
 */
fun <T> createRetrofitService(clazz: Class<T>, endpoint: String, client: OkHttpClient): T {
    val retrofit = Retrofit.Builder()
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(endpoint)
        .client(client)
        .build()

    return retrofit.create(clazz)
}