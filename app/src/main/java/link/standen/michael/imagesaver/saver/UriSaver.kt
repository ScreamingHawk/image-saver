package link.standen.michael.imagesaver.saver

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import android.widget.ImageView
import link.standen.michael.imagesaver.activity.SaverActivity
import link.standen.michael.imagesaver.manager.ProgressManager
import link.standen.michael.imagesaver.util.StorageHelper
import java.io.File
import java.io.FileOutputStream

class UriSaver(private val context: Context, private val uri: Uri): SaverStrategy {

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
	override fun save(): Boolean {
		var success = false
		FileOutputStream(File(StorageHelper.getPublicAlbumStorageDir(context), getFilename())).use { fout ->
			context.contentResolver.openInputStream(uri)?.buffered()?.use {
				fout.write(it.readBytes())
				success = true
			}
		}
		return success
	}

	/**
	 * Get the filename from the uri
	 */
	private fun getFilename(): String {
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
