package com.workfort.wallpaperworld.app.ui.listener

import com.workfort.wallpaperworld.app.data.local.wallpaper.WallpaperEntity

/*
*  ****************************************************************************
*  * Created by : Arhan Ashik on 1/2/2019 at 4:25 PM.
*  * Email : ashik.pstu.cse@gmail.com
*  * 
*  * Last edited by : Arhan Ashik on 1/2/2019.
*  * 
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>  
*  ****************************************************************************
*/

interface WallpaperClickEvent {
    fun onClickWallpaper(wallpaper: WallpaperEntity, position: Int)
    fun onClickWow(wallpaper: WallpaperEntity, position: Int)
}