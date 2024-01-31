package org.navigator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import org.navigator.app.R
import org.navigator.main.actions.NavOpenScreen
import org.navigator.main.actions.NavCloseSubRouter
import org.navigator.main.actions.TabBack
import org.navigator.main.utils.getRouter
import org.navigator.main.models.NavFragment


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
            getRouter().addAction(NavCloseSubRouter, TabBack())
        }

        view.findViewById<Button>(R.id.btnOpen).setOnClickListener {
            getRouter().addAction(NavOpenScreen(NavFragment { HistoryFragment() }))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("Text", view?.findViewById<TextView>(R.id.tvExample)?.text.toString())
    }
}