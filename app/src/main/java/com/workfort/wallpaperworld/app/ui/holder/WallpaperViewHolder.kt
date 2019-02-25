package com.workfort.wallpaperworld.app.ui.holder

import androidx.recyclerview.widget.RecyclerView
import com.workfort.wallpaperworld.util.helper.load
import com.workfort.wallpaperworld.app.data.local.wallpaper.WallpaperEntity
import com.workfort.wallpaperworld.databinding.ItemWallpaperBinding
import com.workfort.wallpaperworld.app.ui.listener.WallpaperClickEvent

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

class WallpaperViewHolder(val binding: ItemWallpaperBinding): RecyclerView.ViewHolder(binding.root) {

    fun bindView(wallpaper: WallpaperEntity, listener: WallpaperClickEvent?) {
        binding.tvTitle.text = wallpaper.title
        val uploadedBy = "by " + wallpaper.uploaderName
        binding.tvUploadedBy.text = uploadedBy
        binding.tvWowCount.text = wallpaper.totalWow.toString()

        binding.imgWallpaper.load(wallpaper.url)

        binding.imgWallpaper.setOnClickListener {
            listener?.onClickWallpaper(wallpaper, adapterPosition)
        }
    }
}