package com.workfort.apps.wallpaperworld.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.workfort.apps.wallpaperworld.R
import com.workfort.apps.wallpaperworld.data.local.wallpaper.WallpaperEntity
import com.workfort.apps.wallpaperworld.databinding.ItemStaggeredWallpaperBinding
import com.workfort.apps.wallpaperworld.ui.holder.WallpaperStaggeredViewHolder
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

class WallpaperStaggeredAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listOfWallpapers = ArrayList<WallpaperEntity>()
    private var listener: WallpaperClickEvent? = null

    fun setWallpaperList(listOfWallpapers: List<WallpaperEntity>) {
        this.listOfWallpapers.addAll(listOfWallpapers)
        notifyDataSetChanged()
    }

    fun getWallpaperList(): ArrayList<WallpaperEntity> {
        return listOfWallpapers
    }

    fun setListener(listener: WallpaperClickEvent) {
        this.listener = listener
    }

    fun clear() {
        listOfWallpapers.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemStaggeredWallpaperBinding>(LayoutInflater.from(parent.context),
            R.layout.item_staggered_wallpaper, parent, false)

        return WallpaperStaggeredViewHolder(binding)
    }

    override fun getItemCount(): Int = listOfWallpapers.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val wallpaperViewHolder = viewHolder as WallpaperStaggeredViewHolder

        wallpaperViewHolder.bindView(listOfWallpapers[position], listener)
    }
}