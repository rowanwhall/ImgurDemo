package personal.rowan.imgur

import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import personal.rowan.imgur.data.GalleryArguments
import personal.rowan.imgur.data.GallerySection
import personal.rowan.imgur.data.GallerySort
import personal.rowan.imgur.data.GallerySource
import personal.rowan.imgur.data.db.model.PopulatedGallery
import personal.rowan.imgur.data.network.NetworkState
import personal.rowan.imgur.data.network.Status
import personal.rowan.imgur.data.paging.PagedListLiveData

import personal.rowan.imgur.databinding.ActivityMainBinding
import personal.rowan.imgur.databinding.FeedBottomSheetBinding
import personal.rowan.imgur.feed.FeedAdapter
import personal.rowan.imgur.feed.FeedViewModel
import personal.rowan.imgur.utils.InjectorUtils

class MainActivity : AppCompatActivity() {

    private val viewModel: FeedViewModel by viewModels {
        InjectorUtils.provideFeedViewModelFactory(
            this
        )
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomSheetBinding: FeedBottomSheetBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var retrySnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.toolbar)
        title = "Imgur"
        subscribeToFeed(viewModel.feed, GalleryArguments(GallerySection.HOT, GallerySort.TOP, GallerySource.NETWORK_ONLY))
        setupBottomSheet()
    }

    private fun subscribeToFeed(feed: PagedListLiveData<GalleryArguments, PopulatedGallery>, initialArguments: GalleryArguments) {
        binding.recycler.adapter = FeedAdapter()
        feed.observePagedList(this, Observer { (binding.recycler.adapter as FeedAdapter).submitList(it) })
        feed.observeNetworkState(this, Observer { onNetworkStateChange(it) })
        feed.observeRefreshState(
            this,
            Observer { binding.swipeRefresh.isRefreshing = it.status == Status.RUNNING })
        binding.swipeRefresh.setOnRefreshListener { viewModel.feed.refresh() }
        feed.setArguments(initialArguments)
    }

    private fun onNetworkStateChange(networkState: NetworkState) {
        when (networkState.status) {
            Status.RUNNING -> { /* no-op */
            }
            Status.SUCCESS -> {
                retrySnackbar?.dismiss()
                retrySnackbar = null
            }
            Status.FAILED -> {
                retrySnackbar = Snackbar.make(
                    binding.root,
                    "There was an error loading more posts",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Retry") { viewModel.feed.retry() }
                retrySnackbar?.show()
            }
        }
    }

    private fun setupBottomSheet() {
        // set initial state
        bottomSheetBinding = binding.bottomSheetInclude
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetBinding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        // nested methods for setting arguments
        fun setSection(section: GallerySection) {
            viewModel.feed.getArguments()?.let {
                viewModel.feed.setArguments(GalleryArguments(section, it.sort, it.source))
            }
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        fun setSort(sort: GallerySort) {
            viewModel.feed.getArguments()?.let {
                viewModel.feed.setArguments(GalleryArguments(it.section, sort, it.source))
            }
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        fun setSource(source: GallerySource) {
            viewModel.feed.getArguments()?.let {
                // adapter must be reset when changing source due to different paged list types
                binding.recycler.adapter = FeedAdapter()
                viewModel.feed.setArguments(GalleryArguments(it.section, it.sort, source))
            }
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        // click listeners for interaction
        bottomSheetBinding.sectionHot.setOnClickListener {
            setSection(GallerySection.HOT)
            bottomSheetBinding.sectionHot.typeface = Typeface.DEFAULT_BOLD
            bottomSheetBinding.sectionTop.typeface = Typeface.DEFAULT
        }
        bottomSheetBinding.sectionTop.setOnClickListener {
            setSection(GallerySection.TOP)
            bottomSheetBinding.sectionTop.typeface = Typeface.DEFAULT_BOLD
            bottomSheetBinding.sectionHot.typeface = Typeface.DEFAULT
        }
        bottomSheetBinding.sortTime.setOnClickListener {
            setSort(GallerySort.TIME)
            bottomSheetBinding.sortTime.typeface = Typeface.DEFAULT_BOLD
            bottomSheetBinding.sortTop.typeface = Typeface.DEFAULT
        }
        bottomSheetBinding.sortTop.setOnClickListener {
            setSort(GallerySort.TOP)
            bottomSheetBinding.sortTop.typeface = Typeface.DEFAULT_BOLD
            bottomSheetBinding.sortTime.typeface = Typeface.DEFAULT
        }
        bottomSheetBinding.sourceDb.setOnClickListener {
            setSource(GallerySource.NETWORK_AND_DB)
            bottomSheetBinding.sourceDb.typeface = Typeface.DEFAULT_BOLD
            bottomSheetBinding.sourceNetwork.typeface = Typeface.DEFAULT
        }
        bottomSheetBinding.sourceNetwork.setOnClickListener {
            setSource(GallerySource.NETWORK_ONLY)
            bottomSheetBinding.sourceNetwork.typeface = Typeface.DEFAULT_BOLD
            bottomSheetBinding.sourceDb.typeface = Typeface.DEFAULT
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                when (bottomSheetBehavior.state) {
                    BottomSheetBehavior.STATE_EXPANDED -> bottomSheetBehavior.state =
                        BottomSheetBehavior.STATE_HIDDEN
                    BottomSheetBehavior.STATE_HIDDEN -> bottomSheetBehavior.state =
                        BottomSheetBehavior.STATE_EXPANDED
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        retrySnackbar = null
    }
}
