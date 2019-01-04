package com.workfort.apps.wallpaperworld.ui.imageviewer

import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.workfort.apps.wallpaperworld.R
import com.workfort.apps.wallpaperworld.data.DummyData
import com.workfort.apps.wallpaperworld.data.local.wallpaper.WallpaperEntity
import com.workfort.apps.wallpaperworld.ui.adapter.WallpaperAdapter
import com.workfort.apps.wallpaperworld.ui.adapter.WallpaperDiffCallback
import com.workfort.apps.wallpaperworld.ui.listener.WallpaperClickEvent
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom
import kotlinx.android.synthetic.main.activity_image_viewer.*


class ImageViewerActivity: AppCompatActivity(), CardStackListener {

    private var adapter: WallpaperAdapter? = null
    private val manager by lazy { CardStackLayoutManager(this, this) }

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
        manager.setVisibleCount(4)
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
        Log.d("CardStackView", "onCardDragging: d = ${direction.name}, r = $ratio")
    }

    override fun onCardSwiped(direction: Direction) {
        Log.d("CardStackView", "onCardSwiped: p = ${manager.topPosition}, d = $direction")
        if (manager.topPosition == adapter!!.itemCount - 5) {
            paginate()
        }

        var imgRes = R.drawable.img_splash2
        if(manager.topPosition%2==0) {
            imgRes = R.drawable.img_splash
        }
        if(manager.topPosition == 3 || manager.topPosition == 8 || manager.topPosition == 10)
            imgRes = R.drawable.img_splash3

        img_overflow.setImageBitmap(BitmapFactory.decodeResource(resources, imgRes))
//        Palette.from(imgBitmap).generate {
//            val defaultColor1 = ContextCompat.getColor(this, R.color.colorPrimary)
//            val defaultColor2 = ContextCompat.getColor(this, R.color.colorPrimaryDark)
//
//            container.background = getBackgroundGradient(
//                it!!.getDarkVibrantColor(defaultColor2), it.getDarkMutedColor(defaultColor1)
//            )
//        }
    }

    override fun onCardRewound() {
        Log.d("CardStackView", "onCardRewound: ${manager.topPosition}")
    }

    override fun onCardCanceled() {
        Log.d("CardStackView", "onCardCanceled: ${manager.topPosition}")
    }

    override fun onCardAppeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.tv_title)
        Log.d("CardStackView", "onCardAppeared: ($position) ${textView.text}")
    }

    override fun onCardDisappeared(view: View, position: Int) {
        val textView = view.findViewById<TextView>(R.id.tv_title)
        Log.d("CardStackView", "onCardDisappeared: ($position) ${textView.text}")
    }

    private fun paginate() {
        val old = adapter!!.getWallpaperList()
        val new = old.plus(DummyData.generateDummyData())
        val callback = WallpaperDiffCallback(old, new)
        val result = DiffUtil.calculateDiff(callback)
        adapter!!.setWallpaperList(new)
        result.dispatchUpdatesTo(adapter!!)
    }
}