package org.navigator.app

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import org.navigator.fragments.HistoryFragment
import org.navigator.main.models.NavFragment
import org.navigator.main.actions.NavBack
import org.navigator.main.actions.NavReplaceScreen
import org.navigator.main.utils.createNavigator
import org.navigator.main.utils.getRouter

class SimpleActivity: AppCompatActivity() {
    private val navigator = createNavigator(R.id.mainContainer, supportFragmentManager)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.simple_activity)
        if (savedInstanceState == null) {
            getRouter(ROUTER_NAME).addAction(
                NavReplaceScreen(
                    NavFragment { HistoryFragment() },
                    args = bundleOf("close" to true)
                )
            )
        }
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getRouter(ROUTER_NAME).addAction(NavBack { finish() })
            }
        })
    }

    override fun onResume() {
        super.onResume()
        getRouter(ROUTER_NAME).attachNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        getRouter(ROUTER_NAME).detachNavigator(isFinishing)
    }

     companion object {
         private const val ROUTER_NAME = "SimpleActivity"
     }
}