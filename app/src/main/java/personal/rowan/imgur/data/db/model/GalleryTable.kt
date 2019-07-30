package personal.rowan.imgur.data.db.model

import androidx.room.*

/**
 * Created by Rowan Hall
 */
@Entity(tableName = "gallery")
data class Gallery(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val datetime: Long,
    val cover: String,
    val coverWidth: Int,
    val coverHeight: Int,
    val accountUrl: String,
    val accountId: String,
    val privacy: String,
    val layout: String,
    val views: Int,
    val link: String,
    val ups: Int,
    val downs: Int,
    val points: Int,
    val score: Int,
    val isAlbum: Boolean,
    val favorite: Boolean,
    val nsfw: Boolean,
    val section: String,
    val commentCount: Int,
    val favoriteCount: Int,
    val topic: String,
    val topicId: Int,
    val imagesCount: Int,
    val inGallery: Boolean,
    val isAd: Boolean
)

@Entity(
    tableName = "image",
    foreignKeys = [ForeignKey(
        entity = Gallery::class,
        parentColumns = ["id"],
        childColumns = ["gallery"]
    )]
)
data class Image(
    @PrimaryKey val id: String,
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
    val isAd: Boolean,
    val link: String,
    val gallery: String
)

class PopulatedGallery {

    @Embedded
    var gallery: Gallery? = null

    @Relation(parentColumn = "id", entityColumn = "gallery")
    var images: List<Image> = ArrayList()
}