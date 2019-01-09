package com.workfort.apps.wallpaperworld.ui.imageviewer

import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.workfort.apps.wallpaperworld.R
import com.workfort.apps.wallpaperworld.data.DummyData
import com.workfort.apps.wallpaperworld.data.local.wallpaper.WallpaperEntity
import com.workfort.apps.wallpaperworld.ui.adapter.WallpaperAdapter
import com.workfort.apps.wallpaperworld.ui.adapter.WallpaperDiffCallback
import com.workfort.apps.wallpaperworld.ui.listener.WallpaperClickEvent
import com.yuyakaido.android.cardstackview.*
import kotlinx.android.synthetic.main.activity_image_viewer.*
import timber.log.Timber
import java.util.*
import androidx.core.content.ContextCompat
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.request.target.Target
import com.workfort.apps.util.helper.*
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import java.io.*


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

        btn_set.setOnClickListener {
//            val wallpaper = BitmapFactory.decodeResource(resources, R.drawable.img_splash2)
//
//            val displayParams = AndroidUtil().getDisplayParams(this)
//            Timber.e("width: %s, height: %s", displayParams.width, displayParams.height)
//
//            val target = object : SimpleTarget<Bitmap>(displayParams.width, displayParams.height) {
//                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
//                    WallpaperUtil(this@ImageViewerActivity).setWallpaper(bitmap)
//                    Timber.e("width: %s, height: %s", bitmap.width, bitmap.height)
//                }
//            }
//
//            Glide.with(this)
//                .asBitmap()
//                .load(R.drawable.img_splash3)
//                .into(target)

            openCropOption(R.drawable.img_splash)
        }
    }

    private fun initializeCardStackView() {
        manager.setStackFrom(StackFrom.Top)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            Timber.e("uri: %s", resultUri.toString())

            val displayParams = AndroidUtil().getDisplayParams(this)

            val target = object : SimpleTarget<Bitmap>(displayParams.width, displayParams.height) {
                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                    WallpaperUtil(this@ImageViewerActivity).setWallpaper(bitmap)
                    Timber.e("width: %s, height: %s", bitmap.width, bitmap.height)
                }
            }

//            val target = object : GliderTarget(){
//                override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
//                    WallpaperUtil(this@ImageViewerActivity).setWallpaper(bitmap)
//                    Timber.e("width: %s, height: %s", bitmap.width, bitmap.height)
//                }
//            }

            Glide.with(this)
                .asBitmap()
                .load(resultUri)
                .into(target)

        } else if (resultCode == UCrop.RESULT_ERROR) {
            Timber.e(UCrop.getError(data!!))
        }
    }

    private fun openCropOption(resource: Int) {
        try {
            val uri = FileUtil().saveBitmap(BitmapFactory.decodeResource(resources, resource))
            ImageUtil().cropImage(this, uri)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}