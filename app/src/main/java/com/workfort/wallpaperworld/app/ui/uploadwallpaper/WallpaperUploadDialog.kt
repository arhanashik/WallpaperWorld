package com.workfort.wallpaperworld.app.ui.uploadwallpaper

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.workfort.wallpaperworld.R
import com.workfort.wallpaperworld.app.data.local.appconst.Const
import com.workfort.wallpaperworld.app.data.local.user.UserEntity
import com.workfort.wallpaperworld.databinding.PromptUploadWallpaperBinding
import com.workfort.wallpaperworld.util.helper.ImagePicker
import com.workfort.wallpaperworld.util.helper.PermissionUtil
import com.workfort.wallpaperworld.util.helper.Toaster
import com.workfort.wallpaperworld.util.helper.load

class WallpaperUploadDialog: DialogFragment() {

    private lateinit var mBinding: PromptUploadWallpaperBinding

    private var user: UserEntity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle)

        if(arguments != null) {
            user = arguments?.getParcelable(Const.Key.USER)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        mBinding = DataBindingUtil.inflate<PromptUploadWallpaperBinding>(
            inflater, R.layout.prompt_upload_wallpaper, container, false
        )

        mBinding.toolbar.setNavigationIcon(R.drawable.ic_close)
        mBinding.toolbar.setNavigationOnClickListener { if(dialog != null) dialog.dismiss() }
        mBinding.toolbar.title = getString(R.string.label_new_wallpaper)

        mBinding.toolbar.inflateMenu(R.menu.menu_upload_wallpaper)
        mBinding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.action_upload -> {
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
                    val pickedImageInfo = ImagePicker.getPickedImageInfo(context, resultCode, data)

                    if (pickedImageInfo.imageUri != null) {
                        mBinding.btnSelectImage.visibility = View.INVISIBLE
                        mBinding.imgPreview.visibility = View.VISIBLE
                        mBinding.imgPreview.load(pickedImageInfo.imageUri)
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
}