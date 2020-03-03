package com.xrexter.zipinputstreamtest

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream


class MainActivity : AppCompatActivity() {
	private val REQUEST_OPEN_DOCUMENT = 101
	private val READ_REQUEST_CODE = 42

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		val zipPath = Environment.getExternalStorageDirectory().absolutePath + "/"

		//mUnzip(zipPath, "test.zip")
		btn_open.setOnClickListener {
			selectFile()
		}
	}

	private fun selectFile() {
		val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
			addCategory(Intent.CATEGORY_OPENABLE)
			type = "*/*"
		}
		startActivityForResult(intent, READ_REQUEST_CODE)
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, resultData: Intent?) {
		super.onActivityResult(requestCode, resultCode, resultData)

		if (resultCode == REQUEST_OPEN_DOCUMENT && resultCode == Activity.RESULT_OK) {
			resultData?.data?.also { uri ->
				val takeFlasgs: Int = intent.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
				contentResolver.takePersistableUriPermission(uri, takeFlasgs)
				Log.i("URI", uri.toString())
				getFileInfo(uri)
			}
		}
		if(requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			resultData?.data?.also { uri ->
				Log.i("URI", uri.toString())
				getFileInfo(uri)
			}
		}
	}

	private fun getFileInfo(uri: Uri){
		//val docFile = DocumentFile.fromUri(this, uri)!!
		val mFile = DocumentFile.fromSingleUri(this, uri)!!
		if(mFile.canRead()){
			tv_label.text = mFile.name.toString()
		}
		/*if (docFile.canRead() && docFile.isFile) {
			tv_label.text = docFile.name.toString()
		}*/
	}

	private fun mUnzip(zipname: String): Boolean {
		val inputStream: InputStream
		val zis: ZipInputStream
		try {
			var filename: String
			inputStream = FileInputStream(zipname)
			zis = ZipInputStream(BufferedInputStream(inputStream))
			var mZipEntry: ZipEntry
			val buffer = ByteArray(1024)
			var count: Int
			while (zis.nextEntry.also { mZipEntry = it } != null) { // zapis do souboru
				filename = mZipEntry.getName()
				if (mZipEntry.isDirectory()) {
					val fmd = File(filename)
					fmd.mkdirs()
					continue
				}
				val fout = FileOutputStream(filename)
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
