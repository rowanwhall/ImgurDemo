package personal.rowan.imgur.data.db.model

import android.os.Parcel
import android.os.Parcelable
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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: ""
    )

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeLong(datetime)
        parcel.writeString(cover)
        parcel.writeInt(coverWidth)
        parcel.writeInt(coverHeight)
        parcel.writeString(accountUrl)
        parcel.writeString(accountId)
        parcel.writeString(privacy)
        parcel.writeString(layout)
        parcel.writeInt(views)
        parcel.writeString(link)
        parcel.writeInt(ups)
        parcel.writeInt(downs)
        parcel.writeInt(points)
        parcel.writeInt(score)
        parcel.writeByte(if (isAlbum) 1 else 0)
        parcel.writeByte(if (favorite) 1 else 0)
        parcel.writeByte(if (nsfw) 1 else 0)
        parcel.writeString(section)
        parcel.writeInt(commentCount)
        parcel.writeInt(favoriteCount)
        parcel.writeString(topic)
        parcel.writeInt(topicId)
        parcel.writeInt(imagesCount)
        parcel.writeByte(if (inGallery) 1 else 0)
        parcel.writeByte(if (isAd) 1 else 0)
        parcel.writeString(sectionArgument)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Gallery> {
        override fun createFromParcel(parcel: Parcel): Gallery {
            return Gallery(parcel)
        }

        override fun newArray(size: Int): Array<Gallery?> {
            return arrayOfNulls(size)
        }
    }
}

data class PopulatedGallery(
    @Embedded
    var gallery: Gallery? = null,
    @Relation(parentColumn = "id", entityColumn = "gallery")
    var images: List<Image> = listOf()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(Gallery::class.java.classLoader),
        parcel.createTypedArrayList(Image) ?: listOf()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(gallery, flags)
        parcel.writeTypedList(images)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PopulatedGallery> {
        override fun createFromParcel(parcel: Parcel): PopulatedGallery {
            return PopulatedGallery(parcel)
        }

        override fun newArray(size: Int): Array<PopulatedGallery?> {
            return arrayOfNulls(size)
        }
    }
}