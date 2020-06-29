package personal.rowan.imgur.data.db.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
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
    )],
    indices = [Index("gallery")]
)
data class Image(
    @PrimaryKey val id: String,
    val title: String?,
    val description: String?,
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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

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

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(description)
        parcel.writeLong(datetime)
        parcel.writeString(type)
        parcel.writeByte(if (animated) 1 else 0)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeInt(size)
        parcel.writeInt(views)
        parcel.writeLong(bandwidth)
        parcel.writeByte(if (favorite) 1 else 0)
        parcel.writeByte(if (isAd) 1 else 0)
        parcel.writeString(link)
        parcel.writeString(gallery)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Image> {
        override fun createFromParcel(parcel: Parcel): Image {
            return Image(parcel)
        }

        override fun newArray(size: Int): Array<Image?> {
            return arrayOfNulls(size)
        }
    }
}