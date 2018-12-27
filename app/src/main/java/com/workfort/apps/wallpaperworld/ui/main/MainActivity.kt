package com.workfort.apps.wallpaperworld.ui.main

import android.app.SearchManager
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import com.workfort.apps.wallpaperworld.R
import com.workfort.apps.wallpaperworld.databinding.CustomTabBinding
import com.workfort.apps.wallpaperworld.ui.main.adapter.PagerAdapter
import com.workfort.apps.wallpaperworld.ui.main.collection.CollectionFragment
import com.workfort.apps.wallpaperworld.ui.main.favourite.FavouriteFragment
import com.workfort.apps.wallpaperworld.ui.main.topchart.TopChartFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val pagerAdapter = PagerAdapter(supportFragmentManager)
        pagerAdapter.addItem(getString(R.string.label_collection), CollectionFragment.newInstance())
        pagerAdapter.addItem(getString(R.string.label_top_chart), TopChartFragment.newInstance())
        pagerAdapter.addItem(getString(R.string.label_favourite), FavouriteFragment.newInstance())

        view_pager.adapter = pagerAdapter
        tab_layout.setupWithViewPager(view_pager)

        setupTabs()
    }

    private fun setupTabs() {
        val tabOne = DataBindingUtil.inflate<CustomTabBinding>(layoutInflater, R.layout.custom_tab,
            null, false)
        tabOne.tab.text = getString(R.string.label_collection)
        tabOne.tab.setTextColor(Color.WHITE)
        tabOne.tab.setTypeface(null, Typeface.BOLD)
        tabOne.tab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_collection_fill, 0, 0)
        tab_layout.getTabAt(0)?.customView = tabOne.root

        val tabTwo = DataBindingUtil.inflate<CustomTabBinding>(layoutInflater, R.layout.custom_tab,
            null, false)
        tabTwo.tab.text = getString(R.string.label_top_chart)
        tabTwo.tab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_chart_border, 0, 0)
        tab_layout.getTabAt(1)?.customView = tabTwo.root

        val tabThree = DataBindingUtil.inflate<CustomTabBinding>(layoutInflater, R.layout.custom_tab,
            null, false)
        tabThree.tab.text = getString(R.string.label_favourite)
        tabThree.tab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_border, 0, 0)
        tab_layout.getTabAt(2)?.customView = tabThree.root

        tab_layout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val view = tab?.customView
                if (view is TextView) {
                    var icon = R.drawable.ic_collection_fill
                    if (tab.position == 1)
                        icon = R.drawable.ic_chart_fill
                    else if (tab.position == 2) icon = R.drawable.ic_favorite_fill
                    view.setTextColor(Color.WHITE)
                    view.setTypeface(null, Typeface.BOLD)
                    view.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val view = tab?.customView
                if (view is TextView) {
                    var icon = R.drawable.ic_collection_border
                    if (tab.position == 1)
                        icon = R.drawable.ic_chart_border
                    else if (tab.position == 2) icon = R.drawable.ic_favorite_border
                    view.setTextColor(ContextCompat.getColor(this@MainActivity, R.color.colorSilver))
                    view.setTypeface(null, Typeface.NORMAL)
                    view.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        (menu.findItem(R.id.action_search).actionView as SearchView).apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }

        return true
    }
}
