package link.standen.michael.imagesaver

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.nhaarman.mockito_kotlin.mock
import io.mockk.every
import io.mockk.mockkStatic
import link.standen.michael.imagesaver.saver.SaverFactory
import link.standen.michael.imagesaver.saver.UriSaver
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class SaverFactoryTest {

	companion object {
		private val context = Activity()
		private var mockUri: Uri = mock()

		private const val mimeImagePrefix = "image/"
		private const val mimeImagePng = "image/png"
		private const val mimeImageJpg = "image/jpeg"
		private const val mimeImageSvg = "image/svg+xml"

		private const val mimeTextPlain = "text/plain"
	}

	@Before
	fun mockLog(){
		mockkStatic(Log::class)
		every { Log.v(any(), any()) } returns 0
		every { Log.d(any(), any()) } returns 0
		every { Log.i(any(), any()) } returns 0
		every { Log.e(any(), any()) } returns 0
	}

	/**
	 * Create an intent with the given values.
	 */
	private fun createIntent(type: String, uri: Uri? = null, textExtra: String? = null): Intent{
		val intent: Intent = mock()
		Mockito.`when`(intent.action).thenReturn(Intent.ACTION_SEND)
		Mockito.`when`(intent.type).thenReturn(type)
		Mockito.`when`(intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)).thenReturn(uri)
		Mockito.`when`(intent.getStringExtra(Intent.EXTRA_TEXT)).thenReturn(textExtra)
		return intent
	}

	/**
	 * Returns the kotlin class of an object
	 */
	private fun toClass(c: Any?) = c?.javaClass?.kotlin

	@Test
	fun createSaver_Null() {
		// Image without uri is null
		assertNull(toClass(SaverFactory().createSaver(context, createIntent(mimeImagePng))))
		assertNull(toClass(SaverFactory().createSaver(context, createIntent(mimeImageJpg))))
		assertNull(toClass(SaverFactory().createSaver(context, createIntent(mimeImageSvg))))
		// Text without extra is null
		assertNull(toClass(SaverFactory().createSaver(context, createIntent(mimeTextPlain))))
	}

	@Test
	fun createSaver_Uri() {
		// All image types with uri
		assertNotNull(mockUri)
		assertEquals(UriSaver::class, toClass(SaverFactory().createSaver(context, createIntent(mimeImagePrefix, mockUri))))
		assertEquals(UriSaver::class, toClass(SaverFactory().createSaver(context, createIntent(mimeImagePng, mockUri))))
		assertEquals(UriSaver::class, toClass(SaverFactory().createSaver(context, createIntent(mimeImageJpg, mockUri))))
		assertEquals(UriSaver::class, toClass(SaverFactory().createSaver(context, createIntent(mimeImageSvg, mockUri))))
	}
}
