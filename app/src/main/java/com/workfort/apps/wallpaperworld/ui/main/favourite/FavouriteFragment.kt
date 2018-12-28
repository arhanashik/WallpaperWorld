package com.workfort.apps.wallpaperworld.ui.main.favourite

import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.workfort.apps.wallpaperworld.R
import kotlinx.android.synthetic.main.fragment_favourite.*

class FavouriteFragment : Fragment() {

    companion object {
        fun newInstance() = FavouriteFragment()
    }

    private lateinit var viewModel: FavouriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_favourite, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(FavouriteViewModel::class.java)

        initView()
    }

    private fun initView() {
        swipe_refresh.setOnRefreshListener {
            Handler().postDelayed({
                swipe_refresh.isRefreshing = false
            }, 5000)
        }
    }
}
