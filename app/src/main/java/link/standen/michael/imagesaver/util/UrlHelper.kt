package link.standen.michael.imagesaver.util

import android.util.Log
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

object UrlHelper {

	private const val TAG = "UrlHelper"

	/**
	 * Replace http with https
	 */
	fun useHttps(url: String) =
			url.replace("http://", "https://")

	/**
	 * Resolves redirects
	 * @return The URL after resolving redirects, or the input parameter
	 */
	fun resolveRedirects(url: String): String {
		Log.d(TAG, "Checking for redirects for $url")

		var u = url
		try {
			var conn = URL(u).openConnection() as HttpURLConnection
			conn.connect()
			var code = conn.responseCode
			while (code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP){
				// Follow redirects
				conn.disconnect()
				u = conn.getHeaderField("Location")
				Log.d(TAG, "Following redirect to $u")
				conn = URL(url).openConnection() as HttpURLConnection
				conn.connect()
				code = conn.responseCode
			}
			Log.d(TAG, "Final status code: $code")
		} catch (e: MalformedURLException){
			// Something went wrong, return original URL
			Log.e(TAG, "Error resolving redirects", e)
			return url
		}
		return u
	}

}
