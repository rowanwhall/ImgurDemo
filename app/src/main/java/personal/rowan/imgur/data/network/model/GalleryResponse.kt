package personal.rowan.imgur.data.network.model

import com.google.gson.annotations.SerializedName

/**
 * Created by Rowan Hall
 */
data class GalleryResponse(val data: List<GalleryDTO>)

data class GalleryDTO(
    val id: String,
    val title: String,
    val description: String,
    val datetime: Long,
    val cover: String,
    @SerializedName("cover_width") val coverWidth: Int,
    @SerializedName("cover_height") val coverHeight: Int,
    @SerializedName("account_url") val accountUrl: String,
    @SerializedName("account_id") val accountId: String,
    val privacy: String,
    val layout: String,
    val views: Int,
    val link: String,
    val ups: Int,
    val downs: Int,
    val points: Int,
    val score: Int,
    @SerializedName("is_album") val isAlbum: Boolean,
    val favorite: Boolean,
    val nsfw: Boolean,
    val section: String,
    @SerializedName("comment_count") val commentCount: Int,
    @SerializedName("favorite_count") val favoriteCount: Int,
    val topic: String,
    @SerializedName("topic_id") val topicId: Int,
    @SerializedName("images_count") val imagesCount: Int,
    @SerializedName("in_gallery") val inGallery: Boolean,
    @SerializedName("is_ad") val isAd: Boolean,
    val images: List<ImageDTO>
)

data class ImageDTO(
    val id: String,
    val title: String,
    val description: String,
    val datetime: Long,
    val type: String,
    val animated: Boolean,
    val width: Int,
    val height: Int,
    val size: Int,
    val views: Int,
    val bandwidth: Long,
    val favorite: Boolean,
    @SerializedName("is_ad") val isAd: Boolean,
    val link: String
)