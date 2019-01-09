package com.workfort.apps.util.helper

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Environment
import androidx.core.content.ContextCompat
import com.workfort.apps.wallpaperworld.R
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import timber.log.Timber
import java.io.File
import java.util.*

class ImageUtil {

    fun cropImage(activity: Activity, uri: Uri) {
        try {
            val id = Random().nextInt(10000)
            val desImg = FileUtil().createEmptyFile("WW_$id.JPEG")
            if (desImg.exists()) {
                if (desImg.delete()) {
                    Timber.e("Old cache cleared")
                }
            }

            val displayParams = AndroidUtil().getDisplayParams(activity)

            val options = UCrop.Options()
            options.setCompressionFormat(Bitmap.CompressFormat.JPEG)
            options.setCompressionQuality(100)
            options.setFreeStyleCropEnabled(false)
            val ratio = AspectRatio("Scrolling", 1f, 1f)
            val ratio2 = AspectRatio("Fixed", displayParams.width.toFloat(), displayParams.height.toFloat())
            options.setAspectRatioOptions(0, ratio, ratio2)

            // Color palette
            options.setToolbarColor(ContextCompat.getColor(activity, R.color.colorPrimary))
            options.setToolbarWidgetColor(ContextCompat.getColor(activity, android.R.color.white))
            options.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorPrimaryDark))
            options.setActiveWidgetColor(ContextCompat.getColor(activity, R.color.colorPrimary))

            UCrop.of(uri, Uri.fromFile(desImg))
                .withOptions(options)
                .start(activity)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

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