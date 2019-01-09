package com.workfort.apps.util.helper;

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.os.Build

class WallpaperUtil(val context: Context) {
    var wallpaperManager: WallpaperManager? = null

    init {
        wallpaperManager = WallpaperManager.getInstance(context)
    }

    fun setWallpaper(wallpaperRes: Int) {
        wallpaperManager!!.setResource(wallpaperRes)
    }

    fun setWallpaper(bitmap: Bitmap) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            wallpaperManager!!.setBitmap(
                bitmap,
                null,
                true,
                WallpaperManager.FLAG_SYSTEM or WallpaperManager.FLAG_LOCK
            )
        } else {
            wallpaperManager!!.setBitmap(bitmap)
        }
    }
}
