package link.standen.michael.imagesaver.saver

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.documentfile.provider.DocumentFile
import link.standen.michael.imagesaver.manager.ProgressManager
import link.standen.michael.imagesaver.util.StorageHelper
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class ImageUrlSaver(private val url: String): SaverStrategy {

	private var streamBytes: ByteArray? = null

	companion object {
		private const val TAG = "ImageUrlSaver"
	}

	/**
	 * Loads the image while updating the progress bar.
	 */
	override fun loadImage(view: ImageView, activity: Activity): Boolean {
		val conn = URL(url).openConnection() as HttpURLConnection
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
		streamBytes = outStream.toByteArray()
		if (streamBytes == null){
			Log.e(TAG, "Unable to read stream")
			return false
		}
		// Convert to image
		val bitmap = BitmapFactory.decodeByteArray(streamBytes!!, 0, total)
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
	override fun save(context: Context, folder: Uri?): Boolean {
		var success = false

		val fname = getFilename()
		// https://stackoverflow.com/a/26765884/2027146
		if (folder != null) {
			DocumentFile.fromTreeUri(context, folder)?.createFile(
				MimeTypeMap.getFileExtensionFromUrl(fname),
				fname
			)?.let { file ->
				context.contentResolver.openOutputStream(file.uri)?.use { fout ->
					success = saveToStream(fout)
				}
			} ?: run {
				Log.e(TAG, "Unable to create file $fname at ${folder.path}")
			}
		} else {
			// No folder, use default
			StorageHelper.getDefaultOutputStream(context, fname).use { fout ->
				success = saveToStream(fout)
			}
		}

		return success
	}

	private fun saveToStream(fout: OutputStream): Boolean{
		var success = false
		if (streamBytes == null) {
			// Read from URL
			val conn = URL(url).openConnection() as HttpURLConnection
			conn.connect()
			conn.inputStream.use { inStream ->
				StorageHelper.saveStream(inStream, fout)
				success = true
			}
		} else {
			// Use stored bytes
			streamBytes?.inputStream()?.use { inStream ->
				StorageHelper.saveStream(inStream, fout)
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
