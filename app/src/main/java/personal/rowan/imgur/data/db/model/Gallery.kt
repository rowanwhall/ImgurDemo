package personal.rowan.imgur.data.db.model

import androidx.room.*
import personal.rowan.imgur.data.GalleryArguments
import personal.rowan.imgur.data.network.model.GalleryDto

/**
 * Created by Rowan Hall
 */
@Entity(tableName = "gallery")
data class Gallery(
    @PrimaryKey val id: String,
    val title: String,
    val description: String?,
    val datetime: Long,
    val cover: String?,
    val coverWidth: Int,
    val coverHeight: Int,
    val accountUrl: String,
    val accountId: String,
    val privacy: String?,
    val layout: String?,
    val views: Int,
    val link: String,
    val ups: Int,
    val downs: Int,
    val points: Int,
    val score: Int,
    val isAlbum: Boolean,
    val favorite: Boolean,
    val nsfw: Boolean,
    val section: String?,
    val commentCount: Int,
    val favoriteCount: Int,
    val topic: String?,
    val topicId: Int,
    val imagesCount: Int,
    val inGallery: Boolean,
    val isAd: Boolean,
    val sectionArgument: String
) {
    constructor(dto: GalleryDto, arguments: GalleryArguments) :
            this(
                dto.id,
                dto.title,
                dto.description,
                dto.datetime,
                dto.cover,
                dto.coverWidth,
                dto.coverHeight,
                dto.accountUrl,
                dto.accountId,
                dto.privacy,
                dto.layout,
                dto.views,
                dto.link,
                dto.ups,
                dto.downs,
                dto.points,
                dto.score,
                dto.isAlbum,
                dto.favorite,
                dto.nsfw,
                dto.section,
                dto.commentCount,
                dto.favoriteCount,
                dto.topic,
                dto.topicId,
                dto.imagesCount,
                dto.inGallery,
                dto.isAd,
                arguments.section.requestString
            )
}

data class PopulatedGallery(
    @Embedded
    var gallery: Gallery? = null,
    @Relation(parentColumn = "id", entityColumn = "gallery")
    var images: List<Image> = listOf()
)