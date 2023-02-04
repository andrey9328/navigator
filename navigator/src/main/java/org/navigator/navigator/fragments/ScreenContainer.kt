package org.navigator.navigator.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.navigator.navigator.R
import org.navigator.navigator.createNavigator
import org.navigator.navigator.getRouter

class ScreenContainer: Fragment() {
    private val navigator by lazy { createNavigator(R.id.containerNavigator, childFragmentManager) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_navigator_container, container, false)
    }

    override fun onResume() {
        super.onResume()
        getRouter().attachNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()
        getRouter().detachNavigator(false)
    }

    companion object {
        const val ROUTER_TAG = "ROUTER_TAG"
    }
}