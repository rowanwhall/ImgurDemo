package personal.rowan.imgur.data

import androidx.lifecycle.*
import androidx.paging.PagedList
import personal.rowan.imgur.data.network.NetworkState

/**
 * Created by Rowan Hall
 */
class PagedListLiveData<A, D>(private val mapArgumentsFunction: Function1<A, PagedListState<D>>) {

    private val arguments = MutableLiveData<A>()
    private val repoResult = Transformations.map(arguments) { mapArgumentsFunction.invoke(it) }
    private val pagedListData = Transformations.switchMap(repoResult) { it.pagedList }
    private val networkStateData = Transformations.switchMap(repoResult) { it.networkState }
    private val refreshStateData = Transformations.switchMap(repoResult) { it.refreshState }

    fun setArguments(arguments: A): Boolean {
        if (this.arguments.value == arguments) {
            return false
        }
        this.arguments.value = arguments
        return true
    }

    fun observePagedList(lifecycleOwner: LifecycleOwner, observer: Observer<PagedList<D>>) {
        pagedListData.observe(lifecycleOwner, observer)
    }

    fun observeNetworkState(lifecycleOwner: LifecycleOwner, observer: Observer<NetworkState>) {
        networkStateData.observe(lifecycleOwner, observer)
    }

    fun observeRefreshState(lifecycleOwner: LifecycleOwner, observer: Observer<NetworkState>) {
        refreshStateData.observe(lifecycleOwner, observer)
    }

    fun refresh() {
        repoResult.value?.refresh?.invoke()
    }

    fun retry() {
        repoResult.value?.retry?.invoke()
    }
}

class PagedListState<T>(val pagedList: LiveData<PagedList<T>>,
                        val networkState: LiveData<NetworkState>,
                        val retry: Function0<Unit>,
                        val refresh: Function0<Unit>,
                        val refreshState: LiveData<NetworkState>
)