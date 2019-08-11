package personal.rowan.imgur.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import personal.rowan.imgur.data.PagingRequestHelper
import personal.rowan.imgur.data.network.NetworkState

/**
 * Created by Rowan Hall
 */

fun PagingRequestHelper.createStatusLiveData(): LiveData<NetworkState> {
    val liveData = MutableLiveData<NetworkState>()
    addListener { report ->
        when {
            report.hasRunning() -> liveData.postValue(NetworkState.LOADING)
            report.hasError() -> liveData.postValue(NetworkState.error(getError(report)))
            else -> liveData.postValue(NetworkState.LOADED)
        }
    }
    return liveData
}

private fun getError(report: PagingRequestHelper.StatusReport): Throwable? {
    return PagingRequestHelper.RequestType.values().mapNotNull { report.getErrorFor(it) }.first()
}