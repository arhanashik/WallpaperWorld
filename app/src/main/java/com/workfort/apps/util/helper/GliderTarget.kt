package com.workfort.apps.util.helper

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

abstract class GliderTarget:  Target<Bitmap> {
    var lastRequest: Request? = null

    override fun onLoadFailed(errorDrawable: Drawable?) {

    }

    override fun getSize(cb: SizeReadyCallback) {

    }

    override fun getRequest(): Request? {
        return lastRequest
    }

    override fun onStop() {

    }

    override fun setRequest(request: Request?) {
        lastRequest = request
    }

    override fun removeCallback(cb: SizeReadyCallback) {

    }

    override fun onLoadCleared(placeholder: Drawable?) {

    }

    override fun onStart() {

    }

    override fun onDestroy() {

    }

    override fun onLoadStarted(placeholder: Drawable?) {

    }

    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {

    }
}