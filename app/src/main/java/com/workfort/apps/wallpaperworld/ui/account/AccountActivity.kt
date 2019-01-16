package com.workfort.apps.wallpaperworld.ui.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.workfort.apps.wallpaperworld.R



class AccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}
