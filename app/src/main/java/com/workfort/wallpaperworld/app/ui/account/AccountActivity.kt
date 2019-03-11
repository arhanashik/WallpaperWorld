package com.workfort.wallpaperworld.app.ui.account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.workfort.wallpaperworld.R
import com.workfort.wallpaperworld.app.data.local.appconst.Const
import com.workfort.wallpaperworld.app.data.local.pref.PrefProp
import com.workfort.wallpaperworld.app.data.local.pref.PrefUtil
import com.workfort.wallpaperworld.app.data.local.user.UserEntity
import com.workfort.wallpaperworld.app.data.local.wallpaper.WallpaperEntity
import com.workfort.wallpaperworld.app.ui.adapter.MyWallpaperStaggeredAdapter
import com.workfort.wallpaperworld.app.ui.imageviewer.ImageViewerActivity
import com.workfort.wallpaperworld.app.ui.listener.MyWallpaperClickEvent
import com.workfort.wallpaperworld.app.ui.uploadwallpaper.WallpaperUploadDialog
import com.workfort.wallpaperworld.util.helper.AndroidUtil
import com.workfort.wallpaperworld.util.helper.StaggeredGridItemDecoration
import com.workfort.wallpaperworld.util.helper.Toaster
import com.workfort.wallpaperworld.util.helper.load
import com.workfort.wallpaperworld.util.lib.remote.ApiService
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
        else {
            initView()
            loadWallpapers()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_account, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            R.id.action_log_out -> logout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initView() {
        img_profile.load(user?.avatar)
        tv_name.text = user?.name
        var uploadCountStr = "Total ${user?.uploadCount} upload(s)"
        tv_upload_count.text = uploadCountStr

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

        btn_new_wallpaper.setOnClickListener {
            val bundle = Bundle()
            bundle.putParcelable(Const.Key.USER, user)

            val uploadDialog = WallpaperUploadDialog()
            uploadDialog.arguments = bundle
            uploadDialog.setListener(object: WallpaperUploadDialog.WallpaperUploadEvent {
                override fun onNewUpload(wallpaper: WallpaperEntity) {
                    adapter.addWallpaper(0, wallpaper)
                    user?.uploadCount = user?.uploadCount!!.plus(1)
                    uploadCountStr = "Total ${user?.uploadCount} upload(s)"
                    tv_upload_count.text = uploadCountStr
                    PrefUtil.set(PrefProp.USER, user!!)
                }
            })
            uploadDialog.show(
                supportFragmentManager.beginTransaction(), WallpaperUploadDialog::class.java.name
            )

//            val dialog = JavaDatePickerDialog.newInstance(Calendar.getInstance().time)
//            dialog.onOk = { date ->
//
//            }
//            dialog.show(supportFragmentManager, "editDate")
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
                    Toaster(this).showToast(getString(R.string.unknown_exception))
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

    private var likeInProgress: Boolean = false
    fun addToFavorite(wallpaper: WallpaperEntity, position: Int) {

        if(likeInProgress) return

        likeInProgress = true
        disposable.add(apiService.addToFavorite(user?.id!!, wallpaper.id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    likeInProgress = false
                    Toaster(this).showToast(it.message)
                    if(!it.error) {
                        wallpaper.totalWow++
                        adapter.notifyItemChanged(position)
                    }
                }, {
                    likeInProgress = false
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

    private fun logout() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.text_are_you_sure))
            .setMessage(getString(R.string.log_out_message))
            .setPositiveButton(getString(R.string.label_log_out)) {
                    _, _ ->
                when(PrefUtil.get(PrefProp.AUTH_TYPE, "")) {
                    Const.AuthType.GOOGLE -> {
                        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build()
                        GoogleSignIn.getClient(this, gso).signOut()
                    }
                    Const.AuthType.FACEBOOK -> {
                        LoginManager.getInstance().logOut()
                    }
                }
                PrefUtil.set(PrefProp.IS_LOGGED_IN, false)
                PrefUtil.set(PrefProp.USER, PrefProp.ACTION_DELETE)
                PrefUtil.set(PrefProp.AUTH_TYPE, PrefProp.ACTION_DELETE)
                finish()
            }
            .setNegativeButton(getString(R.string.label_cancel)) {
                    _,_->
            }
            .create()
            .show()
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}
