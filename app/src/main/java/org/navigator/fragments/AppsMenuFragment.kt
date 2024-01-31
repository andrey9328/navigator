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
import org.navigator.main.actions.NavSelectTab
import org.navigator.main.utils.getRouter
import org.navigator.main.models.NavFragment

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

        }

        view.findViewById<Button>(R.id.btnOpen).setOnClickListener {
            getRouter().addAction(NavOpenScreen(NavFragment { AppsMenuFragment() }))
        }

        view.findViewById<Button>(R.id.openNewScreenTab).setOnClickListener {
            //getRouter(null).addAction(NavCreateSubRouter(R.id.profileMenu.toString(), "profile_new", NavFragment { HistoryFragment() }))
//            getRouter().addAction(
//                NavSelectTab(tabId = R.id.profileMenu.toString()),
//                NavOpenScreen(screen = NavFragment { HistoryFragment() })
//            )

            getRouter().addAction(
                NavCreateSubRouter(tabId = R.id.profileMenu.toString(), "sub", NavFragment { HistoryFragment() })
            )
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("Text", view?.findViewById<TextView>(R.id.tvExample)?.text.toString())
    }
}
