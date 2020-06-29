package personal.rowan.imgur.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import personal.rowan.imgur.R
import personal.rowan.imgur.databinding.FragmentFeedDetailBinding

/**
 * Created by Rowan Hall
 */
class FeedDetailFragment : Fragment() {

    private lateinit var binding: FragmentFeedDetailBinding
    private val args: FeedDetailFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_feed_detail, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageUrl = args.imageUrl
    }

}