package personal.rowan.imgur.data.network

/**
 * Created by Rowan Hall
 */
enum class Status {
    RUNNING,
    SUCCESS,
    FAILED
}

@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
    private val status: Status,
    private val msg: String? = null) {
    companion object {
        val LOADED = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.RUNNING)
        fun error(msg: String?) = NetworkState(Status.FAILED, msg)
    }

    val isRunning = status == Status.RUNNING
    val isFailed = status == Status.FAILED
    val hasErrorText = !msg.isNullOrEmpty()
    val errorText = msg ?: ""
}