package link.standen.michael.imagesaver.util

import android.content.Intent

object IntentHelper {

	fun getIntentText(intent: Intent): String? {
		if (intent.type == "text/plain"){
			return intent.getStringExtra(Intent.EXTRA_TEXT)
		}
		return null
	}

}
