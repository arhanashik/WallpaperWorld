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
import com.workfort.apps.wallpaperworld.ui.main.popular.PopularFragment
import com.workfort.apps.wallpaperworld.ui.main.premium.PremiumFragment
import com.workfort.apps.wallpaperworld.ui.main.topchart.TopChartFragment
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var fragmentStack: Stack<Int> = Stack()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val pagerAdapter = PagerAdapter(supportFragmentManager)
        pagerAdapter.addItem(getString(R.string.label_collection), CollectionFragment.newInstance())
        pagerAdapter.addItem(getString(R.string.label_top_chart), TopChartFragment.newInstance())
        pagerAdapter.addItem(getString(R.string.label_popular), PopularFragment.newInstance())
        pagerAdapter.addItem(getString(R.string.label_premium), PremiumFragment.newInstance())
        pagerAdapter.addItem(getString(R.string.label_favourite), FavouriteFragment.newInstance())

        view_pager.adapter = pagerAdapter
        view_pager.offscreenPageLimit = pagerAdapter.count
        tab_layout.setupWithViewPager(view_pager)
        fragmentStack.push(0)

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
        tabThree.tab.text = getString(R.string.label_popular)
        tabThree.tab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_star_border, 0, 0)
        tab_layout.getTabAt(2)?.customView = tabThree.root

        val tabFour = DataBindingUtil.inflate<CustomTabBinding>(layoutInflater, R.layout.custom_tab,
            null, false)
        tabFour.tab.text = getString(R.string.label_premium)
        tabFour.tab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_diamond_border, 0, 0)
        tab_layout.getTabAt(3)?.customView = tabFour.root

        val tabFive = DataBindingUtil.inflate<CustomTabBinding>(layoutInflater, R.layout.custom_tab,
            null, false)
        tabFive.tab.text = getString(R.string.label_favourite)
        tabFive.tab.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_favorite_border, 0, 0)
        tab_layout.getTabAt(4)?.customView = tabFive.root

        tab_layout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val view = tab?.customView
                val tabPosition = tab?.position
                if (view is TextView) {
                    setTabSelection(true, tabPosition!!, view)
                }

                if (fragmentStack.contains(tabPosition)) {
                    fragmentStack.remove(fragmentStack.indexOf(tabPosition))
                    fragmentStack.push(tabPosition)
                } else {
                    fragmentStack.push(tabPosition)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                val view = tab?.customView
                if (view is TextView) {
                    setTabSelection(false, tab.position, view)
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

    override fun onBackPressed() {
        if (fragmentStack.isNotEmpty()) {
            fragmentStack.pop()
            if(view_pager.currentItem == 0) super.onBackPressed()
            else view_pager.currentItem = fragmentStack.lastElement()
        } else {
            super.onBackPressed()
        }
    }

    private fun setTabSelection(selected: Boolean, position: Int, tv: TextView) {
        var icon = R.drawable.ic_collection_border
        var color = ContextCompat.getColor(this@MainActivity, R.color.colorSilver)
        var textWeight = Typeface.NORMAL

        if(selected) {
            when (position){
                0 -> icon = R.drawable.ic_collection_fill
                1 -> icon = R.drawable.ic_chart_fill
                2 -> icon = R.drawable.ic_star_fill
                3 -> icon = R.drawable.ic_diamond_fill
                4 -> icon = R.drawable.ic_favorite_fill
            }

            color = Color.WHITE
            textWeight = Typeface.BOLD
        }else {
            when (position){
                0 -> icon = R.drawable.ic_collection_border
                1 -> icon = R.drawable.ic_chart_border
                2 -> icon = R.drawable.ic_star_border
                3 -> icon = R.drawable.ic_diamond_border
                4 -> icon = R.drawable.ic_favorite_border
            }
        }

        tv.setTextColor(color)
        tv.setTypeface(null, textWeight)
        tv.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0)
    }
}
