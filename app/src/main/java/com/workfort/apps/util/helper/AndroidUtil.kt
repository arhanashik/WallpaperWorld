package com.workfort.apps.util.helper

import android.content.Context
import androidx.appcompat.widget.PopupMenu


class AndroidUtil {
    data class DisplayParams(val width: Int, val height: Int)

    fun getDisplayParams(context: Context): DisplayParams {
        val config = context.resources.configuration

        return DisplayParams(config.screenWidthDp, config.screenHeightDp)
    }

    fun setForceShowIcon(popupMenu: PopupMenu) {
        try {
            val fields = popupMenu::class.java.declaredFields
            for (field in fields) {
                if ("mPopup" == field.name) {
                    field.isAccessible = true
                    val menuPopupHelper = field.get(popupMenu)
                    val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                    val setForceIcons =
                        classPopupHelper.getMethod("setForceShowIcon", Boolean::class.javaPrimitiveType!!)
                    setForceIcons.invoke(menuPopupHelper, true)
                    break
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

    }
}