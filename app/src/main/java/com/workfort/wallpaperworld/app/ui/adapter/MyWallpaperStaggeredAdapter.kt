package com.workfort.wallpaperworld.app.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.workfort.wallpaperworld.R
import com.workfort.wallpaperworld.app.data.local.wallpaper.WallpaperEntity
import com.workfort.wallpaperworld.databinding.ItemStaggeredWallpaperBinding
import com.workfort.wallpaperworld.app.ui.holder.MyWallpaperStaggeredViewHolder
import com.workfort.wallpaperworld.app.ui.listener.MyWallpaperClickEvent

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

class MyWallpaperStaggeredAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var listOfWallpapers = ArrayList<WallpaperEntity>()
    private var listener: MyWallpaperClickEvent? = null

    fun setWallpaperList(listOfWallpapers: List<WallpaperEntity>) {
        val callback = WallpaperDiffCallback(this.listOfWallpapers.toList(), listOfWallpapers)
        val result = DiffUtil.calculateDiff(callback)

        this.listOfWallpapers.clear()
        this.listOfWallpapers.addAll(listOfWallpapers)
        result.dispatchUpdatesTo(this)
    }

    fun getWallpaperList(): ArrayList<WallpaperEntity> {
        return listOfWallpapers
    }

    fun addWallpaper(wallpaper: WallpaperEntity) {
        listOfWallpapers.add(wallpaper)
        notifyItemChanged(itemCount)
    }

    fun setListener(listener: MyWallpaperClickEvent) {
        this.listener = listener
    }

    fun clear() {
        listOfWallpapers.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<ItemStaggeredWallpaperBinding>(LayoutInflater.from(parent.context),
            R.layout.item_staggered_wallpaper, parent, false)

        return MyWallpaperStaggeredViewHolder(binding)
    }

    override fun getItemCount(): Int = listOfWallpapers.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        val wallpaperViewHolder = viewHolder as MyWallpaperStaggeredViewHolder

        wallpaperViewHolder.bindView(listOfWallpapers[position], listener)
    }
}