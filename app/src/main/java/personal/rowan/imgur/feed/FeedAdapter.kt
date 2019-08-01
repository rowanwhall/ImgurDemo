package personal.rowan.imgur.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import personal.rowan.imgur.data.db.model.PopulatedGallery
import personal.rowan.imgur.databinding.ListItemFeedBinding

/**
 * Created by Rowan Hall
 */
class FeedAdapter : ListAdapter<PopulatedGallery, FeedViewHolder>(FeedDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return FeedViewHolder(ListItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class FeedViewHolder(private val binding: ListItemFeedBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(gallery: PopulatedGallery) {
        binding.apply { setGallery(gallery) }
    }
}

private class FeedDiffCallback : DiffUtil.ItemCallback<PopulatedGallery>() {

    override fun areItemsTheSame(oldItem: PopulatedGallery, newItem: PopulatedGallery): Boolean {
        return oldItem.gallery?.id == newItem.gallery?.id
    }

    override fun areContentsTheSame(oldItem: PopulatedGallery, newItem: PopulatedGallery): Boolean {
        return oldItem == newItem
    }
}