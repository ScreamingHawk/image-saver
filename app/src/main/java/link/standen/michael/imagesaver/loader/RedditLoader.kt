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
			?.getJSONArray("children")
			?.getJSONObject(0) // TODO list?
			?.getJSONObject("data")
			?.getString("url")?.let {
				Log.d(TAG, "Reddit image link: $it")
				return listOf(ImageItem(it, getUrlEnd(it)))
			} ?: return listOf()
	}

}
