package com.workfort.apps.wallpaperworld.ui.searchresult

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.workfort.apps.util.helper.StaggeredGridItemDecoration
import com.workfort.apps.util.helper.Toaster
import com.workfort.apps.util.lib.remote.ApiService
import com.workfort.apps.wallpaperworld.R
import com.workfort.apps.wallpaperworld.data.local.appconst.Const
import com.workfort.apps.wallpaperworld.data.local.wallpaper.WallpaperEntity
import com.workfort.apps.wallpaperworld.ui.adapter.WallpaperDiffCallback
import com.workfort.apps.wallpaperworld.ui.adapter.WallpaperStaggeredAdapter
import com.workfort.apps.wallpaperworld.ui.imageviewer.ImageViewerActivity
import com.workfort.apps.wallpaperworld.ui.listener.WallpaperClickEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_search_result.*
import kotlinx.android.synthetic.main.appbar.view.*
import timber.log.Timber

class SearchResultActivity : AppCompatActivity() {

    var disposable: CompositeDisposable = CompositeDisposable()
    val apiService by lazy {
        ApiService.create()
    }

    private var searchView: SearchView? = null
    private var searchQuery = ""

    private var adapter: WallpaperStaggeredAdapter = WallpaperStaggeredAdapter()
    private lateinit var viewModel: SearchViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        setSupportActionBar(layout_appbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

        handleIntent(intent)

        initView()
    }

    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEARCH) {
            searchQuery = intent.getStringExtra(SearchManager.QUERY)
            if(!TextUtils.isEmpty(searchQuery)) {
                page = 0
                adapter.clear()
                loadWallpapers()
            }
        }
    }

    private fun initView() {
        rv_wallpapers.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        rv_wallpapers.addItemDecoration(StaggeredGridItemDecoration(10, 2))

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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search_result, menu)

        val searchMenuItem = menu.findItem(R.id.action_search)
        searchView = searchMenuItem.actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView?.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }
        searchMenuItem.expandActionView()
        searchView?.setQuery(searchQuery, true)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    private var page = 0
    private fun loadWallpapers() {
        swipe_refresh.isRefreshing = true
        if(page == 0) {
            img_no_data.visibility = View.VISIBLE
            rv_wallpapers.visibility = View.INVISIBLE
        }

        disposable.add(apiService.search(1, searchQuery, page)
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
                            includeResultToAdapter(it.wallpapers)
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

    private fun includeResultToAdapter(wallpapers: List<WallpaperEntity>) {
        val old = adapter.getWallpaperList()
        val new = old.plus(wallpapers)
        val callback = WallpaperDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter.setWallpaperList(new)
        result.dispatchUpdatesTo(adapter)
    }

    fun openImageViewer(wallpaper: WallpaperEntity) {
        val intent = Intent(this, ImageViewerActivity::class.java)
        intent.putExtra(Const.Key.WALLPAPER_TYPE, Const.WallpaperType.SEARCH)
        intent.putExtra(Const.Key.WALLPAPER_LIST, adapter.getWallpaperList())
        intent.putExtra(Const.Key.SELECTED_WALLPAPER, wallpaper)
        intent.putExtra(Const.Key.SEARCH_QUERY, searchQuery)
        intent.putExtra(Const.Key.PAGE, page)
        startActivityForResult(intent, Const.RequestCode.IMAGE_PREVIEW)
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}
