package com.workfort.apps.wallpaperworld.ui.holder

import android.graphics.BitmapFactory
import androidx.constraintlayout.widget.ConstraintSet
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.workfort.apps.util.helper.load
import com.workfort.apps.wallpaperworld.R
import com.workfort.apps.wallpaperworld.data.local.wallpaper.WallpaperEntity
import com.workfort.apps.wallpaperworld.databinding.ItemStaggeredWallpaperBinding
import com.workfort.apps.wallpaperworld.ui.listener.WallpaperClickEvent


/*
*  ****************************************************************************
*  * Created by : Arhan Ashik on 12/27/2018 at 6:39 PM.
*  * Email : ashik.pstu.cse@gmail.com
*  * 
*  * Last edited by : Arhan Ashik on 12/27/2018.
*  * 
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>  
*  ****************************************************************************
*/

class WallpaperStaggeredViewHolder(val binding: ItemStaggeredWallpaperBinding): RecyclerView.ViewHolder(binding.root) {

    fun bindView(wallpaper: WallpaperEntity, listener: WallpaperClickEvent?) {
        binding.tvTitle.text = wallpaper.title
        val uploadedBy = "by " + wallpaper.uploaderName
        binding.tvUploadedBy.text = uploadedBy
        binding.tvWowCount.text = wallpaper.totalWow.toString()

        val set = ConstraintSet()
        val ratio = String.format("%d:%d", wallpaper.width, wallpaper.height)
        set.clone(binding.container)
        set.setDimensionRatio(binding.imgWallpaper.id, ratio)
        set.applyTo(binding.container)

        binding.imgWallpaper.setOnClickListener {
            listener?.onClickWallpaper(wallpaper, adapterPosition)
        }

        binding.imgWallpaper.load(wallpaper.url)
    }
}