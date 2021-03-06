package com.workfort.apps.wallpaperworld.ui.main.collection

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.workfort.apps.util.helper.StaggeredGridItemDecoration
import com.workfort.apps.util.helper.Toaster
import com.workfort.apps.wallpaperworld.R
import com.workfort.apps.wallpaperworld.data.local.appconst.Const
import com.workfort.apps.wallpaperworld.data.local.wallpaper.WallpaperEntity
import com.workfort.apps.wallpaperworld.ui.adapter.WallpaperStaggeredAdapter
import com.workfort.apps.wallpaperworld.ui.imageviewer.ImageViewerActivity
import com.workfort.apps.wallpaperworld.ui.listener.WallpaperClickEvent
import com.workfort.apps.wallpaperworld.ui.main.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_collection.*
import timber.log.Timber

class CollectionFragment : Fragment() {

    companion object {
        fun newInstance() = CollectionFragment()
    }

    private lateinit var adapter: WallpaperStaggeredAdapter
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
        loadWallpapers()
    }

    private fun initView() {
        rv_wallpapers.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rv_wallpapers.addItemDecoration(StaggeredGridItemDecoration(10, 2))

        adapter = WallpaperStaggeredAdapter()
        adapter.setListener(object: WallpaperClickEvent{
            override fun onClickWallpaper(wallpaper: WallpaperEntity, position: Int) {
                openImageViewer(wallpaper)
            }
        })
        rv_wallpapers.adapter = adapter

        swipe_refresh.setOnRefreshListener {
            loadWallpapers()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            Const.RequestCode.IMAGE_PREVIEW -> {
                if(resultCode == RESULT_OK) {

                }
            }
        }
    }

    private var page = 0
    private fun loadWallpapers() {
        swipe_refresh.isRefreshing = true
        if(page == 0) {
            img_no_data.visibility = View.VISIBLE
            rv_wallpapers.visibility = View.INVISIBLE
        }

        val parent = (activity as MainActivity)

        parent.disposable.add(parent.apiService.getWallpapers(
            Const.WallpaperType.COLLECTION, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    swipe_refresh.isRefreshing = false
                    if(it.error) {
                        Toaster(context!!).showToast(it.message)
                    }else {
                        if(it.wallpapers.isEmpty()){
                            if(adapter.itemCount == 0)
                                Toaster(context!!).showToast(it.message)
                            else
                                Toaster(context!!).showToast(getString(R.string.exception_no_more_item))
                        }else {
                            page++
                            img_no_data.visibility = View.INVISIBLE
                            rv_wallpapers.visibility = View.VISIBLE
                            adapter.setWallpaperList(it.wallpapers)
                        }
                    }
                }, {
                    Timber.e(it)
                    swipe_refresh.isRefreshing = false
                    Toaster(context!!).showToast(it.message.toString())
                }
            )
        )
    }

    fun openImageViewer(wallpaper: WallpaperEntity) {
        val intent = Intent(context, ImageViewerActivity::class.java)
        intent.putExtra(Const.Key.WALLPAPER_TYPE, Const.WallpaperType.COLLECTION)
        intent.putExtra(Const.Key.WALLPAPER_LIST, adapter.getWallpaperList())
        intent.putExtra(Const.Key.SELECTED_WALLPAPER, wallpaper)
        intent.putExtra(Const.Key.PAGE, page)
        startActivityForResult(intent, Const.RequestCode.IMAGE_PREVIEW)
    }
}
