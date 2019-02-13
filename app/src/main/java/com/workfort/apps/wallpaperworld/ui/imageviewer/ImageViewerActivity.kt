package com.workfort.apps.wallpaperworld.ui.imageviewer

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.workfort.apps.util.helper.*
import com.workfort.apps.util.lib.remote.ApiService
import com.workfort.apps.wallpaperworld.R
import com.workfort.apps.wallpaperworld.data.local.appconst.Const
import com.workfort.apps.wallpaperworld.data.local.wallpaper.WallpaperEntity
import com.workfort.apps.wallpaperworld.databinding.PromptWallpaperSetOptionBinding
import com.workfort.apps.wallpaperworld.ui.adapter.WallpaperAdapter
import com.workfort.apps.wallpaperworld.ui.adapter.WallpaperDiffCallback
import com.workfort.apps.wallpaperworld.ui.listener.WallpaperClickEvent
import com.yalantis.ucrop.UCrop
import com.yuyakaido.android.cardstackview.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_image_viewer.*
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList


class ImageViewerActivity: AppCompatActivity(), CardStackListener {

    var disposable: CompositeDisposable = CompositeDisposable()
    val apiService by lazy {
        ApiService.create()
    }

    private var wallpaperType: String? = null
    private var wallpaperList = ArrayList<WallpaperEntity>()
    private var selectedWallpaper: WallpaperEntity? = null
    private var searchQuery: String? = null
    private var page: Int = 0

    private var adapter: WallpaperAdapter? = null
    private val manager by lazy { CardStackLayoutManager(this, this) }

    private var visibleItemCount = 3
    private var lastSwipeDirection: Stack<Direction> = Stack()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        wallpaperType = intent.getStringExtra(Const.Key.WALLPAPER_TYPE)
        wallpaperList = intent.getParcelableArrayListExtra(Const.Key.WALLPAPER_LIST)
        selectedWallpaper = intent.getParcelableExtra(Const.Key.SELECTED_WALLPAPER)
        searchQuery = intent.getStringExtra(Const.Key.SEARCH_QUERY)
        page = intent.getIntExtra(Const.Key.PAGE, 0)

        if(TextUtils.isEmpty(wallpaperType) || wallpaperList.isEmpty()) {
            val returnIntent = Intent()
            setResult(RESULT_CANCELED, returnIntent)
            finish()
        }

        adapter = WallpaperAdapter()
        adapter!!.setWallpaperList(wallpaperList)
        adapter!!.setListener(object: WallpaperClickEvent {
            override fun onClickWallpaper(wallpaper: WallpaperEntity, position: Int) {
                card_stack_view.visibility = View.INVISIBLE
                overflow.visibility = View.INVISIBLE
            }
        })

        img_overflow.setOnClickListener{
            if(card_stack_view.visibility != View.VISIBLE) {
                card_stack_view.visibility = View.VISIBLE
                overflow.visibility = View.VISIBLE
            }
        }

        initializeCardStackView()

        btn_set.setOnClickListener {
            showSetOption(adapter!!.getWallpaper(manager.topPosition))
        }
    }

    private fun initializeCardStackView() {
        manager.setStackFrom(StackFrom.Left)
        manager.setVisibleCount(visibleItemCount)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.FREEDOM)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        card_stack_view.layoutManager = manager
        card_stack_view.adapter = adapter
        card_stack_view.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }

        val selectedPos = adapter!!.getWallpaperList().indexOf(selectedWallpaper)
        if(selectedPos > 0) {
            for (i in 1..selectedPos) {
                lastSwipeDirection.push(Direction.Bottom)
            }
            card_stack_view.scrollToPosition(selectedPos)
        }

        if(adapter!!.itemCount == 1 || selectedPos == adapter!!.itemCount - 1) {
            manager.setCanScrollHorizontal(false)
            manager.setCanScrollVertical(false)
        }
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Timber.d("CardStackView:: onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {
        Timber.d("CardStackView:: onCardSwiped: p = ${manager.topPosition}, d = $direction")
        lastSwipeDirection.push(direction)
        if (manager.topPosition == adapter!!.itemCount - 5) {
            paginate()
        }

        val lastPosition = adapter!!.itemCount - 1

        when(manager.topPosition) {
            lastPosition - visibleItemCount -> {
                if(visibleItemCount > 1) {
                    visibleItemCount--
                    manager.setVisibleCount(visibleItemCount)
                }
            }
            lastPosition -> {
                manager.setCanScrollHorizontal(false)
                manager.setCanScrollVertical(false)
            }
        }
    }

    override fun onCardRewound() {
        Timber.d("CardStackView:: onCardRewound: ${manager.topPosition}")
        val lastPosition = adapter!!.itemCount - 1
        when(manager.topPosition) {
            lastPosition - visibleItemCount -> {
                if(visibleItemCount < 3) {
                    visibleItemCount++
                    manager.setVisibleCount(visibleItemCount)
                }
            }
        }
    }

    override fun onCardCanceled() {
        Timber.d("CardStackView:: onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.tv_title)
        Timber.d("CardStackView:: onCardAppeared: ($position) ${textView.text}")

        img_overflow.loadWithPlatter(adapter!!.getWallpaper(position).url, overflow)
    }

    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.tv_title)
        Timber.d("CardStackView:: onCardDisappeared: ($position) ${textView.text}")
    }

    private fun paginate() {
        if(wallpaperType!!.equals(Const.WallpaperType.SEARCH)) {
            disposable.add(apiService.search(1, searchQuery!!, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        if(!it.error && it.wallpapers.isNotEmpty()) {
                            page++
                            includeResultToAdapter(it.wallpapers)
                        }
                    }, {
                        Timber.e(it)
                    }
                )
            )
        }else {
            disposable.add(apiService.getWallpapers(wallpaperType!!, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        if(!it.error && it.wallpapers.isNotEmpty()) {
                            page++
                            includeResultToAdapter(it.wallpapers)
                        }
                    }, {
                        Timber.e(it)
                    }
                )
            )
        }
    }

    private fun includeResultToAdapter(wallpapers: List<WallpaperEntity>) {
        val old = adapter!!.getWallpaperList()
        val new = old.plus(wallpapers)
        val callback = WallpaperDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter!!.setWallpaperList(new)
        result.dispatchUpdatesTo(adapter!!)
    }

    override fun onBackPressed() {
        if(card_stack_view.visibility != View.VISIBLE) {
            card_stack_view.visibility = View.VISIBLE
            overflow.visibility = View.VISIBLE
        }else {
            if(lastSwipeDirection.empty()) {
                super.onBackPressed()
            }else {
                if(lastSwipeDirection.size == adapter!!.itemCount - 1) {
                    manager.setCanScrollHorizontal(true)
                    manager.setCanScrollVertical(true)
                }
                val setting = RewindAnimationSetting.Builder()
                    .setDirection(lastSwipeDirection.pop())
                    .setDuration(200)
                    .setInterpolator(DecelerateInterpolator())
                    .build()
                manager.setRewindAnimationSetting(setting)
                card_stack_view.rewind()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            Timber.e("uri: %s", resultUri.toString())

            btn_set.isEnabled = false
            pb.visibility = View.VISIBLE
            Thread {
                val bitmap = ImageUtil().uriToBitmap(this, resultUri!!)
                WallpaperUtil(this@ImageViewerActivity).setWallpaper(bitmap!!, true)
                Timber.e("width: %s, height: %s", bitmap.width, bitmap.height)

                runOnUiThread {
                    btn_set.isEnabled = true
                    pb.visibility = View.INVISIBLE
                    Toaster(this).showToast("Wallpaper set successfully!")
                }
            }.start()

        } else if (resultCode == UCrop.RESULT_ERROR) {
            Timber.e(UCrop.getError(data!!))
        }
    }

    private fun showSetOption(wallpaper: WallpaperEntity) {
        val binding = DataBindingUtil.inflate<PromptWallpaperSetOptionBinding>(
            layoutInflater, R.layout.prompt_wallpaper_set_option, null, false
        )

        val setDialog = AlertDialog.Builder(this)
            .setView(binding.root)
            .create()

        binding.btnSetFull.setOnClickListener { setDialog.dismiss(); setScrollingWallpaper() }
        binding.txtSetFull.setOnClickListener { setDialog.dismiss(); setScrollingWallpaper() }

        binding.btnSetCustom.setOnClickListener { setDialog.dismiss(); openCropOption(wallpaper) }
        binding.txtSetCustom.setOnClickListener { setDialog.dismiss(); openCropOption(wallpaper) }

        setDialog.show()
    }

    private fun setScrollingWallpaper() {
        btn_set.isEnabled = false
        pb.visibility = View.VISIBLE
        Thread {
            val bitmap = ImageUtil().drawableToBitmap(img_overflow.drawable)
            WallpaperUtil(this).setWallpaper(bitmap, false)

            runOnUiThread {
                btn_set.isEnabled = true
                pb.visibility = View.INVISIBLE
                Toaster(this).showToast("Wallpaper set successfully!")
            }
        }.start()
    }

    private fun openCropOption(wallpaper: WallpaperEntity) {
        try {
            val bitmap = ImageUtil().drawableToBitmap(img_overflow.drawable)
            val uri = FileUtil().saveBitmap(bitmap)
            ImageUtil().cropImage(this, uri)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}