package com.workfort.apps.wallpaperworld.ui.main.topchart

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.workfort.apps.wallpaperworld.R
import kotlinx.android.synthetic.main.fragment_top_chart.*

class TopChartFragment : Fragment() {

    companion object {
        fun newInstance() = TopChartFragment()
    }

    private lateinit var viewModel: TopChartViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_top_chart, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TopChartViewModel::class.java)

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
