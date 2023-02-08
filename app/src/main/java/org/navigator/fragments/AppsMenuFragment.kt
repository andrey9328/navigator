package org.navigator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.navigator.app.R
import org.navigator.navigator.models.NavigationScreen
import org.navigator.navigator.actions.NavBackToRootScreen
import org.navigator.navigator.actions.NavOpenScreen
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
        view.findViewById<TextView>(R.id.tvExample)?.let {
            if (savedInstanceState?.getString("Text") == null) {
                it.text = "AppsMenuFragment " + parentFragmentManager.backStackEntryCount.toString()
            } else {
                it.text = savedInstanceState.getString("Text")
            }
        }
        view.findViewById<Button>(R.id.btnBackParent).setOnClickListener {
            getRouter().addAction(NavBackToRootScreen)
        }

        view.findViewById<Button>(R.id.btnOpen).setOnClickListener {
            if (parentFragmentManager.backStackEntryCount > 3) {
                getRouter().addAction(NavOpenScreen(NavigationScreen { AppsMenuFragment() }))
                return@setOnClickListener
            }
            getRouter().addAction(NavOpenScreen(NavigationScreen { AppsMenuFragment() }))
        }

        view.findViewById<Button>(R.id.closeActivity).visibility =
            if(arguments?.containsKey("close") == true) View.VISIBLE else View.GONE

        view.findViewById<Button>(R.id.closeActivity)?.setOnClickListener {
            requireActivity().finish()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("Text", view?.findViewById<TextView>(R.id.tvExample)?.text.toString())
    }
}