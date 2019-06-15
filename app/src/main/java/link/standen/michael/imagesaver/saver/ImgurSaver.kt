package link.standen.michael.imagesaver.saver

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.ImageView
import link.standen.michael.imagesaver.activity.SaverActivity
import link.standen.michael.imagesaver.util.StorageHelper
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.net.HttpURLConnection
import java.net.URL

class ImgurSaver(private val context: Context, url: String): Saver {

	private val imageLink: String?

	companion object {
		private const val TAG = "ImgurSaver"
		private const val CLIENT_ID = "a14848c980d9b25"
	}

	init {
		// Create URL
		val link = url.replaceBefore("imgur.com", "https://api.")
			.replace("imgur.com", "imgur.com/3/")

		// Open it
		val conn = URL(link).openConnection() as HttpURLConnection
		conn.setRequestProperty("Authorization", "Client-ID $CLIENT_ID")
		conn.connect()
		// Read API
		val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
		Log.d(TAG, response)
		imageLink = JSONObject(response)
			.getJSONObject("data")
			?.getJSONArray("images")
			?.getJSONObject(0)
			?.getString("link")
	}

	override fun loadImage(view: ImageView, activity: Activity) {
		val conn = URL(imageLink).openConnection() as HttpURLConnection
		conn.connect()
		val bitmap = BitmapFactory.decodeStream(conn.inputStream)
		activity.runOnUiThread {
			view.setImageBitmap(bitmap)
		}
	}

	/**
	 * Save the shared uri
	 */
	override fun save(): Boolean {
		var success = false

		val file = File(StorageHelper.getPublicAlbumStorageDir(context), getFilename())

		val conn = URL(imageLink).openConnection() as HttpURLConnection
		conn.setRequestProperty("Authorization", "Client-ID $CLIENT_ID")
		conn.connect()
		conn.inputStream.use { bis ->
			StorageHelper.saveStream(bis, file)
			success = true
		}

		return success
	}

	/**
	 * Creates filename from the api or uses default
	 */
	private fun getFilename() =
			imageLink?.replaceBeforeLast("/", "")
				?.replace("/", "")
				?: SaverActivity.DEFAULT_FILENAME
}
