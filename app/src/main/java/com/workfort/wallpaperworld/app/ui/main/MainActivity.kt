package com.workfort.wallpaperworld.app.ui.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import com.workfort.wallpaperworld.R
import com.workfort.wallpaperworld.app.data.local.appconst.Const
import com.workfort.wallpaperworld.app.data.local.pref.PrefProp
import com.workfort.wallpaperworld.app.data.local.pref.PrefUtil
import com.workfort.wallpaperworld.app.data.local.user.UserEntity
import com.workfort.wallpaperworld.app.ui.account.AccountActivity
import com.workfort.wallpaperworld.app.ui.adapter.PagerAdapter
import com.workfort.wallpaperworld.app.ui.main.collection.CollectionFragment
import com.workfort.wallpaperworld.app.ui.main.favourite.FavoriteFragment
import com.workfort.wallpaperworld.app.ui.main.popular.PopularFragment
import com.workfort.wallpaperworld.app.ui.main.premium.PremiumFragment
import com.workfort.wallpaperworld.app.ui.main.topchart.TopChartFragment
import com.workfort.wallpaperworld.databinding.CustomTabBinding
import com.workfort.wallpaperworld.databinding.PromptCreateAccountBinding
import com.workfort.wallpaperworld.util.helper.AndroidUtil
import com.workfort.wallpaperworld.util.helper.Toaster
import com.workfort.wallpaperworld.util.lib.remote.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import timber.log.Timber
import java.net.URL
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    var disposable: CompositeDisposable = CompositeDisposable()
    val apiService by lazy { ApiService.create() }

    // for google login
    private var mGoogleSignInClient: GoogleSignInClient? = null

    // for facebook login
    private var callbackManager: CallbackManager? = null

    private var signInDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val pagerAdapter = PagerAdapter(supportFragmentManager)
        pagerAdapter.addItem(getString(R.string.label_collection), CollectionFragment.newInstance())
        pagerAdapter.addItem(getString(R.string.label_top_chart), TopChartFragment.newInstance())
        pagerAdapter.addItem(getString(R.string.label_popular), PopularFragment.newInstance())
        pagerAdapter.addItem(getString(R.string.label_premium), PremiumFragment.newInstance())
        pagerAdapter.addItem(getString(R.string.label_favourite), FavoriteFragment.newInstance())

        view_pager.adapter = pagerAdapter
        view_pager.offscreenPageLimit = pagerAdapter.count
        tab_layout.setupWithViewPager(view_pager)

        setupTabs()
        AndroidUtil().getKeyHash()
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == R.id.action_account) {
            performAccountAction()

            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == Const.RequestCode.GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)

                Toaster(this).showToast(getString(R.string.complete_sign_up_message) + account!!.displayName)
                signUp(account.displayName!!, account.id!!, account.id!!, account.email!!,
                    account.photoUrl.toString(), Const.AuthType.GOOGLE)

                addLoginAnalytics(Const.AuthType.GOOGLE, account.email!!)
            } catch (e: ApiException) {
                Timber.w("signInResult:failed code=%s", e.statusCode)
                Toaster(this).showToast(getString(R.string.unknown_exception))
            }
        }else {
            callbackManager?.onActivityResult(requestCode, resultCode, data)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() {
        if(view_pager.currentItem == 0) super.onBackPressed()
        else view_pager.currentItem = 0
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

    private fun performAccountAction() {

        if(PrefUtil.get(PrefProp.IS_LOGGED_IN, false)!!) {
            val user = PrefUtil.get<UserEntity>(PrefProp.USER, null)
            AccountActivity.runActivity(this, user!!)
        }else {

            val prompt = DataBindingUtil.inflate<PromptCreateAccountBinding>(layoutInflater,
                R.layout.prompt_create_account, null, false)

            prompt.btnFb.setOnClickListener {
                configureFacebookSignIn()
                performFacebookSignIn()
            }

            prompt.btnGoogle.setOnClickListener {
                configureGoogleSignIn()
                performGoogleSignIn()
            }

            signInDialog = AlertDialog.Builder(this).setView(prompt.root).create()
            signInDialog!!.show()
        }
    }

    private fun configureFacebookSignIn() {
        callbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(callbackManager, object: FacebookCallback<LoginResult> {
             override fun onSuccess(result: LoginResult?) { handleFacebookSignInData(result?.accessToken) }

             override fun onCancel() {
                 Toaster(this@MainActivity).showToast(R.string.user_canceled_fb_login_exception)
             }

             override fun onError(error: FacebookException?) {
                 Timber.e(error)
                 Toaster(this@MainActivity).showToast(R.string.unknown_exception)
             }
        })
    }

    private fun performFacebookSignIn() {
        val accessToken = AccessToken.getCurrentAccessToken()
        val needToLogIn = accessToken == null || accessToken.isExpired
        if(needToLogIn) {
            val permissions = Arrays.asList("public_profile", "email")
            //btn_fb_login.setReadPermissions(permissions)
            //btn_fb_login.performClick()
            LoginManager.getInstance().logInWithReadPermissions(this, permissions)
            return
        }

        handleFacebookSignInData(accessToken)
    }

    private fun handleFacebookSignInData(accessToken: AccessToken?) {
        if(accessToken == null) return

        val request = GraphRequest.newMeRequest(accessToken) { obj, _ ->
            try {
                val id = obj.optString("id")
                val name = obj.optString("name")
                var email = obj.optString("email")
                val image = URL("http://graph.facebook.com/$id/picture?type=large")

                if(TextUtils.isEmpty(email)) email = id
                signUp(name, id, id, email, image.toString(), Const.AuthType.GOOGLE)
                addLoginAnalytics(Const.AuthType.FACEBOOK, email)
            }catch (ex: JSONException) {
                ex.printStackTrace()
                Toaster(this).showToast(R.string.unknown_exception)
            }
        }
        val parameters = Bundle()
        parameters.putString("fields", "id, name, email")
        request.parameters = parameters
        request.executeAsync()
    }

    private fun configureGoogleSignIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun performGoogleSignIn() {
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if(account != null) {
            Toaster(this).showToast(getString(R.string.complete_sign_up_message) + account.displayName)
            signUp(account.displayName!!, account.id!!, account.id!!, account.email!!, account.photoUrl.toString(),
                Const.AuthType.GOOGLE)

            addLoginAnalytics(Const.AuthType.GOOGLE, account.email!!)
            return
        }

        startActivityForResult(mGoogleSignInClient!!.signInIntent, Const.RequestCode.GOOGLE_SIGN_IN)
    }

    private fun signUp(name: String, username: String, password: String, email: String, avatar: String,
                       authType: String) {
        disposable.add(apiService.signUp(name, username, password, email, avatar, authType)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    Toaster(this).showToast(it.message)
                    if(!it.error) {
                        PrefUtil.set(PrefProp.IS_LOGGED_IN, true)
                        PrefUtil.set(PrefProp.USER, it.user!!)
                        PrefUtil.set(PrefProp.AUTH_TYPE, authType)
                        AccountActivity.runActivity(this, it.user)
                        signInDialog!!.dismiss()
                    }
                }, {
                    Timber.e(it)
                    Toaster(this).showToast(getString(R.string.unknown_exception))
                }
            )
        )
    }

    private fun addLoginAnalytics(method: String, extra: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.METHOD, method)
        bundle.putString(FirebaseAnalytics.Param.VALUE, extra)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}
