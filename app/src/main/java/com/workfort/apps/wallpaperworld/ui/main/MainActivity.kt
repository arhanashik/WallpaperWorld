package com.workfort.apps.wallpaperworld.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.workfort.apps.wallpaperworld.R
import com.workfort.apps.wallpaperworld.databinding.ActivityMainBinding
import com.workfort.apps.wallpaperworld.ui.main.adapter.PagerAdapter
import com.workfort.apps.wallpaperworld.ui.main.favourite.FavouriteFragment
import com.workfort.apps.wallpaperworld.ui.main.wallpapers.WallpapersFragment

class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(mBinding.layoutToolbar.toolbar)

        if (savedInstanceState == null) {
            val fragmentAdapter = PagerAdapter(supportFragmentManager)
            fragmentAdapter.addItem(getString(R.string.title_fragment_wallpapers), WallpapersFragment.newInstance())
            fragmentAdapter.addItem(getString(R.string.title_fragment_favourite), FavouriteFragment.newInstance())
            mBinding.viewPager.adapter = fragmentAdapter

            mBinding.tabs.setupWithViewPager(mBinding.viewPager)
        }
    }
}
