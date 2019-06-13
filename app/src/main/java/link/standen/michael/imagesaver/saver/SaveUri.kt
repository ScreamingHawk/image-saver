package link.standen.michael.imagesaver.saver

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import link.standen.michael.imagesaver.R
import link.standen.michael.imagesaver.activity.SaverActivity
import java.io.File
import java.io.FileOutputStream

class SaveUri(private val context: Context, private val uri: Uri): Saver {

	/**
	 * Save the shared uri
	 */
	override fun save(): Boolean {
		var success = false
		FileOutputStream(File(getPublicAlbumStorageDir(), getFilename())).use { fout ->
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

	/**
	 * Returns the folder to save into
	 * https://developer.android.com/training/data-storage/files
	 */
	private fun getPublicAlbumStorageDir(): File? {
		val folder = File(
			Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), context.getString(R.string.folder_name))
		// Make directory if doesn't exist
		if (folder.mkdirs()){
			// Create .nomedia file
			File(folder.path, ".nomedia").createNewFile()
		}
		return folder
	}

}
