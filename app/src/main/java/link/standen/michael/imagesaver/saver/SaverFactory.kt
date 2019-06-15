package link.standen.michael.imagesaver.saver

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import android.util.Log
import link.standen.michael.imagesaver.util.UrlHelper

/**
 * A factory to create the relevant saver
 */
class SaverFactory {

	companion object {
		const val TAG = "SaverFactory"
		const val IMGUR_REGEX = """https?://[m.]?imgur.com/[0-z/]+"""
	}

	/**
	 * Create the relevant saver
	 */
	fun createSaver(context: Context, intent: Intent): Saver? {
		if (intent.action == Intent.ACTION_SEND){
			if (intent.type?.startsWith("image/") == true) {
				(intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
					Log.i(TAG, "Using UriSaver")
					return UriSaver(context, it)
				}
			}
			if (intent.type == "text/plain"){
				(intent.getStringExtra(Intent.EXTRA_TEXT))?.let { originalUrl ->
					val url = UrlHelper.resolveRedirects(context, UrlHelper.useHttps(originalUrl))
					// Find match
					if (regexMatches(IMGUR_REGEX, url)){
						Log.i(TAG, "Using ImgurSaver")
						return ImgurSaver(context, url)
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
