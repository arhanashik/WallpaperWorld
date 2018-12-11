package com.workfort.apps.wallpaperworld

import android.animation.Animator
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.view.View
import androidx.databinding.DataBindingUtil
import com.workfort.apps.wallpaperworld.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivitySplashBinding

    private lateinit var mSetLeftIn: AnimatorSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        loadAnimations()
        changeCameraDistance()
        flipCard()
    }

    private fun loadAnimations() {
        mSetLeftIn = AnimatorInflater.loadAnimator(this, R.animator.flip_in) as AnimatorSet
        mSetLeftIn.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {}
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationStart(animation: Animator) {
                mBinding.overflow.visibility = View.VISIBLE
            }
        })
    }

    private fun changeCameraDistance() {
        val distance = 8000
        val scale = resources.displayMetrics.density * distance
        mBinding.imgLogo.cameraDistance = scale
    }

    private fun flipCard() {
        mSetLeftIn.setTarget(mBinding.imgLogo)
        mSetLeftIn.start()
    }
}
