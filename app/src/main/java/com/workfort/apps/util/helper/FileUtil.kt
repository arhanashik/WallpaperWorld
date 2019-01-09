package com.workfort.apps.util.helper

import android.graphics.Bitmap
import android.net.Uri
import com.workfort.apps.WallpaperWorldApp
import java.io.File
import java.io.FileOutputStream
import java.util.*

class FileUtil {
    fun saveBitmap(bitmap: Bitmap): Uri {
        val id = Random().nextInt(10000)
        val file = File(WallpaperWorldApp.getApplicationContext().cacheDir, "WW_$id.JPEG")

        val fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.flush()
        fos.close()

        return Uri.fromFile(file)
    }

    fun createEmptyFile(fileName: String): File {
        return File(WallpaperWorldApp.getApplicationContext().cacheDir, fileName)
    }
}
