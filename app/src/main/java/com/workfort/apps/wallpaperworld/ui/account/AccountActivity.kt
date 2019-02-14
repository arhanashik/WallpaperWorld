package com.workfort.apps.wallpaperworld.ui.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.workfort.apps.util.helper.StaggeredGridItemDecoration
import com.workfort.apps.util.helper.Toaster
import com.workfort.apps.util.helper.load
import com.workfort.apps.util.lib.remote.ApiService
import com.workfort.apps.wallpaperworld.R
import com.workfort.apps.wallpaperworld.data.local.appconst.Const
import com.workfort.apps.wallpaperworld.data.local.user.UserEntity
import com.workfort.apps.wallpaperworld.data.local.wallpaper.WallpaperEntity
import com.workfort.apps.wallpaperworld.ui.adapter.WallpaperStaggeredAdapter
import com.workfort.apps.wallpaperworld.ui.imageviewer.ImageViewerActivity
import com.workfort.apps.wallpaperworld.ui.listener.WallpaperClickEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_account.*
import timber.log.Timber

class AccountActivity : AppCompatActivity() {

    private var disposable: CompositeDisposable = CompositeDisposable()
    private val apiService by lazy { ApiService.create() }

    private var user: UserEntity? = null

    private lateinit var adapter: WallpaperStaggeredAdapter
    private lateinit var viewModel: AccountViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        viewModel = ViewModelProviders.of(this).get(AccountViewModel::class.java)

        user = intent.getParcelableExtra(Const.Key.USER)
        if(user == null) finish()

        img_profile.load(user!!.avatar)
        tv_name.text = user!!.name
        tv_upload_count.text = "Total ${user!!.uploadCount} upload(s)"

        initView()
        loadWallpapers()
    }

    private fun initView() {
        rv_wallpapers.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rv_wallpapers.addItemDecoration(StaggeredGridItemDecoration(10, 2))

        adapter = WallpaperStaggeredAdapter()
        adapter.setListener(object: WallpaperClickEvent {
            override fun onClickWallpaper(wallpaper: WallpaperEntity, position: Int) {
                openImageViewer(wallpaper)
            }
        })
        rv_wallpapers.adapter = adapter

        swipe_refresh.setOnRefreshListener {
            loadWallpapers()
        }
    }

    private var page = 0
    private fun loadWallpapers() {
        swipe_refresh.isRefreshing = true
        if(page == 0) {
            img_no_data.visibility = View.VISIBLE
            rv_wallpapers.visibility = View.INVISIBLE
        }

        disposable.add(apiService.getFavorites(user!!.id,
            Const.WallpaperType.OWN, page)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    swipe_refresh.isRefreshing = false
                    if(it.error) {
                        Toaster(this).showToast(it.message)
                    }else {
                        if(it.wallpapers.isEmpty()){
                            if(adapter.itemCount == 0)
                                Toaster(this).showToast(it.message)
                            else
                                Toaster(this).showToast(getString(R.string.exception_no_more_item))
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
                    Toaster(this).showToast(it.message.toString())
                }
            )
        )
    }

    fun openImageViewer(wallpaper: WallpaperEntity) {
        val intent = Intent(this, ImageViewerActivity::class.java)
        intent.putExtra(Const.Key.WALLPAPER_TYPE, Const.WallpaperType.FAVORITE)
        intent.putExtra(Const.Key.WALLPAPER_LIST, adapter.getWallpaperList())
        intent.putExtra(Const.Key.SELECTED_WALLPAPER, wallpaper)
        intent.putExtra(Const.Key.PAGE, page)
        startActivityForResult(intent, Const.RequestCode.IMAGE_PREVIEW)
    }
}
