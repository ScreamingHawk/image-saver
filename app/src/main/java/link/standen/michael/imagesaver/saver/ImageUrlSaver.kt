package link.standen.michael.imagesaver.saver

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import link.standen.michael.imagesaver.util.StorageHelper
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class ImageUrlSaver(private val context: Context, private val url: String): Saver {

	private var streamBytes: ByteArray? = null

	companion object {
		private const val TAG = "ImageUrlSaver"
	}

	override fun loadImage(view: ImageView, activity: Activity): Boolean {
		val conn = URL(url).openConnection() as HttpURLConnection
		conn.connect()
		streamBytes = conn.inputStream.readBytes()
		val bitmap = BitmapFactory.decodeStream(streamBytes?.inputStream())
		if (bitmap == null){
			Log.e(TAG, "Unable to load image")
			return false
		}
		activity.runOnUiThread {
			view.setImageBitmap(bitmap)
		}
		return true
	}

	/**
	 * Save the shared url
	 */
	override fun save(): Boolean {
		var success = false

		val file = File(StorageHelper.getPublicAlbumStorageDir(context), getFilename())

		if (streamBytes == null) {
			val conn = URL(url).openConnection() as HttpURLConnection
			conn.connect()
			conn.inputStream.use { bis ->
				StorageHelper.saveStream(bis, file)
				success = true
			}
		} else {
			streamBytes?.inputStream()?.use { bis ->
				StorageHelper.saveStream(bis, file)
				success = true
			}
		}

		return success
	}

	/**
	 * Creates filename from the link
	 */
	private fun getFilename() =
			url.replaceBeforeLast("/", "")
				.replace("/", "")
}
