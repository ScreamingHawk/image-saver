package link.standen.michael.imagesaver.activity

import android.app.ListActivity
import link.standen.michael.imagesaver.R

/**
 * The entry point activity.
 * This activity shows the list of user selected folders.
 */
class FolderListActivity : ListActivity() {

	override fun onCreate(savedInstanceState: android.os.Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.folder_list_activity)

		val fab = findViewById<android.support.design.widget.FloatingActionButton>(R.id.fab)
		fab.setOnClickListener {
			//TODO FAB implementation
		}

		//TODO List adapter
	}
}
