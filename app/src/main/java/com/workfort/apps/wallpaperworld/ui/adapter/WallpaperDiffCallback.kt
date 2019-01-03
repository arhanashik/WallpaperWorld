package com.workfort.apps.wallpaperworld.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.workfort.apps.wallpaperworld.data.local.wallpaper.WallpaperEntity

class WallpaperDiffCallback(
        private val old: List<WallpaperEntity>,
        private val new: List<WallpaperEntity>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition].id == new[newPosition].id
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == new[newPosition]
    }
}
