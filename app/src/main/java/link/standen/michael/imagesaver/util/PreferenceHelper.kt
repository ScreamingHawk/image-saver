package link.standen.michael.imagesaver.util

import android.content.Context
import androidx.preference.PreferenceManager
import link.standen.michael.imagesaver.R

object PreferenceHelper {

	/**
	 * Gets a string from the preference manager
	 */
	private fun getString(context: Context, keyId: Int, defaultId: Int) =
		PreferenceManager(context).sharedPreferences.getString(context.getString(keyId), context.getString(defaultId))

	/**
	 * Returns the folder name from the settings
	 */
	fun getFolderName(context: Context) = getString(context, R.string.setting_folder_name_key, R.string.setting_folder_name_default)

}
