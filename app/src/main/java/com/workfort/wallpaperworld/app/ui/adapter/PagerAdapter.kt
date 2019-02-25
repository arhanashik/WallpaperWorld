package com.workfort.wallpaperworld.app.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/*
*  ****************************************************************************
*  * Created by : Arhan Ashik on 12/14/2018 at 5:52 PM.
*  * Email : ashik.pstu.cse@gmail.com
*  * 
*  * Last edited by : Arhan Ashik on 12/14/2018.
*  * 
*  * Last Reviewed by : <Reviewer Name> on <mm/dd/yy>  
*  ****************************************************************************
*/

class PagerAdapter (fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private var titles: ArrayList<String> = ArrayList()
    private var fragments: ArrayList<Fragment> = ArrayList()

    fun addItem(title: String, fragment: Fragment) {
        titles.add(title)
        fragments.add(fragment)
    }

    fun removeItem(position: Int) {
        titles.removeAt(position)
        fragments.removeAt(position)
    }

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }
}