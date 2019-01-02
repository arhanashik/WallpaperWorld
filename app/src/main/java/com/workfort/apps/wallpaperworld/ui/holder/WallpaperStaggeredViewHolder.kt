package com.workfort.apps.wallpaperworld.ui.holder

import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.workfort.apps.wallpaperworld.R
import com.workfort.apps.wallpaperworld.data.local.WallpaperEntity
import com.workfort.apps.wallpaperworld.databinding.ItemStaggeredWallpaperBinding
import com.workfort.apps.wallpaperworld.ui.listener.WallpaperClickEvent
import com.workfort.apps.wallpaperworld.util.helper.load

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

        var imgRes = R.drawable.img_splash2
        if(adapterPosition%2 == 0) imgRes = R.drawable.img_splash

        binding.imgWallpaper.load(imgRes)
    }
}