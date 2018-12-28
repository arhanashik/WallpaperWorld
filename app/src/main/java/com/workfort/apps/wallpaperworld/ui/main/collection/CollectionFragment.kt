package com.workfort.apps.wallpaperworld.ui.main.collection

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.workfort.apps.wallpaperworld.R
import com.workfort.apps.wallpaperworld.data.local.WallpaperEntity
import com.workfort.apps.wallpaperworld.ui.main.adapter.GridItemDecoration
import com.workfort.apps.wallpaperworld.ui.main.adapter.WallpaperStaggeredAdapter
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
        rv_wallpapers.addItemDecoration(GridItemDecoration(10, 2))

        val wallpaperStaggeredAdapter = WallpaperStaggeredAdapter()
        rv_wallpapers.adapter = wallpaperStaggeredAdapter
        wallpaperStaggeredAdapter.setWallpaperList(generateDummyData())

        swipe_refresh.setOnRefreshListener {
            Handler().postDelayed({
                swipe_refresh.isRefreshing = false
            }, 5000)
        }
    }

    private fun generateDummyData(): List<WallpaperEntity> {
        val listOfWallpapers = mutableListOf<WallpaperEntity>()

        var movieModel = WallpaperEntity(1, "Avengers", "", 234, "", "arhan.ashik")
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(2, "Avengers: Age of Ultron", "", 234, "", "arhan.ashik")
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(3, "Iron Man 3", "", 234, "", "arhan.ashik")
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(4, "Avengers: Infinity War", "", 234, "", "arhan.ashik")
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(5, "Thor: Ragnarok", "", 234, "", "arhan.ashik")
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(6, "lack Panther", "", 234, "", "arhan.ashik")
        listOfWallpapers.add(movieModel)

        movieModel = WallpaperEntity(7, "Logan", "", 234, "", "arhan.ashik")
        listOfWallpapers.add(movieModel)

        return listOfWallpapers
    }
}
