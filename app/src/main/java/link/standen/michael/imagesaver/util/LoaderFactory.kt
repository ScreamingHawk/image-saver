package link.standen.michael.imagesaver.util

import android.content.Intent
import android.net.Uri
import android.util.Log
import link.standen.michael.imagesaver.loader.*

/**
 * A factory to create the relevant saver
 */
object LoaderFactory {

	private const val TAG = "LoaderFactory"
	private const val IMGUR_REGEX = """^https?:\/\/[m.]?imgur\.com\/[0-z\/]+"""
	private const val REDDIT_REGEX = """^https?:\/\/[0-z]*.?reddit\.com\/[^\s]+"""
	private const val CHAN_REGEX = """^https?:\/\/boards\.4chan\.org\/[0-z]+\/thread\/[0-9]+(\/[^\s]*)?"""
	private const val TWITTER_REGEX = """^https?:\/\/twitter\.com\/[0-z]+/status/[0-z]+"""
	private const val IMAGE_URL_REGEX = """.*(?i)(png|jpg|jpeg|gif)(\?[^\s]*)?$"""

	/**
	 * Create the relevant loader
	 */
	fun createLoader(intent: Intent): LoaderStrategy? {
		if (intent.action == Intent.ACTION_SEND){
			if (intent.type?.startsWith("image/") == true) {
				(intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM))?.let {
					Log.i(TAG, "Using UriSaver")
					return UriLoader(it)
				}
			}
			// Get text
			IntentHelper.getIntentText(intent)?.let { originalUrl ->
				if (UrlHelper.isUrl(originalUrl)) {
					Log.i(TAG, "Saving Url: $originalUrl")
					val url = UrlHelper.resolveRedirects(UrlHelper.useHttps(originalUrl))
					Log.d(TAG, "Converted: $url")
					// Find match
					if (regexMatches(IMGUR_REGEX, url)) {
						Log.i(TAG, "Using ImgurLoader")
						return ImgurLoader(url)
					}
					if (regexMatches(REDDIT_REGEX, url)) {
						Log.i(TAG, "Using RedditLoader")
						return RedditLoader(url)
					}
					if (regexMatches(CHAN_REGEX, url)) {
						Log.i(TAG, "Using ChanLoader")
						return ChanLoader(url)
					}
					if (regexMatches(IMAGE_URL_REGEX, url)) {
						Log.i(TAG, "Using ImageUrlLoader")
						return ImageUrlLoader(url)
					}
					if (regexMatches(TWITTER_REGEX, url)) {
						Log.i(TAG, "Using TwitterLoader")
						return TwitterLoader(url)
					}
				}
			}
		}
		return null
	}

	/**
	 * Test string matches regex.
	 */
	private fun regexMatches(regex: String, url: String) =
		!(regex.toRegex().matchEntire(url)?.groups?.isEmpty() ?: true)
}
