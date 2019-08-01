package personal.rowan.imgur

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer

import personal.rowan.imgur.databinding.ActivityMainBinding
import personal.rowan.imgur.feed.FeedAdapter
import personal.rowan.imgur.feed.FeedViewModel
import personal.rowan.imgur.feed.FeedViewState
import personal.rowan.imgur.utils.InjectorUtils

class MainActivity : AppCompatActivity() {

    private val viewModel: FeedViewModel by viewModels { InjectorUtils.provideFeedViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val adapter = FeedAdapter()
        binding.feedRecycler.adapter = adapter
        viewModel.feed.observe(this, Observer<FeedViewState> {
            adapter.submitList(it.galleries)
        })
        viewModel.loadFeed()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }
}
