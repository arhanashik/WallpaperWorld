package com.workfort.apps.wallpaperworld.ui.uploadwallpaper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.workfort.apps.wallpaperworld.R
import com.workfort.apps.wallpaperworld.data.local.appconst.Const
import com.workfort.apps.wallpaperworld.data.local.user.UserEntity
import com.workfort.apps.wallpaperworld.databinding.PromptUploadWallpaperBinding

class WallpaperUploadDialog: DialogFragment() {

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

        val binding = DataBindingUtil.inflate<PromptUploadWallpaperBinding>(
            inflater, R.layout.prompt_upload_wallpaper, container, false
        )

        binding.toolbar.setNavigationIcon(R.drawable.ic_close)
        binding.toolbar.setNavigationOnClickListener { if(dialog != null) dialog.dismiss() }
        binding.toolbar.title = getString(R.string.label_new_wallpaper)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        if(dialog != null) {
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
    }
}