package com.workfort.apps.util.helper

import android.content.Context


class AndroidUtil {
    data class DisplayParams(val width: Int, val height: Int)

    fun getDisplayParams(context: Context): DisplayParams {
        val config = context.resources.configuration

        return DisplayParams(config.screenWidthDp, config.screenHeightDp)
    }
}