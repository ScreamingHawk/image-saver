package link.standen.michael.imagesaver.util

import android.content.Intent
import android.net.Uri
import android.util.Log
import link.standen.michael.imagesaver.saver.*

/**
 * A factory to create the relevant saver
 */
object SaverFactory {

	private const val TAG = "SaverFactory"
	private const val IMGUR_REGEX = """^https?:\/\/[m.]?imgur\.com\/[0-z\/]+"""
	private const val REDDIT_REGEX = """^https?:\/\/[0-z]*.?reddit\.com\/[^\s]+"""
	private const val TWITTER_REGEX = """^https?:\/\/twitter\.com\/[0-z]+/status/[0-z]+"""
	private const val IMAGE_URL_REGEX = """.*(?i)(png|jpg|jpeg|gif)(\?[^\s]*)?$"""

	/**
	 * Create the relevant saver
	 */
	fun createSaver(intent: Intent): SaverStrategy? {
		if (intent.action == Intent.ACTION_SEND){
			if (intent.type?.startsWith("image/") == true) {
				(intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM))?.let {
					Log.i(TAG, "Using UriSaver")
					return UriSaver(it)
				}
			}
			// Get text
			IntentHelper.getIntentText(intent)?.let { originalUrl ->
				if (UrlHelper.isUrl(originalUrl)) {
					Log.i(TAG, "Saving Url: $originalUrl")
					val url = UrlHelper.resolveRedirects(UrlHelper.useHttps(originalUrl))
					Log.d(TAG, "Converted: $url")
					// Find match
					if (regexMatches(
							IMGUR_REGEX,
							url
						)
					) {
						Log.i(TAG, "Using ImgurSaver")
						return ImgurSaver(url)
					}
					if (regexMatches(
							REDDIT_REGEX,
							url
						)
					) {
						Log.i(TAG, "Using RedditSaver")
						return RedditSaver(url)
					}
					if (regexMatches(
							IMAGE_URL_REGEX,
							url
						)
					) {
						Log.i(TAG, "Using ImageUrlSaver")
						return ImageUrlSaver(url)
					}
					if (regexMatches(
							TWITTER_REGEX,
							url
						)
					){
						Log.i(TAG, "Using TwitterSaver")
						return TwitterSaver(url)
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
