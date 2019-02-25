package com.workfort.wallpaperworld.app.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.workfort.wallpaperworld.app.data.local.wallpaper.WallpaperEntity

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
        return old[oldPosition] == new[newPosition]
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition].url.equals(new[newPosition].url)
    }
}
