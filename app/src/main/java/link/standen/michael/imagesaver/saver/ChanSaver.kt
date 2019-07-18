package link.standen.michael.imagesaver.saver

import android.app.Activity
import android.util.Log
import android.widget.ImageView
import link.standen.michael.imagesaver.data.LinkItem
import link.standen.michael.imagesaver.manager.GalleryManager
import org.json.JSONObject
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

class ChanSaver(url: String): EnhancedImageUrlSaver(), GalleryManager {

	companion object {
		private const val TAG = "ChanSaver"
	}

	private val links = mutableListOf<LinkItem>()
	private var currentIndex = 0

	init {
		// Transform URL to API URL
		val board = url.substringAfter(".org/").substringBefore('/')
		val thread = url.substringAfter("thread/").substringBefore('/')
		val link = "https://a.4cdn.org/$board/thread/$thread.json"
		val imgBase = "https://is2.4chan.org/$board/"

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
					links.add(LinkItem(
						imgBase + post.getLong("tim").toString() + ext,
						post.getString("filename") + ext
					))
				}
			}
		}
		Log.d(TAG, "Chan got ${links.size} links")
		// Create ImageUrlSaver with the first image
		if (links.isNotEmpty()){
			imageUrlSaver = ImageUrlSaver(links[0].link, links[0].fname)
		}
	}

	/**
	 * Returns this as the gallery manager if more than one image
	 */
	override fun getGalleryManager(): GalleryManager? = if (links.size > 1) this else null

	/**
	 * Returns true if there is a next image
	 */
	override fun hasNextImage() = (currentIndex + 1) < links.size

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
			imageUrlSaver = ImageUrlSaver(links[currentIndex].link, links[currentIndex].fname)
		}
		loadImage(view, activity)
	}

	/**
	 * Display the previous image
	 */
	override fun showPreviousImage(view: ImageView, activity: Activity){
		if (hasPreviousImage()) {
			currentIndex--
			imageUrlSaver = ImageUrlSaver(links[currentIndex].link, links[currentIndex].fname)
		}
		loadImage(view, activity)
	}

}
