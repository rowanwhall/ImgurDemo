package personal.rowan.imgur.data

/**
 * Created by Rowan Hall
 */
data class GalleryArguments(val section: GallerySection, val sort: GallerySort, val source: GallerySource)

enum class GallerySection(val requestString: String) {
    HOT("hot"),
    TOP("top")
}

enum class GallerySort(val requestString: String) {
    TOP("top"),
    TIME("time"),
}

enum class GallerySource {
    NETWORK_ONLY,
    NETWORK_AND_DB
}