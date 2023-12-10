package org.navigator.main.navigators
import android.os.Bundle
import androidx.fragment.app.Fragment

interface INavigator {
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