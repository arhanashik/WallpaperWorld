package com.workfort.wallpaperworld.app.data.remote

import com.workfort.wallpaperworld.app.data.local.user.UserEntity

data class SignUpResponse(val error: Boolean,
                          val message: String,
                          val user: UserEntity?)