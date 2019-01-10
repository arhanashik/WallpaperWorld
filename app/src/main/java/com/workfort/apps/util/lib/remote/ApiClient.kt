package com.workfort.apps.util.lib.remote

import com.workfort.apps.wallpaperworld.data.remote.WallpaperResponse
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiClient {
    @GET("Api.php?call=wallpapers")
    fun getWallpapers(@Query("type") type: String) : Flowable<WallpaperResponse>
}