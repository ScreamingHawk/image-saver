package link.standen.michael.imagesaver.saver

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.ImageView
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class ImgurSaver(context: Context, url: String): SaverStrategy {

	private val imageUrlSaver: ImageUrlSaver?

	companion object {
		private const val TAG = "ImgurSaver"
		private const val CLIENT_ID = "a14848c980d9b25"
	}

	init {
		// Transform URL to API URL
		val link = url.replaceBefore("imgur.com", "https://api.")
			.replace("imgur.com", "imgur.com/3/")

		// Open it
		val conn = URL(link).openConnection() as HttpURLConnection
		conn.setRequestProperty("Authorization", "Client-ID $CLIENT_ID")
		conn.connect()
		// Read API
		val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
		Log.d(TAG, response)
		val imageLink = JSONObject(response)
			.getJSONObject("data")
			?.getJSONArray("images")
			?.getJSONObject(0)
			?.getString("link")
		Log.d(TAG, "Imgur image link: $imageLink")
		// Create ImageUrlSaver
		if (imageLink == null) {
			imageUrlSaver = null
		} else {
			imageUrlSaver = ImageUrlSaver(context, imageLink)
		}
	}

	/**
	 * Use the imageUrlSaver to load the image
	 */
	override fun loadImage(view: ImageView, activity: Activity): Boolean = imageUrlSaver?.loadImage(view, activity) ?: false

	/**
	 * Use the imageUrlSaver to load the image
	 */
	override fun save(): Boolean = imageUrlSaver?.save() ?: false

}
