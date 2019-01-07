package com.workfort.apps.wallpaperworld.ui.imageviewer

import android.graphics.*
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.workfort.apps.wallpaperworld.R
import com.workfort.apps.wallpaperworld.data.DummyData
import com.workfort.apps.wallpaperworld.data.local.wallpaper.WallpaperEntity
import com.workfort.apps.wallpaperworld.ui.adapter.WallpaperAdapter
import com.workfort.apps.wallpaperworld.ui.adapter.WallpaperDiffCallback
import com.workfort.apps.wallpaperworld.ui.listener.WallpaperClickEvent
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.activity_image_viewer.*
import org.json.JSONObject
import timber.log.Timber
import java.util.*


class ImageViewerActivity: AppCompatActivity(), CardStackListener {

    private var adapter: WallpaperAdapter? = null
    private val manager by lazy { CardStackLayoutManager(this, this) }

    private var visibleItemCount = 4
    private var lastSwipeDirection: Stack<Direction> = Stack()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        adapter = WallpaperAdapter()
        adapter!!.setWallpaperList(DummyData.generateDummyData())
        adapter!!.setListener(object: WallpaperClickEvent {
            override fun onClickWallpaper(wallpaper: WallpaperEntity, position: Int) {

            }
        })

        initializeCardStackView()
    }

    private fun initializeCardStackView() {
        manager.setStackFrom(StackFrom.Top)
        manager.setVisibleCount(visibleItemCount)
        manager.setTranslationInterval(8.0f)
        manager.setScaleInterval(0.95f)
        manager.setSwipeThreshold(0.3f)
        manager.setMaxDegree(20.0f)
        manager.setDirections(Direction.HORIZONTAL)
        manager.setCanScrollHorizontal(true)
        manager.setCanScrollVertical(true)
        card_stack_view.layoutManager = manager
        card_stack_view.adapter = adapter
        card_stack_view.itemAnimator.apply {
            if (this is DefaultItemAnimator) {
                supportsChangeAnimations = false
            }
        }
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
        Timber.d("CardStackView:: onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {
        Timber.d("CardStackView:: onCardSwiped: p = ${manager.topPosition}, d = $direction")
        lastSwipeDirection.push(direction)
        if (manager.topPosition == adapter!!.itemCount - 5) {
            //paginate()
        }

        var imgRes = R.drawable.img_splash2
        if(manager.topPosition%2==0) {
            imgRes = R.drawable.img_splash
        }
        if(manager.topPosition == 3 || manager.topPosition == 8 || manager.topPosition == 10)
            imgRes = R.drawable.img_splash3

        val lastPosition = adapter!!.itemCount - 1
        Timber.d("CardStackView:: onCardSwiped: lastPosition = $lastPosition")

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

        img_overflow.setImageBitmap(BitmapFactory.decodeResource(resources, imgRes))
    }

    override fun onCardRewound() {
        Timber.d("CardStackView:: onCardRewound: ${manager.topPosition}")
        val lastPosition = adapter!!.itemCount - 1
        when(manager.topPosition) {
            lastPosition - visibleItemCount -> {
                if(visibleItemCount < 4) {
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
    }

    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.tv_title)
        Timber.d("CardStackView:: onCardDisappeared: ($position) ${textView.text}")
    }

    private fun paginate() {
        val old = adapter!!.getWallpaperList()
        val new = old.plus(DummyData.generateDummyData())
        val callback = WallpaperDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter!!.setWallpaperList(new)
        result.dispatchUpdatesTo(adapter!!)
    }

    override fun onBackPressed() {
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