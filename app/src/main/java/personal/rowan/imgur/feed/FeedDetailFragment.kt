package personal.rowan.imgur.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import personal.rowan.imgur.R
import personal.rowan.imgur.databinding.FragmentFeedDetailBinding
import personal.rowan.imgur.databinding.ListItemFeedDetailBinding

/**
 * Created by Rowan Hall
 */
class FeedDetailFragment : Fragment() {

    private lateinit var binding: FragmentFeedDetailBinding
    private val args: FeedDetailFragmentArgs by navArgs()

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            activity?.title = getString(R.string.detail_toolbar,
                (position + 1).toString(), args.populatedGallery.images.size.toString())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_feed_detail, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(binding.detailToolbar)
        binding.detailViewpager.registerOnPageChangeCallback(pageChangeCallback)
        binding.detailViewpager.adapter = FeedDetailAdapter(args.populatedGallery.images.map { it.link })
    }

    override fun onDestroyView() {
        binding.detailViewpager.unregisterOnPageChangeCallback(pageChangeCallback)
        super.onDestroyView()
    }
}

class FeedDetailAdapter(private val imageUrls: List<String>) : RecyclerView.Adapter<FeedDetailViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedDetailViewHolder {
        return FeedDetailViewHolder(ListItemFeedDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return imageUrls.size
    }

    override fun onBindViewHolder(holder: FeedDetailViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }
}

class FeedDetailViewHolder(private val binding: ListItemFeedDetailBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(imageUrl: String) {
        binding.imageUrl = imageUrl
    }
}