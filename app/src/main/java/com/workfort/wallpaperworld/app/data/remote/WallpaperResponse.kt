package com.workfort.wallpaperworld.app.data.remote

import com.workfort.wallpaperworld.app.data.local.wallpaper.WallpaperEntity

data class WallpaperResponse (val error: Boolean,
                              val message: String,
                              val wallpaper: WallpaperEntity?)