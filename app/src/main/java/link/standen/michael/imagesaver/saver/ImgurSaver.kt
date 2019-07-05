package link.standen.michael.imagesaver.saver

import android.app.Activity
import android.util.Log
import android.widget.ImageView
import link.standen.michael.imagesaver.manager.GalleryManager
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class ImgurSaver(url: String): EnhancedImageUrlSaver(), GalleryManager {

	companion object {
		private const val TAG = "ImgurSaver"
		private const val CLIENT_ID = "a14848c980d9b25"
	}

	private val imageList = mutableListOf<String>()
	private var currentIndex = 0

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
		// Store all the image links
		val imagesArray = JSONObject(response)
			.getJSONObject("data")
			?.getJSONArray("images")
		repeat(imagesArray?.length() ?: 0) { i ->
			imagesArray?.getJSONObject(i)?.getString("link")?.let {
				imageList.add(it)
			}
		}
		Log.d(TAG, "Imgur got ${imageList.size} links")
		// Create ImageUrlSaver with the first image
		if (imageList.isNotEmpty()){
			imageUrlSaver = ImageUrlSaver(imageList[0])
		}
	}

	/**
	 * Returns this as the gallery manager
	 */
	override fun getGalleryManager(): GalleryManager? = this

	/**
	 * Returns true if there is a next image
	 */
	override fun hasNextImage() = (currentIndex + 1) < imageList.size

	/**
	 * Returns true if there is a previous image
	 */
	override fun hasPreviousImage() = currentIndex > 0

	/**
	 * Display the next image
	 */
	override fun showNextImage(view: ImageView, activity: Activity){
		if (hasNextImage()) {
			currentIndex++
			imageUrlSaver = ImageUrlSaver(imageList[currentIndex])
		}
		loadImage(view, activity)
	}

	/**
	 * Display the previous image
	 */
	override fun showPreviousImage(view: ImageView, activity: Activity){
		if (hasPreviousImage()) {
			currentIndex--
			imageUrlSaver = ImageUrlSaver(imageList[currentIndex])
		}
		loadImage(view, activity)
	}

}
