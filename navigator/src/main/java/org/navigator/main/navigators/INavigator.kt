package org.navigator.main.navigators
import androidx.fragment.app.Fragment

interface INavigator {
    /**
     * Return current fragment is last in backstack
     */
    fun isRootFragment() : Boolean

    /**
     * Return current fragment
     */
    fun getShowFragment(): Fragment?
}