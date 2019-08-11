package personal.rowan.imgur.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import personal.rowan.imgur.data.db.model.PopulatedGallery
import personal.rowan.imgur.data.network.NetworkState
import personal.rowan.imgur.databinding.ListItemFeedBinding
import personal.rowan.imgur.databinding.ListItemNetworkStateBinding

/**
 * Created by Rowan Hall
 */
class FeedAdapter : PagedListAdapter<PopulatedGallery, FeedViewHolder>(FeedDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_FEED = 0
        private const val VIEW_TYPE_NETWORK = 1
    }

    private var networkState: NetworkState? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        return when(viewType) {
            VIEW_TYPE_NETWORK -> NetworkStateViewHolder(ListItemNetworkStateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> GalleryViewHolder(ListItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        when(getItemViewType(position)) {
            VIEW_TYPE_NETWORK -> (holder as NetworkStateViewHolder).bind(networkState!!)
            VIEW_TYPE_FEED -> (holder as GalleryViewHolder).bind(getItem(position)!!)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == itemCount - 1)
            VIEW_TYPE_NETWORK else
            VIEW_TYPE_FEED
    }

    private fun hasExtraRow() = networkState != null && networkState != NetworkState.LOADED

    override fun getItemCount(): Int {
        return super.getItemCount() + if (hasExtraRow()) 1 else 0
    }

    fun setNetworkState(newNetworkState: NetworkState?) {
        val previousState = networkState
        val hadExtraRow = hasExtraRow()
        networkState = newNetworkState
        val hasExtraRow = hasExtraRow()
        if (hadExtraRow != hasExtraRow) {
            if (hadExtraRow) {
                notifyItemRemoved(super.getItemCount())
            } else {
                notifyItemInserted(super.getItemCount())
            }
        } else if (hasExtraRow && previousState != newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }
}

class GalleryViewHolder(private val binding: ListItemFeedBinding) : FeedViewHolder(binding.root) {

    fun bind(gallery: PopulatedGallery) {
        binding.apply {
            setGallery(gallery)
            executePendingBindings()
        }
    }
}

class NetworkStateViewHolder(private val binding: ListItemNetworkStateBinding) : FeedViewHolder(binding.root) {

    fun bind(networkState: NetworkState) {
        binding.apply {
            setNetworkState(networkState)
            executePendingBindings()
        }
    }
}

abstract class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

private class FeedDiffCallback : DiffUtil.ItemCallback<PopulatedGallery>() {

    override fun areItemsTheSame(oldItem: PopulatedGallery, newItem: PopulatedGallery): Boolean {
        return oldItem.gallery?.id == newItem.gallery?.id
    }

    override fun areContentsTheSame(oldItem: PopulatedGallery, newItem: PopulatedGallery): Boolean {
        return oldItem == newItem
    }
}