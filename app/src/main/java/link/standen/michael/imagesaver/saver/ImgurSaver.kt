package link.standen.michael.imagesaver.saver

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.ImageView
import org.json.JSONObject
import java.io.BufferedReader
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

	override fun loadImage(view: ImageView, activity: Activity): Boolean {
		if (imageLink == null){
			return false
		}
		return ImageUrlSaver(context, imageLink).loadImage(view, activity)
	}

	/**
	 * Save the shared uri
	 */
	override fun save(): Boolean {
		if (imageLink == null){
			return false
		}
		return ImageUrlSaver(context, imageLink).save()
	}

}
