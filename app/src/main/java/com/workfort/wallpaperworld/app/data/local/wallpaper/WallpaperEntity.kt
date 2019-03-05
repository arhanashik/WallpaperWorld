package com.workfort.wallpaperworld.app.data.local.wallpaper

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.workfort.wallpaperworld.app.data.local.appconst.Const

/*
*  ****************************************************************************
*  * Created by : Arhan Ashik on 12/27/2018 at 7:06 PM.
*  * Email : ashik.pstu.cse@gmail.com
*  * 
*  * Last edited by : Arhan Ashik on 12/27/2018.
*  * 
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>  
*  ****************************************************************************
*/

data class WallpaperEntity (var id: Int = -1,
                            var title: String? = "",
                            var url: String? = "",
                            var width: Int = 0,
                            var height: Int = 0,
                            var tag: String? = "",
                            var price: Int = 0,
                            @SerializedName("total_wow") var totalWow: Int = 0,
                            @SerializedName("total_download") var totalDownload: Int = 0,
                            var status: Int = Const.WallpaperStatus.UNDEFINED,
                            @SerializedName("uploader_id") var uploaderId: Int = -1,
                            @SerializedName("uploader_name") var uploaderName: String? = ""): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(url)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeString(tag)
        parcel.writeInt(price)
        parcel.writeInt(totalWow)
        parcel.writeInt(totalDownload)
        parcel.writeInt(status)
        parcel.writeInt(uploaderId)
        parcel.writeString(uploaderName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WallpaperEntity> {
        override fun createFromParcel(parcel: Parcel): WallpaperEntity {
            return WallpaperEntity(parcel)
        }

        override fun newArray(size: Int): Array<WallpaperEntity?> {
            return arrayOfNulls(size)
        }
    }
}