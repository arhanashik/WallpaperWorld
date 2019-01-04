package com.workfort.apps.wallpaperworld.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.workfort.apps.wallpaperworld.R
import com.workfort.apps.wallpaperworld.data.local.wallpaper.WallpaperEntity
import com.workfort.apps.wallpaperworld.databinding.ItemWallpaperBinding
import com.workfort.apps.wallpaperworld.ui.holder.WallpaperViewHolder
import com.workfort.apps.wallpaperworld.ui.listener.WallpaperClickEvent

/*
*  ****************************************************************************
*  * Created by : Arhan Ashik on 12/27/2018 at 7:12 PM.
*  * Email : ashik.pstu.cse@gmail.com
*  * 
*  * Last edited by : Arhan Ashik on 12/27/2018.
*  * 
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>  
*  ****************************************************************************
*/

class WallpaperAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listOfWallpapers = listOf<WallpaperEntity>()
    private var listener: WallpaperClickEvent? = null

    fun setWallpaperList(listOfWallpapers: List<WallpaperEntity>) {
        this.listOfWallpapers = listOfWallpapers
        notifyDataSetChanged()
    }

    fun getWallpaper(position: Int): WallpaperEntity {
        return listOfWallpapers[position]
    }

    fun getWallpaperList(): List<WallpaperEntity> {
        return listOfWallpapers
    }

    fun setListener(listener: WallpaperClickEvent) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemWallpaperBinding>(LayoutInflater.from(parent.context),
            R.layout.item_wallpaper, parent, false)

        return WallpaperViewHolder(binding)
    }

    override fun getItemCount(): Int = listOfWallpapers.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val wallpaperViewHolder = viewHolder as WallpaperViewHolder

        wallpaperViewHolder.bindView(listOfWallpapers[position], listener)
    }
}