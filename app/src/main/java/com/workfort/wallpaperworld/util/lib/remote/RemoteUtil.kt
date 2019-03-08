package com.workfort.wallpaperworld.util.lib.remote

import io.reactivex.FlowableEmitter
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import java.io.File


class RemoteUtil {

    fun createMultipartBody(filePath: String, fileType: String): MultipartBody.Part {
        return MultipartBody.Part.createFormData("file", filePath, createRequestBody(fileType, File(filePath)))
    }

    fun createRequestBody(mediaType: String, data: String): RequestBody {
        return RequestBody.create(MediaType.parse(mediaType), data)
    }

    private fun createRequestBody(mediaType: String, file: File): RequestBody {
        return RequestBody.create(MediaType.parse(mediaType), file)
    }

    fun createCountingMultipartBody(filePath: String, fileType: String, emitter: FlowableEmitter<Double>): MultipartBody.Part {
        return MultipartBody.Part.createFormData("file", filePath,
            createCountingRequestBody(fileType, filePath, emitter))
    }

    private fun createCountingRequestBody(mediaType: String, filePath: String, emitter: FlowableEmitter<Double>): RequestBody {
        return CountingRequestBody(createRequestBody(mediaType, File(filePath)), object: CountingRequestBody.Listener {
            override fun onRequestProgress(bytesWritten: Long, contentLength: Long) {
                val progress = 1.0 * bytesWritten / contentLength
                Timber.e("progress: %s", progress)
                emitter.onNext(progress)
            }
        })
    }
}