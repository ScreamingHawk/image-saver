package link.standen.michael.imagesaver.util

import android.content.Context
import android.net.Uri
import android.os.Environment
import java.io.File
import java.io.InputStream
import android.util.Log
import java.io.OutputStream

object StorageHelper {

	private const val TAG = "StorageHelper"

	/**
	 * Returns the folder to save into
	 * https://developer.android.com/training/data-storage/files
	 */
	fun getPublicAlbumStorageDir(context: Context): Uri {
		val folder = File(
			Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), PreferenceHelper.getFolderName(context))
		// Make directory if doesn't exist
		if (folder.mkdirs()){
			// Create .nomedia file if required
			if (PreferenceHelper.getNoMedia(context)){
				File(folder.path, ".nomedia").createNewFile()
			}
		}
		return Uri.fromFile(folder)
	}

	/**
	 * Create or delete the nomedia file
	 */
	fun updateNoMedia(context: Context, create: Boolean? = null){
		val doCreate = create ?: PreferenceHelper.getNoMedia(context)
		val nomedia = File(getPublicAlbumStorageDir(context).path, ".nomedia")
		if (doCreate){
			nomedia.createNewFile()
		} else {
			nomedia.delete()
		}
	}

	/**
	 * Save the input stream to the output stream.
	 */
	fun saveStream(inputStream: InputStream, fout: OutputStream, totalSize: Int? = null){
		var downloadedSize = 0
		val buffer = ByteArray(1024)
		var bufferLength = inputStream.read(buffer)
		while (bufferLength > 0) {
			fout.write(buffer, 0, bufferLength)
			downloadedSize += bufferLength
			if (totalSize != null){
				val percent = 100 * downloadedSize / totalSize
				Log.d(TAG, "Progress: $downloadedSize / $totalSize ($percent%)")
			}
			bufferLength = inputStream.read(buffer)
		}
	}
}
