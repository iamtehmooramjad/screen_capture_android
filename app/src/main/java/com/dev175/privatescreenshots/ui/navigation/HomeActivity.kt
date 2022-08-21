package com.dev175.privatescreenshots.ui.navigation

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.dev175.privatescreenshots.R
import com.dev175.privatescreenshots.databinding.ActivityHomeBinding
import com.dev175.privatescreenshots.ui.base.BaseActivity
import com.dev175.privatescreenshots.utils.fadeVisibility
import com.dev175.privatescreenshots.utils.showShortToast
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>(R.layout.activity_home) {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun initUi() {

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
        navController.addOnDestinationChangedListener { _, destination, _ ->
            bindings.appBarMainNavigation.tvToolbarTitle.text = destination.label

            if (destination.id == R.id.homeFragment
            ) {
                bindings.appBarMainNavigation.toolbar.fadeVisibility(View.GONE,0)
            }


        }
/*
        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.shareAppLink->{
                    shareAppLink()
                }
                R.id.appPrivacyPolicy->{
                    showShortToast("Privacy Policy")
                }
            }
            return@setNavigationItemSelectedListener false
        }*/

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