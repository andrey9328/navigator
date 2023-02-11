package org.navigator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.navigator.app.R
import org.navigator.navigator.actions.*
import org.navigator.navigator.models.NavigationScreen
import org.navigator.navigator.getRouter

class SubFragment: Fragment() {

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
                it.text = "SubFragment " + parentFragmentManager.backStackEntryCount.toString()
            } else {
                it.text = savedInstanceState.getString("Text")
            }
        }
        view.findViewById<Button>(R.id.btnBackParent).setOnClickListener {
            getRouter(null).addAction(NavClearSubNavigator("Sub"))
            getRouter(null).addAction(NavSelectTab(R.id.appsMenu.toString()))
        }

        view.findViewById<Button>(R.id.btnOpen).setOnClickListener {
            getRouter().addAction(NavOpenScreen(NavigationScreen { SubFragment() }))
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