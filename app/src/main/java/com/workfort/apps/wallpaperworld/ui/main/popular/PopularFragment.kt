package com.workfort.apps.wallpaperworld.ui.main.popular

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.workfort.apps.wallpaperworld.R

class PopularFragment : Fragment() {

    companion object {
        fun newInstance() = PopularFragment()
    }

    private lateinit var viewModel: PopularViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_popular, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(PopularViewModel::class.java)
    }

}
