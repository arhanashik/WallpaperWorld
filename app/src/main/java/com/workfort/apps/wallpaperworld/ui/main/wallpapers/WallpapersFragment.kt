package com.workfort.apps.wallpaperworld.ui.main.wallpapers

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.workfort.apps.wallpaperworld.R

class WallpapersFragment : Fragment() {

    companion object {
        fun newInstance() = WallpapersFragment()
    }

    private lateinit var viewModel: WallpapersViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_wallpapers, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(WallpapersViewModel::class.java)

    }

}
