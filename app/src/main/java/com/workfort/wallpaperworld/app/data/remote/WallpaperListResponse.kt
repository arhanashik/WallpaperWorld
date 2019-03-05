package com.workfort.wallpaperworld.app.data.remote

import com.workfort.wallpaperworld.app.data.local.wallpaper.WallpaperEntity

data class WallpaperListResponse (val error: Boolean,
                                  val message: String,
                                  val wallpapers: List<WallpaperEntity>)