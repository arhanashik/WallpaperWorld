package com.workfort.apps.util.helper

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable

class ImageUtil {

    fun getBackgroundGradient(color1: Int, color2: Int): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(color1, color2, color1)
        )
    }

    fun adjustOpacity(bitmap: Bitmap, opacity: Int): Bitmap {
        val mutableBitmap = if (bitmap.isMutable)
            bitmap
        else
            bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val colour = opacity and 0xFF shl 24
        canvas.drawColor(colour, PorterDuff.Mode.DST_IN)
        return mutableBitmap
    }
}