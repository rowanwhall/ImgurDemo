package personal.rowan.imgur.data.db.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import personal.rowan.imgur.data.network.model.ImageDto

/**
 * Created by Rowan Hall
 */
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
) {
    constructor(dto: ImageDto, galleryId: String) : this(
        dto.id,
        dto.title,
        dto.description,
        dto.datetime,
        dto.type,
        dto.animated,
        dto.width,
        dto.height,
        dto.size,
        dto.views,
        dto.bandwidth,
        dto.favorite,
        dto.isAd,
        dto.link,
        galleryId
    )
}