package link.standen.michael.imagesaver.saver

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Parcelable

/**
 * A factory to create the relevant saver
 */
class SaverFactory {

	/**
	 * Create the relevant saver
	 */
	fun createSaver(context: Context, intent: Intent): Saver? {
		if (intent.action == Intent.ACTION_SEND && intent.type?.startsWith("image/") == true) {
			(intent.getParcelableExtra<Parcelable>(Intent.EXTRA_STREAM) as? Uri)?.let {
				return UriSaver(context, it)
			}
		}
		return null
	}
}
