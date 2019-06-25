package link.standen.michael.imagesaver.saver

import android.app.Activity
import android.content.Context
import android.net.Credentials
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Save twitter links.
 * https://developer.twitter.com/en/docs/tweets/post-and-engage/api-reference/get-statuses-show-id
 * GET https://api.twitter.com/1.1/statuses/show.json?id=<id>
 */
class TwitterSaver(context: Context, url: String): Saver {

	private var imageUrlSaver: ImageUrlSaver? = null

	@Suppress("SpellCheckingInspection")
	companion object {
		private const val TAG = "TwitterSaver"
		private const val API_KEY = "A0RdTD8JKUbX6HMN1gnsLhy8X"
		private const val API_SECRET = "roaDsuhoj6QTGckm48US06aI6r8doL3qGOLMzNqmDeekjpdcZn"
	}

	init {
		// Authenticate
		val authConn = URL("https://api.twitter.com/oauth2/token").openConnection() as HttpURLConnection
		val apiBasic = Base64.encodeToString("$API_KEY:$API_SECRET".toByteArray(), Base64.NO_WRAP)
		authConn.setRequestProperty("Authorization", "Basic $apiBasic")
		authConn.requestMethod = "POST"
		authConn.outputStream.bufferedWriter().use { fout ->
			fout.write("grant_type=client_credentials")
		}
		authConn.connect()
		val authResponse = authConn.inputStream.bufferedReader().use(BufferedReader::readText)
		authConn.disconnect()
		val token = JSONObject(authResponse).getString("access_token")

		if (token.isNullOrBlank()){
			Log.e(TAG, "Unable to authenticate with Twitter")
			Log.e(TAG, authResponse)
		} else {
			// Transform URL to API URL
			val id = url.substringAfter("status/", "").substringBefore("?")
			val link = "https://api.twitter.com/1.1/statuses/show.json?id=$id&tweet_mode=extended"

			// Open it
			val conn = URL(link).openConnection() as HttpURLConnection
			conn.setRequestProperty("Authorization", "Bearer $token")
			conn.connect()
			// Read API
			val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
			conn.disconnect()
			Log.d(TAG, response)
			val imageLink = JSONObject(response)
				?.getJSONObject("entities")
				?.getJSONArray("media")
				?.getJSONObject(0)
				?.getString("media_url_https")
			Log.d(TAG, "Twitter image link: $imageLink")
			// Create ImageUrlSaver
			if (!imageLink.isNullOrBlank()) {
				imageUrlSaver = ImageUrlSaver(context, imageLink)
			}
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
