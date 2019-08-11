package personal.rowan.imgur

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import personal.rowan.imgur.data.GallerySort
import personal.rowan.imgur.data.db.model.PopulatedGallery

import personal.rowan.imgur.databinding.ActivityMainBinding
import personal.rowan.imgur.feed.FeedAdapter
import personal.rowan.imgur.feed.FeedViewModel
import personal.rowan.imgur.utils.InjectorUtils

class MainActivity : AppCompatActivity() {

    private val viewModel: FeedViewModel by viewModels { InjectorUtils.provideFeedViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initRecycler(binding.feedRecycler)
        initSwipeRefresh(binding.feedRefresh)
        viewModel.loadFeed(GallerySort.TIME)
    }

    private fun initRecycler(recycler: RecyclerView) {
        val adapter = FeedAdapter()
        recycler.adapter = adapter
        viewModel.feed.observe(this, Observer<PagedList<PopulatedGallery>> { adapter.submitList(it) })
        viewModel.networkState.observe(this, Observer { adapter.setNetworkState(it) })
    }

    private fun initSwipeRefresh(swipeRefreshLayout: SwipeRefreshLayout) {
        viewModel.refreshState.observe(this, Observer { swipeRefreshLayout.isRefreshing = it.isRunning })
        swipeRefreshLayout.setOnRefreshListener { viewModel.refresh() }
    }
}
