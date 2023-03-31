package org.navigator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import org.navigator.app.R
import org.navigator.navigator.models.NavigationScreen
import org.navigator.navigator.actions.NavCreateSubNavigator
import org.navigator.navigator.getRouter

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
        view.findViewById<Button>(R.id.btnOpen)?.setOnClickListener {
            getRouter().addAction(NavCreateSubNavigator(R.id.historyMenu.toString(), "1", NavigationScreen { LoginFragment() }, isStayCurrentTab = false))
        }
    }
}