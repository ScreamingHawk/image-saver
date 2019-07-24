package link.standen.michael.imagesaver.loader

import android.content.Context
import link.standen.michael.imagesaver.data.ImageItem
import link.standen.michael.imagesaver.util.UrlHelper.getUrlEnd

class ImageUrlLoader(private val url: String, private val fname: String? = null): LoaderStrategy {

	/**
	 * Returns the URL as a ImageItem.
	 */
	override fun listImages(context: Context): List<ImageItem> =
		listOf(ImageItem(url, fname ?: getUrlEnd(url)))
}
