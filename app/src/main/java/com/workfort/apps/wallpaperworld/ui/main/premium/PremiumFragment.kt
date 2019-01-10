package com.workfort.apps.wallpaperworld.ui.main.premium

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.workfort.apps.wallpaperworld.R
import com.workfort.apps.wallpaperworld.data.local.wallpaper.WallpaperEntity
import com.workfort.apps.wallpaperworld.ui.adapter.WallpaperStaggeredAdapter
import com.workfort.apps.wallpaperworld.ui.listener.WallpaperClickEvent
import com.workfort.apps.util.helper.StaggeredGridItemDecoration
import com.workfort.apps.util.helper.Toaster
import com.workfort.apps.wallpaperworld.data.local.appconst.Const
import com.workfort.apps.wallpaperworld.ui.main.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_premium.*
import timber.log.Timber

class PremiumFragment : Fragment() {

    companion object {
        fun newInstance() = PremiumFragment()
    }

    private lateinit var adapter: WallpaperStaggeredAdapter
    private lateinit var viewModel: PremiumViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_premium, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PremiumViewModel::class.java)

        initView()
        loadWallpapers()
    }

    private fun initView() {
        rv_wallpapers.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rv_wallpapers.addItemDecoration(StaggeredGridItemDecoration(10, 2))

        adapter = WallpaperStaggeredAdapter()
        adapter.setListener(object: WallpaperClickEvent {
            override fun onClickWallpaper(wallpaper: WallpaperEntity, position: Int) {
                Toast.makeText(context, "${wallpaper.title} : $position", Toast.LENGTH_SHORT).show()
            }
        })
        rv_wallpapers.adapter = adapter

        swipe_refresh.setOnRefreshListener {
            loadWallpapers()
        }
    }

    private fun loadWallpapers() {
        swipe_refresh.isRefreshing = true
        img_no_data.visibility = View.VISIBLE
        rv_wallpapers.visibility = View.INVISIBLE

        val parent = (activity as MainActivity)

        parent.disposable.add(parent.apiService.getWallpapers(Const.WallpaperType.COLLECTION)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    swipe_refresh.isRefreshing = false
                    if(it.error || it.wallpapers.isEmpty()) {
                        Toaster(context!!).showToast(it.message)
                    }else {
                        img_no_data.visibility = View.INVISIBLE
                        rv_wallpapers.visibility = View.VISIBLE
                        adapter.setWallpaperList(it.wallpapers)
                    }
                }, {
                    Timber.e(it)
                    swipe_refresh.isRefreshing = false
                    Toaster(context!!).showToast(it.message.toString())
                }
            )
        )
    }
}
