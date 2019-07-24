package link.standen.michael.imagesaver.loader

import android.content.Context
import android.util.Log
import link.standen.michael.imagesaver.data.ImageItem
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class ChanLoader(private val url: String): LoaderStrategy {

	companion object {
		private const val TAG = "ChanLoader"
	}

	override fun listImages(context: Context): List<ImageItem> {
		// Transform URL to API URL
		val board = url.substringAfter(".org/").substringBefore('/')
		val thread = url.substringAfter("thread/").substringBefore('/')
		val link = "https://a.4cdn.org/$board/thread/$thread.json"
		val imgBase = "https://is2.4chan.org/$board/"
		val links = mutableListOf<ImageItem>()

		// Open it
		val conn = URL(link).openConnection() as HttpURLConnection
		conn.connect()
		// Read API
		val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
		Log.d(TAG, response)
		// Store all the image links
		val postsArray = JSONObject(response)
			.getJSONArray("posts")
		repeat(postsArray?.length() ?: 0) { i ->
			postsArray?.getJSONObject(i)?.let { post ->
				if (post.has("ext")){
					val ext = post.getString("ext")
					links.add(ImageItem(
						imgBase + post.getLong("tim").toString() + ext,
						post.getString("filename") + ext
					))
				}
			}
		}
		Log.d(TAG, "Chan got ${links.size} links")
		return links
	}

}
