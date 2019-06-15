package link.standen.michael.imagesaver.util

import android.content.Context
import android.os.Environment
import link.standen.michael.imagesaver.R
import java.io.File
import java.io.InputStream
import android.util.Log
import java.io.FileOutputStream


object StorageHelper {

	private const val TAG = "StorageHelper"

	/**
	 * Returns the folder to save into
	 * https://developer.android.com/training/data-storage/files
	 */
	fun getPublicAlbumStorageDir(context: Context): File? {
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

	/**
	 * Save the input stream to the file.
	 */
	fun saveStream(inputStream: InputStream, file: File, totalSize: Int? = null){
		// Create the file
		file.createNewFile()

		FileOutputStream(file).use { fout ->
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
}
