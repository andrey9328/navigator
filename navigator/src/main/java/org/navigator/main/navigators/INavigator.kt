package org.navigator.main.navigators
import android.os.Bundle
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

    /**
     * Save data before rotation screen
     */
    fun saveBundleState(bundle: Bundle)

    /**
     * Restore data after rotation
     * @param bundle data for restore
     */
    fun restoreBundleState(bundle: Bundle)
}