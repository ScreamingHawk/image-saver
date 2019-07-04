package link.standen.michael.imagesaver.saver

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ImageView
import androidx.documentfile.provider.DocumentFile
import link.standen.michael.imagesaver.activity.SaverActivity
import link.standen.michael.imagesaver.manager.ProgressManager
import link.standen.michael.imagesaver.util.StorageHelper
import java.io.File
import java.io.OutputStream

class UriSaver(private val uri: Uri): SaverStrategy {

	/**
	 * Load the image
	 */
	override fun loadImage(view: ImageView, activity: Activity): Boolean {
		view.setImageURI(uri)
		// Use progress bar with value 1 to force display
		ProgressManager(activity, 1).completed()
		return true
	}

	/**
	 * Save the shared uri
	 */
	override fun save(context: Context, folder: Uri?): Boolean {
		var success = false

		val fname = getFilename(context)
		if (folder != null) {
			// https://stackoverflow.com/a/26765884/2027146
			DocumentFile.fromTreeUri(context, folder)?.createFile(MimeTypeMap.getFileExtensionFromUrl(fname), fname)
				?.let { file ->
					context.contentResolver.openOutputStream(file.uri)?.use { fout ->
						success = saveToStream(context, fout)
					}
				}
		} else {
			// No folder, use default
			StorageHelper.getDefaultOutputStream(context, fname).use { fout ->
				success = saveToStream(context, fout)
			}
		}
		return success
	}

	private fun saveToStream(context: Context, fout: OutputStream): Boolean {
		var success = false
		context.contentResolver.openInputStream(uri)?.buffered()?.use {
			fout.write(it.readBytes())
			success = true
		}
		return success
	}

	/**
	 * Get the filename from the uri
	 */
	private fun getFilename(context: Context): String {
		// Find the actual filename
		var fname: String? = null
		if (uri.scheme == "content") {
			context.contentResolver.query(uri, null, null, null, null)?.use {
				if (it.moveToFirst()) {
					fname = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
				}
			}
		}
		if (fname.isNullOrBlank()){
			fname = uri.lastPathSegment
		}
		if (fname.isNullOrBlank()){
			Log.w(SaverActivity.TAG, "Unable to get filename, using default")
			fname = SaverActivity.DEFAULT_FILENAME
		}
		Log.d(SaverActivity.TAG, "Filename: $fname")
		return fname!!
	}

}
