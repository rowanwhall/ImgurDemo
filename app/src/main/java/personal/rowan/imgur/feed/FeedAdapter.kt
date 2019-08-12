package personal.rowan.imgur.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import personal.rowan.imgur.data.db.model.Gallery
import personal.rowan.imgur.data.db.model.PopulatedGallery
import personal.rowan.imgur.databinding.ListItemFeedBinding

/**
 * Created by Rowan Hall
 */
class FeedAdapter : PagedListAdapter<PopulatedGallery, FeedViewHolder>(FeedDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return GalleryViewHolder(ListItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        (holder as GalleryViewHolder).bind(getItem(position))
    }
}

class GalleryViewHolder(private val binding: ListItemFeedBinding) : FeedViewHolder(binding.root) {

    companion object {
        private val PLACEHOLDER_GALLERY = PopulatedGallery(
            Gallery(
                id = "",
                title = "",
                description = "",
                datetime = 0,
                cover = "",
                coverWidth = 0,
                coverHeight = 0,
                accountUrl = "",
                accountId = "",
                privacy = "",
                layout = "",
                views = 0,
                link = "",
                ups = 0,
                downs = 0,
                points = 0,
                score = 0,
                isAlbum = false,
                favorite = false,
                nsfw = false,
                section = "",
                commentCount = 0,
                favoriteCount = 0,
                topic = "",
                topicId = 0,
                imagesCount = 0,
                inGallery = false,
                isAd = false,
                sectionArgument = ""
            ),
            arrayListOf()
        )
    }

    fun bind(gallery: PopulatedGallery?) {
        binding.apply {
            setGallery(gallery ?: PLACEHOLDER_GALLERY)
            executePendingBindings()
        }
    }
}

// For use as marker interface for additional feed item types
abstract class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

private class FeedDiffCallback : DiffUtil.ItemCallback<PopulatedGallery>() {

    override fun areItemsTheSame(oldItem: PopulatedGallery, newItem: PopulatedGallery): Boolean {
        return oldItem.gallery?.id == newItem.gallery?.id
    }

    override fun areContentsTheSame(oldItem: PopulatedGallery, newItem: PopulatedGallery): Boolean {
        return oldItem == newItem
    }
}