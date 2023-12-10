package org.navigator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.navigator.app.R
import org.navigator.main.actions.NavBackToRootScreen
import org.navigator.main.actions.NavCreateSubRouter
import org.navigator.main.actions.NavOpenScreen
import org.navigator.main.utils.getRouter
import org.navigator.main.models.NavigationScreen

class AppsMenuFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_apps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<TextView>(R.id.tvExample)?.let {
            if (savedInstanceState?.getString("Text") == null) {
                it.text = "AppsMenuFragment " + parentFragmentManager.backStackEntryCount.toString()
            } else {
                it.text = savedInstanceState.getString("Text")
            }
        }
        view.findViewById<Button>(R.id.btnBackParent).setOnClickListener {
            getRouter(null).sendResult("result", "111")
            getRouter().addAction(NavBackToRootScreen)
        }

        view.findViewById<Button>(R.id.btnOpen).setOnClickListener {
            getRouter().addAction(NavOpenScreen(NavigationScreen { AppsMenuFragment() }))
        }

        view.findViewById<Button>(R.id.openNewScreenTab).setOnClickListener {
            getRouter(null).addAction(NavCreateSubRouter(R.id.profileMenu.toString(), "profile_new", NavigationScreen { HistoryFragment() }))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("Text", view?.findViewById<TextView>(R.id.tvExample)?.text.toString())
    }
}
