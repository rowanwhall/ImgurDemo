package personal.rowan.imgur.feed

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import io.reactivex.disposables.Disposable
import personal.rowan.imgur.R
import personal.rowan.imgur.data.GalleryArguments
import personal.rowan.imgur.data.GallerySection
import personal.rowan.imgur.data.GallerySort
import personal.rowan.imgur.data.GallerySource
import personal.rowan.imgur.data.db.model.PopulatedGallery
import personal.rowan.imgur.data.network.NetworkState
import personal.rowan.imgur.data.network.Status
import personal.rowan.imgur.data.paging.PagedListLiveData
import personal.rowan.imgur.databinding.FeedBottomSheetBinding
import personal.rowan.imgur.databinding.FragmentFeedBinding
import personal.rowan.imgur.utils.InjectorUtils

/**
 * Created by Rowan Hall
 */
class FeedFragment : Fragment() {

    private val viewModel: FeedViewModel by viewModels { InjectorUtils.provideFeedViewModelFactory(activity!!) }
    private lateinit var binding: FragmentFeedBinding
    private var itemClickDisposable: Disposable? = null
    private var bottomSheet: BottomSheetDialog? = null
    private var retrySnackbar: Snackbar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_feed, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        val initialArguments = GalleryArguments(GallerySection.HOT, GallerySort.TOP, GallerySource.NETWORK_ONLY)
        subscribeToFeed(viewModel.feed, initialArguments)
        setupBottomSheet(initialArguments)
    }

    private fun setupToolbar() {
        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)
        activity.title = "Imgur Jetpack Demo"
        setHasOptionsMenu(true)
    }

    private fun subscribeToFeed(feed: PagedListLiveData<GalleryArguments, PopulatedGallery>, initialArguments: GalleryArguments) {
        val adapter = FeedAdapter()
        itemClickDisposable = adapter.itemClickObservable().subscribe { navigateToDetailFragment(it) }
        binding.recycler.adapter = adapter
        feed.observePagedList(this, Observer { (binding.recycler.adapter as FeedAdapter).submitList(it) })
        feed.observeNetworkState(this, Observer { onNetworkStateChange(it) })
        feed.observeRefreshState(this, Observer { binding.swipeRefresh.isRefreshing = it.status == Status.RUNNING })
        binding.swipeRefresh.setOnRefreshListener { viewModel.feed.refresh() }
        feed.setArguments(initialArguments)
    }

    private fun navigateToDetailFragment(populatedGallery: PopulatedGallery) {
        findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToFeedDetailFragment(populatedGallery.images[0].link))
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
                ).setAction("Retry") { viewModel.feed.retry() }
                retrySnackbar?.show()
            }
        }
    }

    private fun setupBottomSheet(initialArguments: GalleryArguments) {
        // set initial state
        val bottomSheetBinding = FeedBottomSheetBinding.inflate(LayoutInflater.from(context!!))
        bottomSheet = BottomSheetDialog(context!!)
        bottomSheet?.setContentView(bottomSheetBinding.root)
        bottomSheetBinding.arguments = initialArguments

        // nested methods for setting arguments
        fun setNewArguments(newArguments: GalleryArguments) {
            viewModel.feed.setArguments(newArguments)
            bottomSheetBinding.arguments = newArguments
            bottomSheet?.hide()
        }

        fun setSection(section: GallerySection) {
            viewModel.feed.getArguments()?.let { setNewArguments(GalleryArguments(section, it.sort, it.source)) }
        }

        fun setSort(sort: GallerySort) {
            viewModel.feed.getArguments()?.let { setNewArguments(GalleryArguments(it.section, sort, it.source)) }
        }

        fun setSource(source: GallerySource) {
            viewModel.feed.getArguments()?.let {
                // adapter must be reset when changing source due to different paged list types
                binding.recycler.adapter = FeedAdapter()
                setNewArguments(GalleryArguments(it.section, it.sort, source))
            }
        }

        // click listeners for interaction
        bottomSheetBinding.sectionHot.setOnClickListener { setSection(GallerySection.HOT) }
        bottomSheetBinding.sectionTop.setOnClickListener { setSection(GallerySection.TOP) }
        bottomSheetBinding.sortTime.setOnClickListener { setSort(GallerySort.TIME) }
        bottomSheetBinding.sortTop.setOnClickListener { setSort(GallerySort.TOP) }
        bottomSheetBinding.sourceDb.setOnClickListener { setSource(GallerySource.NETWORK_AND_DB) }
        bottomSheetBinding.sourceNetwork.setOnClickListener { setSource(GallerySource.NETWORK_ONLY) }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                bottomSheet?.show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        itemClickDisposable?.dispose()
        retrySnackbar = null
        bottomSheet = null
    }

}