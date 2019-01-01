package com.workfort.apps.wallpaperworld.data.local

import android.os.Parcel
import android.os.Parcelable

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

data class WallpaperEntity (val id: Int,
                            val title: String?,
                            val url: String?,
                            val width: Int,
                            val height: Int,
                            val totalWow: Int,
                            val uploaderId: String?,
                            val uploaderName: String?): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(url)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeInt(totalWow)
        parcel.writeString(uploaderId)
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