package com.workfort.wallpaperworld.util.lib.remote

import com.workfort.wallpaperworld.app.data.remote.Response
import com.workfort.wallpaperworld.app.data.remote.SignUpResponse
import com.workfort.wallpaperworld.app.data.remote.WallpaperResponse
import io.reactivex.Flowable
import okhttp3.MultipartBody
import retrofit2.http.*

interface ApiClient {
    @FormUrlEncoded
    @POST("Api.php?call=signup")
    fun signUp(@Field("name") name: String,
               @Field("username") username: String,
               @Field("password") password: String,
               @Field("email") email: String,
               @Field("avatar") avatar: String,
               @Field("auth_type") authType: String) : Flowable<SignUpResponse>

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

    @FormUrlEncoded
    @POST("Api.php?call=favorite")
    fun addToFavorite(@Field("id") id: Int,
               @Field("wallpaper_id") wallpaperId: Int): Flowable<Response>

    @Multipart
    @POST("Api.php?call=upload")
    fun createWallpaper(@Part("title") title: String,
                        @Part("tag") tag: String,
                        @Part("price") price: Int,
                        @Part("uploader_id") uploaderId: String,
                        @Part wallpaper: MultipartBody.Part): Flowable<Response>
}