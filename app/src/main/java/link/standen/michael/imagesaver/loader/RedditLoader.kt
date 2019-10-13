package link.standen.michael.imagesaver.loader

import android.content.Context
import android.util.Log
import link.standen.michael.imagesaver.data.ImageItem
import link.standen.michael.imagesaver.util.UrlHelper.getUrlEnd
import org.json.JSONArray
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Save reddit links using +".json" API.
 */
class RedditLoader(private val url: String): LoaderStrategy {

	companion object {
		private const val TAG = "RedditLoader"
	}

	override fun listImages(context: Context): List<ImageItem> {
		val items = mutableListOf<ImageItem>()

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
		JSONArray(response)
			.getJSONObject(0)
			?.getJSONObject("data")
			?.getJSONArray("children")?.let { data ->
				repeat(data.length()) { i ->
					data.getJSONObject(i)
						?.getJSONObject("data")
						?.getString("url")?.let {
							items.add(ImageItem(it, getUrlEnd(it)))
						}
				}
			}
		Log.d(TAG, "Reddit got ${items.size} links")
		// Fail over to blank
		return items
	}

}
