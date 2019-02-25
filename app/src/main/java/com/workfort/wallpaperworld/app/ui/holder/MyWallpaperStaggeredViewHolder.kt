package com.workfort.wallpaperworld.app.ui.holder

import android.view.View
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import com.workfort.wallpaperworld.util.helper.load
import com.workfort.wallpaperworld.app.data.local.appconst.Const
import com.workfort.wallpaperworld.app.data.local.wallpaper.WallpaperEntity
import com.workfort.wallpaperworld.databinding.ItemStaggeredWallpaperBinding
import com.workfort.wallpaperworld.app.ui.listener.MyWallpaperClickEvent


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

class MyWallpaperStaggeredViewHolder(val binding: ItemStaggeredWallpaperBinding): RecyclerView.ViewHolder(binding.root) {

    fun bindView(wallpaper: WallpaperEntity, listener: MyWallpaperClickEvent?) {
        binding.tvTitle.text = wallpaper.title
        var status = "Pending"

        when (wallpaper.status) {
            Const.WallpaperStatus.REJECTED -> status = "Rejected"
            Const.WallpaperStatus.PENDING -> status = "Pending"
            Const.WallpaperStatus.PUBLISHED -> status = "Published"
            Const.WallpaperStatus.HIDDEN -> status = "Hidden"
        }
        binding.tvUploadedBy.text = status
        binding.tvWowCount.text = wallpaper.totalWow.toString()

        val set = ConstraintSet()
        val ratio = String.format("%d:%d", wallpaper.width, wallpaper.height)
        set.clone(binding.container)
        set.setDimensionRatio(binding.imgWallpaper.id, ratio)
        set.applyTo(binding.container)

        binding.imgWallpaper.setOnClickListener {
            listener?.onClickWallpaper(wallpaper, adapterPosition)
        }

        binding.tvWowCount.setOnClickListener {
            listener?.onClickWow(wallpaper, adapterPosition)
        }

        if(wallpaper.status != Const.WallpaperStatus.REJECTED && wallpaper.status != Const.WallpaperStatus.PENDING) {
            binding.imgOption.visibility = View.VISIBLE
            binding.imgOption.setOnClickListener {
                listener?.onClickMore(binding.imgOption, wallpaper, adapterPosition)
            }
        }

        binding.imgWallpaper.load(wallpaper.url)
    }
}