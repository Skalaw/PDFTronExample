package com.example.pdftronexample

import android.net.Uri
import android.os.Environment
import com.pdftron.filters.SecondaryFileFilter
import com.pdftron.pdf.PDFViewCtrl
import com.pdftron.sdf.SDFDoc
import java.io.File


/**
 * @author Milosz Skalski
 */
val SAVE_MODES = arrayOf(SDFDoc.SaveMode.REMOVE_UNUSED)

/**
 * @return true for success
 */
fun PDFViewCtrl.save(fileName: String, saveInternal: Boolean): Boolean {
    if (saveInternal) {
        val filePath = context.filesDir.absolutePath + File.separator + fileName
        var shouldUnlock = false
        return try {
            docLock(true)
            shouldUnlock = true
            doc.save(filePath, SAVE_MODES, null)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            if (shouldUnlock) {
                docUnlock()
            }
        }
    } else {
        val filePath = getStorageDir().absolutePath + File.separator + fileName
        val newFile = File(filePath).apply {
            if (!exists()) {
                createNewFile()
            }
        }
        val uri = Uri.fromFile(newFile)
        var filter: SecondaryFileFilter? = null
        var shouldUnlock = false
        return try {
            docLock(true)
            shouldUnlock = true
            filter = SecondaryFileFilter(context, uri)
            doc.save(filter, SAVE_MODES)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        } finally {
            if (shouldUnlock) {
                docUnlock()
            }
            filter?.close()
        }
    }
}

private fun getStorageDir(): File {
    val storageDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
    if (!storageDir.exists()) {
        storageDir.mkdir()
    }
    return storageDir
}