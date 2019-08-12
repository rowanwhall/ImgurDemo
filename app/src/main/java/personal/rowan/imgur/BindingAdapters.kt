package personal.rowan.imgur

import android.view.View
import android.view.View.*
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import personal.rowan.imgur.data.db.model.PopulatedGallery

/**
 * Created by Rowan Hall
 */

@BindingAdapter("visibleOrGone")
fun View.setVisibleOrGone(show: Boolean) {
    visibility = if (show) VISIBLE else GONE
}

@BindingAdapter("visible")
fun View.setVisible(show: Boolean) {
    visibility = if (show) VISIBLE else INVISIBLE
}

@BindingAdapter("textOrGone")
fun TextView.setTextOrGone(text: String?) {
    if (text.isNullOrEmpty()) {
        visibility = GONE
    } else {
        setText(text)
        visibility = VISIBLE
    }
}

@BindingAdapter("imageUrl")
fun ImageView.setImageUrl(url: String?) {
    GlideApp.with(context)
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
        .optionalCenterCrop()
        .placeholder(android.R.color.darker_gray)
        .error(android.R.color.holo_red_dark) // todo: get real error and placeholder assets
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

@BindingAdapter("galleryImage")
fun ImageView.setGalleryImage(gallery: PopulatedGallery) {
    val images = gallery.images
    if (images.isNotEmpty()) {
        setImageUrl(images[0].link)
    } else {
        setImageUrl(gallery.gallery!!.link)
    }
}