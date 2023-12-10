package org.navigator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import org.navigator.app.R
import org.navigator.main.actions.NavOpenScreen
import org.navigator.main.actions.NavSelectTab
import org.navigator.main.utils.getRouter
import org.navigator.main.models.NavigationScreen


class HistoryFragment: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getRouter(null).setResultListener("result") { result ->
            view.findViewById<TextView>(R.id.tvExample).text = result as String
        }

        view.findViewById<TextView>(R.id.tvExample)?.let {
            if (savedInstanceState?.getString("Text") == null) {
                it.text = "History fragment " + parentFragmentManager.backStackEntryCount.toString()
            } else {
                it.text = savedInstanceState.getString("Text")
            }

        }
        view.findViewById<Button>(R.id.btnOpenProfile).setOnClickListener {
            val inputText = view.findViewById<EditText>(R.id.etArguments).text
            if (inputText.isNullOrEmpty()) {
                getRouter(null).addAction(NavSelectTab(R.id.profileMenu.toString()))
            } else {
                getRouter(null).addAction(
                    NavSelectTab(
                        R.id.profileMenu.toString(), args =
                        bundleOf("Arg" to inputText.toString())
                    )
                )
            }
        }

        view.findViewById<Button>(R.id.btnOpen).setOnClickListener {
            getRouter().addAction(NavOpenScreen(NavigationScreen { HistoryFragment() }))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("Text", view?.findViewById<TextView>(R.id.tvExample)?.text.toString())
    }
}