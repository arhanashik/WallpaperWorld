package com.workfort.wallpaperworld.app.ui.uploadwallpaper

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.workfort.wallpaperworld.R
import com.workfort.wallpaperworld.app.data.local.appconst.Const
import com.workfort.wallpaperworld.app.data.local.user.UserEntity
import com.workfort.wallpaperworld.app.data.local.wallpaper.WallpaperEntity
import com.workfort.wallpaperworld.databinding.PromptUploadWallpaperBinding
import com.workfort.wallpaperworld.util.helper.*
import com.workfort.wallpaperworld.util.lib.remote.ApiService
import com.workfort.wallpaperworld.util.lib.remote.RemoteUtil
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class WallpaperUploadDialog: DialogFragment() {

    private lateinit var mBinding: PromptUploadWallpaperBinding

    private var disposable: CompositeDisposable = CompositeDisposable()
    private val apiService by lazy { ApiService.create() }

    private val fileUtil = FileUtil()
    private val remoteUtil = RemoteUtil()

    private var user: UserEntity? = null
    private var wallpaper: WallpaperEntity? = WallpaperEntity()
    private var wallpaperInfo: ImageInfo? = null
    private var listener: WallpaperUploadEvent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)

        if(arguments != null) {
            user = arguments?.getParcelable(Const.Key.USER)
            wallpaper?.uploaderId = user?.id!!
            wallpaper?.uploaderName = user?.name
        }
    }

    fun setListener(listener: WallpaperUploadEvent) {
        this.listener = listener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        mBinding = DataBindingUtil.inflate(
            inflater, R.layout.prompt_upload_wallpaper, container, false
        )

        mBinding.toolbar.setNavigationIcon(R.drawable.ic_close)
        mBinding.toolbar.setNavigationOnClickListener { if(dialog != null) dialog.dismiss() }
        mBinding.toolbar.title = getString(R.string.label_new_wallpaper)

        mBinding.toolbar.inflateMenu(R.menu.menu_upload_wallpaper)
        mBinding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.action_upload -> {
                    uploadWallpaper()
                    true
                }
                else -> false
            }
        }

        mBinding.btnSelectImage.setOnClickListener { pickImage() }
        mBinding.imgPreview.setOnClickListener { pickImage() }

        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        if(dialog != null) {
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            ImagePicker.REQUEST_CODE_PICK_IMAGE -> {
                if (resultCode == Activity.RESULT_OK) {
                    wallpaperInfo = ImagePicker.getPickedImageInfo(context, resultCode, data)

                    if (wallpaperInfo?.imageUri != null) {
                        mBinding.btnSelectImage.visibility = View.INVISIBLE
                        mBinding.imgPreview.visibility = View.VISIBLE
                        mBinding.imgPreview.load(wallpaperInfo?.imageUri)
                    }
                } else {
                    if (resultCode != Activity.RESULT_CANCELED) {
                        Toaster(context!!).showToast("Could not pick image")
                    }
                }
            }
            else -> {

            }
        }
    }

    private fun pickImage() {
        if (PermissionUtil.on().request(this,
                PermissionUtil.REQUEST_CODE_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            ImagePicker.pickImage(this)
        }
    }

    private fun uploadWallpaper() {
        if(mBinding.spTag.selectedItemPosition == 0) {
            Toaster(context!!).showToast(R.string.category_required_exception); return
        }
        wallpaper?.tag = mBinding.spTag.selectedItem.toString()

        if(!validate(mBinding.tilTitle, mBinding.etTitle, R.string.title_required_exception)) return
        wallpaper?.title = mBinding.etTitle.text.toString()

        if(!validate(mBinding.tilPrice, mBinding.etPrice, R.string.price_required_exception)) return
        wallpaper?.price = mBinding.etPrice.text.toString().toInt()

        if(wallpaperInfo?.imageUri == null) {
            Toaster(context!!).showToast(R.string.wallpaper_required_exception); return
        }

        val wallpaperPath = fileUtil.getPath(wallpaperInfo?.imageUri!!)
        val wallpaperMediaTypeStr = fileUtil.getFileType(wallpaperInfo?.imageUri!!)

        if(TextUtils.isEmpty(wallpaperPath) || TextUtils.isEmpty(wallpaperMediaTypeStr)) {
            Toaster(context!!).showToast(R.string.invalid_wallpaper_exception); return
        }

        disposable.add(getUploadFlowable(wallpaperPath!!, wallpaperMediaTypeStr!!)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Toaster(context!!).showToast("Progress: $it")
                    if(it == 1.0) Toaster(context!!).showToast(R.string.wallpaper_creation_success_message)
                }, {
                    Timber.e(it)
                    Toaster(context!!).showToast(it.message.toString())
                }
            )
        )
    }

    private fun getUploadFlowable(path: String, mediaType: String): Flowable<Double> {
        return Flowable.create<Double>({ emitter ->
            try {
                val response = apiService.createWallpaper(
                    remoteUtil.createRequestBody("text/plain", wallpaper?.title!!),
                    remoteUtil.createRequestBody("text/plain", wallpaper?.tag!!),
                    remoteUtil.createRequestBody("text/plain", wallpaper?.price.toString()),
                    remoteUtil.createRequestBody("text/plain", wallpaper?.uploaderId.toString()),
                    remoteUtil.createCountingMultipartBody(path, mediaType, emitter)).blockingGet()
                Timber.e("error: %s, message: %s", response.error, response.message)

                if(response.error) emitter.onError(Throwable(response.message))
                else {
                    wallpaper = response.wallpaper
                    onUiThread { listener?.onNewUpload(wallpaper!!); this.dismiss() }
                    emitter.onComplete()
                }
            } catch (e: Exception) {
                emitter.tryOnError(e)
            }
        }, BackpressureStrategy.LATEST)
    }

    private fun validate(layout: TextInputLayout, editText: TextInputEditText, error: Int): Boolean {
        if(editText.text == null || TextUtils.isEmpty(editText.text.toString())) {
            layout.error = getString(error)
            return false
        }

        layout.error = null
        return true
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }

    interface WallpaperUploadEvent{
        fun onNewUpload(wallpaper: WallpaperEntity)
    }
}