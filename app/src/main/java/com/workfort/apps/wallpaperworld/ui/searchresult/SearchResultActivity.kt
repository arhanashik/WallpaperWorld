package com.workfort.apps.wallpaperworld.ui.searchresult

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.workfort.apps.wallpaperworld.R
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_search_result.*

class SearchResultActivity : AppCompatActivity() {

    private var searchView: SearchView? = null
    private var searchQuery = "jj"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_result)
        setSupportActionBar(layout_appbar.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEARCH) {
            searchQuery = intent.getStringExtra(SearchManager.QUERY)

            Toast.makeText(this, searchQuery, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search_result, menu)

        val searchMenuItem = menu.findItem(R.id.action_search)
        searchView = searchMenuItem.actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView?.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }
        searchMenuItem.expandActionView()
        searchView?.setQuery(searchQuery, true)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId) {
            android.R.id.home -> onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }
}
