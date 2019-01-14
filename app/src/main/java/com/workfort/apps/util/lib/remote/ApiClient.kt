package com.workfort.apps.util.lib.remote

import com.workfort.apps.wallpaperworld.data.remote.WallpaperResponse
import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiClient {
    @GET("Api.php?call=wallpapers")
    fun getWallpapers(@Query("type") type: String,
                      @Query("page") page: Int) : Flowable<WallpaperResponse>

    @GET("Api.php?call=wallpapers")
    fun getFavorites(@Query("id") id: Int,
                     @Query("type") type: String,
                     @Query("page") page: Int): Flowable<WallpaperResponse>

    @GET("Api.php?call=search")
    fun search(@Query("id") id: Int,
               @Query("query") query: String,
               @Query("page") page: Int): Flowable<WallpaperResponse>
}