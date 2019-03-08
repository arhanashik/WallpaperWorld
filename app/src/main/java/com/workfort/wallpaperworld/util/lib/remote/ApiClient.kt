package com.workfort.wallpaperworld.util.lib.remote

import com.workfort.wallpaperworld.app.data.remote.Response
import com.workfort.wallpaperworld.app.data.remote.SignUpResponse
import com.workfort.wallpaperworld.app.data.remote.WallpaperListResponse
import com.workfort.wallpaperworld.app.data.remote.WallpaperResponse
import io.reactivex.Flowable
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
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
                      @Query("page") page: Int) : Flowable<WallpaperListResponse>

    @GET("Api.php?call=wallpapers")
    fun getFavorites(@Query("id") id: Int,
                     @Query("type") type: String,
                     @Query("page") page: Int): Flowable<WallpaperListResponse>

    @GET("Api.php?call=search")
    fun search(@Query("id") id: Int,
               @Query("query") query: String,
               @Query("page") page: Int): Flowable<WallpaperListResponse>

    @FormUrlEncoded
    @POST("Api.php?call=favorite")
    fun addToFavorite(@Field("id") id: Int,
               @Field("wallpaper_id") wallpaperId: Int): Flowable<Response>

    @Multipart
    @POST("Api.php?call=upload")
    fun createWallpaper(@Part("title") title: RequestBody,
                        @Part("tag") tag: RequestBody,
                        @Part("price") price: RequestBody,
                        @Part("uploader_id") uploaderId: RequestBody,
                        @Part wallpaper: MultipartBody.Part): Single<WallpaperResponse>
}