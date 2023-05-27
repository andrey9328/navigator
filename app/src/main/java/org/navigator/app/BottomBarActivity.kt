package org.navigator.app

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.navigator.fragments.AppsMenuFragment
import org.navigator.fragments.HistoryFragment
import org.navigator.fragments.ProfileFragment
import org.navigator.main.models.NavigationScreen
import org.navigator.main.actions.NavBack
import org.navigator.main.actions.NavSelectTab
import org.navigator.main.createMultiStackNavigator
import org.navigator.main.getRouter

class BottomBarActivity: AppCompatActivity() {
    private val navigator = createMultiStackNavigator(R.id.mainContainer, supportFragmentManager,
        actionSelectTab = {
            try {
                findViewById<BottomNavigationView>(R.id.bottomView).menu.findItem(it.toInt()).isChecked = true
            } catch (e: Exception) {
                findViewById<BottomNavigationView>(R.id.bottomView).menu.findItem(R.id.profileMenu).isChecked = true
            }

                          },
        createScreen = {
            when(it) {
                R.id.appsMenu.toString() -> NavigationScreen { AppsMenuFragment() }
                R.id.historyMenu .toString() -> NavigationScreen { HistoryFragment() }
                else -> NavigationScreen { ProfileFragment() }
            }
        },
        backStackBuilder = { back, current, new ->
            if (current == null || current == "profile_new") return@createMultiStackNavigator back
            val result = ArrayList(back)
            current.let { result.add(it) }
            //if (new == "profile_new") { result.add(R.id.profileMenu.toString()) }
            result
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_activity_main)
        val bottom = findViewById<BottomNavigationView>(R.id.bottomView)
        bottom.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.appsMenu -> getRouter().addAction(NavSelectTab(it.itemId.toString()))
                R.id.historyMenu -> getRouter().addAction(NavSelectTab(it.itemId.toString()))
                R.id.profileMenu -> getRouter().addAction(NavSelectTab(it.itemId.toString()))
            }
            true
        }
        if (savedInstanceState == null) {
            findViewById<BottomNavigationView>(R.id.bottomView).selectedItemId = R.id.appsMenu
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getRouter().addAction(NavBack { finish() })
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getRouter().attachNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        getRouter().detachNavigator(isFinishing)
    }
}