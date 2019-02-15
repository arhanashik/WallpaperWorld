package com.workfort.apps.wallpaperworld.ui.account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.workfort.apps.util.helper.AndroidUtil
import com.workfort.apps.util.helper.StaggeredGridItemDecoration
import com.workfort.apps.util.helper.Toaster
import com.workfort.apps.util.helper.load
import com.workfort.apps.util.lib.remote.ApiService
import com.workfort.apps.wallpaperworld.R
import com.workfort.apps.wallpaperworld.data.local.appconst.Const
import com.workfort.apps.wallpaperworld.data.local.user.UserEntity
import com.workfort.apps.wallpaperworld.data.local.wallpaper.WallpaperEntity
import com.workfort.apps.wallpaperworld.ui.adapter.MyWallpaperStaggeredAdapter
import com.workfort.apps.wallpaperworld.ui.imageviewer.ImageViewerActivity
import com.workfort.apps.wallpaperworld.ui.listener.MyWallpaperClickEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_account.*
import timber.log.Timber

class AccountActivity : AppCompatActivity() {

    companion object {
        fun runActivity(activity: Activity, user: UserEntity) {
            val intent = Intent(activity, AccountActivity::class.java)
            intent.putExtra(Const.Key.USER, user)
            activity.startActivity(intent)
        }
    }

    private var disposable: CompositeDisposable = CompositeDisposable()
    private val apiService by lazy { ApiService.create() }

    private var user: UserEntity? = null

    private lateinit var viewModel: AccountViewModel
    private var adapter: MyWallpaperStaggeredAdapter = MyWallpaperStaggeredAdapter()

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
        val uploadCountStr = "Total ${user!!.uploadCount} upload(s)"
        tv_upload_count.text = uploadCountStr

        initView()
        loadWallpapers()
    }

    private fun initView() {
        rv_wallpapers.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rv_wallpapers.addItemDecoration(StaggeredGridItemDecoration(10, 2))

        adapter.setListener(object: MyWallpaperClickEvent {
            override fun onClickWallpaper(wallpaper: WallpaperEntity, position: Int) {
                openImageViewer(wallpaper)
            }

            override fun onClickWow(wallpaper: WallpaperEntity, position: Int) {
                addToFavorite(wallpaper, position)
            }

            override fun onClickMore(anchorView: View, wallpaper: WallpaperEntity, position: Int) {
                showMoreOptions(anchorView, wallpaper, position)
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

    fun showMoreOptions(anchorView: View, wallpaper: WallpaperEntity, position: Int) {
        val popupMenu = PopupMenu(this, anchorView)
        popupMenu.inflate(R.menu.menu_my_wallpaper)

        val toggleVisibility = popupMenu.menu.getItem(0)
        when(wallpaper.status) {
            Const.WallpaperStatus.REJECTED,
            Const.WallpaperStatus.PENDING -> toggleVisibility.isVisible = false
            Const.WallpaperStatus.HIDDEN -> {
                toggleVisibility.setIcon(R.drawable.ic_show)
                toggleVisibility.title = getString(R.string.label_show)
            }
        }
        AndroidUtil().setForceShowIcon(popupMenu)
        popupMenu.setOnMenuItemClickListener {
            when(it!!.itemId) {
                R.id.action_hide -> {
                    if(wallpaper.status == Const.WallpaperStatus.HIDDEN)
                        wallpaper.status = Const.WallpaperStatus.PUBLISHED
                    else
                        wallpaper.status = Const.WallpaperStatus.HIDDEN
                }
            }
            true
        }
        popupMenu.show()
    }

    fun addToFavorite(wallpaper: WallpaperEntity, position: Int) {
        disposable.add(apiService.addToFavorite(user!!.id, wallpaper.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Toaster(this).showToast(it.message)
                    if(!it.error) {
                        wallpaper.totalWow++
                        adapter.notifyItemChanged(position)
                    }
                }, {
                    Timber.e(it)
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
