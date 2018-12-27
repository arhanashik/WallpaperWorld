package com.workfort.apps.wallpaperworld.ui.holder

import androidx.recyclerview.widget.RecyclerView
import com.workfort.apps.wallpaperworld.data.local.WallpaperEntity
import com.workfort.apps.wallpaperworld.databinding.ItemStaggeredWallpaperBinding
import java.util.*

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
    private val mRandom = Random()

    fun bindView(wallpaperEntity: WallpaperEntity) {
        binding.tvTitle.text = wallpaperEntity.title
        val uploadedBy = "by " + wallpaperEntity.uploaderName
        binding.tvUploadedBy.text = uploadedBy
        binding.tvWowCount.text = wallpaperEntity.totalWow.toString()

        binding.root.layoutParams.height = getRandomIntInRange(250, 150)
    }

    private fun getRandomIntInRange(max: Int, min: Int): Int {
        return mRandom.nextInt(max - min + min) + min
    }
}