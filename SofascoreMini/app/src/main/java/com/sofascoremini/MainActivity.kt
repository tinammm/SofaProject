package com.sofascoremini


import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.sofascoremini.databinding.ActivityMainBinding
import com.sofascoremini.ui.settings.THEME
import com.sofascoremini.util.getColorFromAttribute
import com.sofascoremini.util.loadTournamentImage
import com.sofascoremini.util.setUpAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private val preferences by lazy { PreferenceManager.getDefaultSharedPreferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        setUpAppTheme(preferences.getString(THEME, "light") ?: "light")

        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController

        setSupportActionBar(binding.toolbar)

        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.settingsFragment -> {
                    setUpAppBar(logoVisibility = false, hasNavIcon = true)
                }

                R.id.tournamentDetailsFragment -> {
                    setUpAppBar(
                        logoVisibility = false, hasNavIcon = true
                    )
                }

                R.id.eventDetailsFragment -> {
                    setUpAppBar(
                        logoVisibility = false,
                        hasNavIcon = true,
                        navTint = R.attr.n_lv_1,
                        backgroundColor = R.attr.surface1,
                    )
                }

                R.id.mainListFragment -> {
                    setUpAppBar(
                        logoVisibility = true,
                        hasNavIcon = false,
                        backgroundColor = R.attr.colorPrimary
                    )
                }
            }
        }
    }

    fun setUpAppBar(
        logoVisibility: Boolean,
        hasNavIcon: Boolean,
        navIcon: Int = R.drawable.ic_arrow_back,
        navTint: Int = R.attr.surface1,
        backgroundColor: Int = R.attr.colorPrimary,
        hasEventLabel: Boolean = false,
        label: String = "",
        labelImageId: Int = 0,
        navigateFunction: () -> Unit = {}
    ) {

        binding.apply {
            appLogo.isVisible = logoVisibility
            eventDetailsLabel.visibility = View.GONE
            toolbar.apply {
                if (hasNavIcon) {
                    setNavigationIcon(navIcon)
                    setNavigationIconTint(getColorFromAttribute(root.context, navTint))
                }
                setBackgroundColor(getColorFromAttribute(root.context, backgroundColor))
            }
            if (hasEventLabel) {
                eventDetailsLabel.visibility = View.VISIBLE
                tournamentInfo.text = label
                tournamentLogo.loadTournamentImage(labelImageId)

                eventDetailsLabel.setOnClickListener {
                    navigateFunction()
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.top_bar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_settings -> {
                navController.navigate(R.id.settingsFragment)
                binding.toolbar.setNavigationIcon(R.drawable.ic_arrow_back)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}

