package com.workfort.apps.wallpaperworld.data.local.user

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class UserEntity(val id: Int,
                      val name: String?,
                      val username: String?,
                      val email: String?,
                      @SerializedName("upload_count")
                      val uploadCount: Int?,
                      val avatar: String?,
                      @SerializedName("auth_type")
                      val authType: String?): Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(name)
        parcel.writeString(username)
        parcel.writeString(email)
        parcel.writeValue(uploadCount)
        parcel.writeString(avatar)
        parcel.writeString(authType)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserEntity> {
        override fun createFromParcel(parcel: Parcel): UserEntity {
            return UserEntity(parcel)
        }

        override fun newArray(size: Int): Array<UserEntity?> {
            return arrayOfNulls(size)
        }
    }
}