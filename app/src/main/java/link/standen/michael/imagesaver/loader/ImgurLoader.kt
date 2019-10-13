package link.standen.michael.imagesaver.loader

import android.content.Context
import android.util.Log
import link.standen.michael.imagesaver.data.ImageItem
import link.standen.michael.imagesaver.util.UrlHelper.getUrlEnd
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class ImgurLoader(private val url: String): LoaderStrategy {

	companion object {
		private const val TAG = "ImgurLoader"
		private const val CLIENT_ID = "a14848c980d9b25"
	}

	override fun listImages(context: Context): List<ImageItem> {
		val links = mutableListOf<ImageItem>()

		// Transform URL to API URL
		val link = url.replaceBefore("imgur.com", "https://api.")
			.replace("imgur.com", "imgur.com/3")
			.replace("/a/", "/album/") // Album is shortened

		// Open it
		val conn = URL(link).openConnection() as HttpURLConnection
		conn.setRequestProperty("Authorization", "Client-ID $CLIENT_ID")
		conn.connect()
		// Read API
		val response = conn.inputStream.bufferedReader().use(BufferedReader::readText)
		Log.d(TAG, response)
		// Store all the image links
		JSONObject(response)
			.getJSONObject("data")
			?.getJSONArray("images")?.let { images ->
				repeat(images.length()) { i ->
					images.getJSONObject(i)?.getString("link")?.let {
						links.add(ImageItem(it, getUrlEnd(it)))
					}
				}
			}

		Log.d(TAG, "Imgur got ${links.size} links")
		return links
	}

}
