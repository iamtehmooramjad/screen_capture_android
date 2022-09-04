package com.screencapture.android.ui.navigation

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.screencapture.android.R
import com.screencapture.android.databinding.ActivityHomeBinding
import com.screencapture.android.ui.base.BaseActivity
import com.screencapture.android.ui.screenshots.ScreenshotsActivity
import com.screencapture.android.ui.settings.SettingsActivity
import com.screencapture.android.utils.fadeVisibility
import com.screencapture.android.utils.showShortToast
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var bindings: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        bindings = DataBindingUtil.setContentView(this, R.layout.activity_home)
        bindings.lifecycleOwner = this
        initUi()
    }

     fun initUi() {

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val drawerLayout: DrawerLayout = bindings.drawerLayout
        val navView: NavigationView = bindings.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_home_navigation) as NavHostFragment
        navController = navHostFragment.navController

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.screenshotsActivity,
                R.id.settingsActivity
            ), drawerLayout
        )

        navView.setupWithNavController(navController)

        bindings.appBarMainNavigation.ivBack.setOnClickListener {
            navController.navigateUp()
        }

        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.shareAppLink->{
                    shareAppLink()
                }
                R.id.appPrivacyPolicy->{
                    showShortToast("Privacy Policy")
                }
                R.id.screenshotsActivity->{
                   startActivity(Intent(this,ScreenshotsActivity::class.java))
                }
                R.id.settingsActivity->{
                    startActivity(Intent(this,SettingsActivity::class.java))
                }
            }
            return@setNavigationItemSelectedListener false

        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            bindings.appBarMainNavigation.tvToolbarTitle.text = destination.label

            if (destination.id == R.id.homeFragment) {
                bindings.appBarMainNavigation.toolbar.fadeVisibility(View.GONE,0)
            }
        }




    }



    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.nav_host_fragment_content_home_navigation)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }



    fun openAndCloseDrawer() {
        if (bindings.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            bindings.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            bindings.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun shareAppLink() {
        val shareMessage = getString(R.string.share_message)+application.packageName
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage)
        sendIntent.type = "text/plain"
        startActivity(sendIntent)
    }




}