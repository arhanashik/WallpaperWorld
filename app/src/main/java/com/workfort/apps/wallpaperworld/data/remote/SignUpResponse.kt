package com.workfort.apps.wallpaperworld.data.remote

import com.workfort.apps.wallpaperworld.data.local.user.UserEntity

data class SignUpResponse(val error: Boolean,
                          val message: String,
                          val user: UserEntity?)