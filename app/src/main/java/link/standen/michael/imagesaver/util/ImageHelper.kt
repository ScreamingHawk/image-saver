package link.standen.michael.imagesaver.util

import android.app.Activity
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.documentfile.provider.DocumentFile
import link.standen.michael.imagesaver.data.ImageItem
import link.standen.michael.imagesaver.manager.ProgressManager
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

/**
 * Loads into into views, saves images
 */
object ImageHelper {

	private const val TAG = "ImageHelper"

	/**
	 * Loads the image while updating the progress bar.
	 */
	fun loadImage(item: ImageItem, view: ImageView, activity: Activity): Boolean {
		if (item.uri != null){
			activity.runOnUiThread {
				view.setImageURI(item.uri)
			}
			return true
		}
		if (item.bytes == null) {
			loadImageBytes(item, activity)
		}
		item.bytes?.let { bytes ->
			// Convert to image
			val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
			if (bitmap == null){
				Log.e(TAG, "Unable to load image")
				return false
			}
			activity.runOnUiThread {
				view.setImageBitmap(bitmap)
			}
			return true
		}
		// Fail
		return false
	}

	/**
	 * Loads the bytes from the URL.
	 */
	private fun loadImageBytes(item: ImageItem, activity: Activity) {
		if (item.link == null){
			// Nothing to load
			return
		}
		Log.d(TAG, "Loading image from ${item.link}")
		val conn = URL(item.link).openConnection() as HttpURLConnection
		conn.connect()
		val total = conn.contentLength
		val progress = ProgressManager(activity, total)
		val outStream = ByteArrayOutputStream()
		// Read the stream
		conn.inputStream.use { inStream ->
			val buff = ByteArray(1024)
			var count = 0
			var len = inStream.read(buff)
			while (len > 0) {
				// Update the progress bar
				count += len
				progress.updateProgress(count)
				// Write bytes
				outStream.write(buff, 0, len)
				len = inStream.read(buff)
			}
		}
		progress.completed()
		// Convert to byte array
		item.bytes = outStream.toByteArray()
	}

	/**
	 * Save the shared url
	 */
	fun saveImage(item: ImageItem, activity: Activity, folder: Uri?): Boolean {
		var success = false

		val mime = MimeTypeMap.getFileExtensionFromUrl(item.fname)

		val fname = if (PreferenceHelper.isRandomFilename(activity)){
			UUID.randomUUID().toString() + '.' + mime
		} else item.fname

		// https://stackoverflow.com/a/26765884/2027146
		if (folder != null) {
			DocumentFile.fromTreeUri(activity, folder)?.createFile(
				mime,
				fname
			)?.let { file ->
				activity.contentResolver.openOutputStream(file.uri)?.use { fout ->
					success = saveToStream(item, fout, activity)
				}
			} ?: run {
				Log.e(TAG, "Unable to create file $fname at ${folder.path}")
			}
		} else {
			// No folder, use default
			StorageHelper.getDefaultOutputStream(activity, fname).use { fout ->
				success = saveToStream(item, fout, activity)
			}
		}

		return success
	}

	private fun saveToStream(item: ImageItem, fout: OutputStream, activity: Activity): Boolean{
		var success = false

		if (item.bytes == null) {
			loadImageBytes(item, activity)
		}
		item.bytes?.let { bytes ->
			// Save bytes
			Log.i(TAG, "Saving bytes")
			bytes.inputStream().use { inStream ->
				StorageHelper.saveStream(inStream, fout)
				success = true
			}
		} ?: item.link?.let { url ->
			// Save from URL
			Log.i(TAG, "Saving from URL")
			val conn = URL(url).openConnection() as HttpURLConnection
			conn.connect()
			conn.inputStream.use { inStream ->
				StorageHelper.saveStream(inStream, fout)
				success = true
			}
		} ?: item.uri?.let { uri ->
			// Save Uri
			Log.i(TAG, "Saving Uri")
			success = StorageHelper.saveUri(uri, activity, fout)
		}
		return success
	}
}
