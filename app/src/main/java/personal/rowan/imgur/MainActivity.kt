package personal.rowan.imgur

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import personal.rowan.imgur.data.GalleryArguments
import personal.rowan.imgur.data.GallerySection
import personal.rowan.imgur.data.GallerySort
import personal.rowan.imgur.data.network.NetworkState
import personal.rowan.imgur.data.network.Status

import personal.rowan.imgur.databinding.ActivityMainBinding
import personal.rowan.imgur.feed.FeedAdapter
import personal.rowan.imgur.feed.FeedViewModel
import personal.rowan.imgur.utils.InjectorUtils

class MainActivity : AppCompatActivity() {

    private val viewModel: FeedViewModel by viewModels { InjectorUtils.provideFeedViewModelFactory(this) }
    private lateinit var binding: ActivityMainBinding
    private var retrySnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initUi()
    }

    private fun initUi() {
        val adapter = FeedAdapter()
        binding.feedRecycler.adapter = adapter
        viewModel.feedData.observePagedList(this, Observer { adapter.submitList(it) })
        viewModel.feedData.observeNetworkState(this, Observer { onNetworkStateChange(it) })
        viewModel.feedData.observeRefreshState(this, Observer { binding.feedRefresh.isRefreshing = it.status == Status.RUNNING })
        binding.feedRefresh.setOnRefreshListener { viewModel.feedData.refresh() }
        viewModel.feedData.setArguments(GalleryArguments(GallerySection.HOT, GallerySort.TOP))
    }

    private fun onNetworkStateChange(networkState: NetworkState) {
        when (networkState.status) {
            Status.RUNNING -> { /* no-op */ }
            Status.SUCCESS -> {
                retrySnackbar?.dismiss()
                retrySnackbar = null
            }
            Status.FAILED -> {
                retrySnackbar = Snackbar.make(
                    binding.root,
                    "There was an error loading more posts",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Retry") { viewModel.feedData.retry() }
                retrySnackbar?.show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        retrySnackbar = null
    }
}
