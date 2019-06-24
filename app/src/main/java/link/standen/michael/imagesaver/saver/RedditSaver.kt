package link.standen.michael.imagesaver.saver

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.ImageView
import org.json.JSONArray
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Save reddit links using +".json" API.
 */
class RedditSaver(context: Context, url: String): Saver {

	private val imageUrlSaver: ImageUrlSaver?

	companion object {
		private const val TAG = "RedditSaver"
	}

	init {
		// Transform URL to API URL
		val link = url.substringBefore("?")
			.substringBefore("#") +
				"/.json"

		// Open it
		val conn = URL(link).openConnection() as HttpURLConnection
		conn.connect()
		// Read API
		val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
		Log.d(TAG, response)
		val imageLink = JSONArray(response)
			.getJSONObject(0)
			?.getJSONObject("data")
			?.getJSONArray("children")
			?.getJSONObject(0)
			?.getJSONObject("data")
			?.getString("url")
		Log.d(TAG, "Reddit image link: $imageLink")
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
