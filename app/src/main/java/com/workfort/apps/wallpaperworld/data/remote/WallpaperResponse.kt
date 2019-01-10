package com.workfort.apps.wallpaperworld.data.remote

import com.workfort.apps.wallpaperworld.data.local.wallpaper.WallpaperEntity

data class WallpaperResponse (val error: Boolean,
                              val message: String,
                              val wallpapers: List<WallpaperEntity>)