package com.workfort.apps.wallpaperworld.ui.main.collection

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.workfort.apps.wallpaperworld.R
import com.workfort.apps.wallpaperworld.data.DummyData
import com.workfort.apps.wallpaperworld.data.local.WallpaperEntity
import com.workfort.apps.wallpaperworld.util.helper.StaggeredGridItemDecoration
import com.workfort.apps.wallpaperworld.ui.adapter.WallpaperStaggeredAdapter
import com.workfort.apps.wallpaperworld.ui.listener.WallpaperClickEvent
import kotlinx.android.synthetic.main.fragment_collection.*

class CollectionFragment : Fragment() {

    companion object {
        fun newInstance() = CollectionFragment()
    }

    private lateinit var viewModel: CollectionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(CollectionViewModel::class.java)

        initView()
    }

    private fun initView() {
        rv_wallpapers.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rv_wallpapers.addItemDecoration(StaggeredGridItemDecoration(10, 2))

        val wallpaperStaggeredAdapter = WallpaperStaggeredAdapter()
        wallpaperStaggeredAdapter.setListener(object: WallpaperClickEvent{
            override fun onClickWallpaper(wallpaper: WallpaperEntity, position: Int) {
                Toast.makeText(context, "${wallpaper.title} : $position", Toast.LENGTH_SHORT).show()
            }
        })
        rv_wallpapers.adapter = wallpaperStaggeredAdapter
        wallpaperStaggeredAdapter.setWallpaperList(DummyData.generateDummyData())

        swipe_refresh.setOnRefreshListener {
            Handler().postDelayed({
                swipe_refresh.isRefreshing = false
                wallpaperStaggeredAdapter.setWallpaperList(DummyData.generateDummyData())
            }, 1000)
        }
    }
}
