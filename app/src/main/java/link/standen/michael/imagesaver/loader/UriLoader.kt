package link.standen.michael.imagesaver.loader

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import link.standen.michael.imagesaver.data.ImageItem
import link.standen.michael.imagesaver.util.StorageHelper

class UriLoader(private val uri: Uri): LoaderStrategy {

	companion object {
		private const val TAG = "UriLoader"
	}

	override fun listImages(context: Context): List<ImageItem> =
		listOf(ImageItem(link = null, uri = uri, fname = getFilename(context)))

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
			Log.w(TAG, "Unable to get filename, using default")
			fname = StorageHelper.DEFAULT_FILENAME
		}
		Log.d(TAG, "Filename: $fname")
		return fname!!
	}

}
