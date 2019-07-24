package link.standen.michael.imagesaver.data

import android.net.Uri

/**
 * A data class for storing images with information
 */
data class ImageItem (
	val link: String?, // URL
	val fname: String, // Filename
	val uri: Uri? = null, // Data
	@Volatile var bytes: ByteArray? = null

) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as ImageItem

		if (link != other.link) return false
		if (fname != other.fname) return false
		if (uri != other.uri) return false

		return true
	}

	override fun hashCode(): Int {
		var result = link?.hashCode() ?: 0
		result = 31 * result + fname.hashCode()
		result = 31 * result + (uri?.hashCode() ?: 0)
		return result
	}
}
