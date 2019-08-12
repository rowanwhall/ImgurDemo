package personal.rowan.imgur

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.google.android.material.snackbar.Snackbar
import personal.rowan.imgur.data.GallerySort
import personal.rowan.imgur.data.db.model.PopulatedGallery
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
        viewModel.feed.observe(this, Observer<PagedList<PopulatedGallery>> { adapter.submitList(it) })
        viewModel.networkState.observe(this, Observer { onNetworkStateChange(it) })
        viewModel.refreshState.observe(
            this,
            Observer { binding.feedRefresh.isRefreshing = it.status == Status.RUNNING })
        binding.feedRefresh.setOnRefreshListener { viewModel.refresh() }
        viewModel.loadFeed(GallerySort.TIME)
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
                ).setAction("Retry") { viewModel.retry() }
                retrySnackbar?.show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        retrySnackbar = null
    }
}
