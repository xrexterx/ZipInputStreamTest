package com.xrexter.zipinputstreamtest

import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class MainActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		val zipPath = Environment.getExternalStorageDirectory().absolutePath + "/"
		mUnzip(zipPath, "test.zip")
	}
	private fun mUnzip(path: String, zipname: String): Boolean {
		val inputStream: InputStream
		val zis: ZipInputStream
		try {
			var filename: String
			inputStream = FileInputStream(path + zipname)
			zis = ZipInputStream(BufferedInputStream(inputStream))
			var mZipEntry: ZipEntry
			val buffer = ByteArray(1024)
			var count: Int
			while (zis.nextEntry.also { mZipEntry = it } != null) { // zapis do souboru
				filename = mZipEntry.getName()
				// Need to create directories if not exists, or
// it will generate an Exception...
				if (mZipEntry.isDirectory()) {
					val fmd = File(path + filename)
					fmd.mkdirs()
					continue
				}
				val fout = FileOutputStream(path + filename)
				// cteni zipu a zapis
				while (zis.read(buffer).also { count = it } != -1) {
					fout.write(buffer, 0, count)
				}
				fout.close()
				zis.closeEntry()
				Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show()
			}
			zis.close()
		} catch (e: IOException) {
			e.printStackTrace()
			return false
		}
		return true
	}
}
