package org.navigator.navigator.actions

import android.os.Bundle
import org.navigator.navigator.models.NavigationScreen

interface INavActions

/**
 * Open sub screen
 *
 * @param tabId id scope screen
 * @param args bundle for this fragment if bundle is not null clear all chain and recreate root fragment
 */
data class NavSelectTab(val tabId: String, val args: Bundle? = null): INavActions

/**
 * Open new screen in chain
 * @param args bundle for new fragment
 */
data class NavOpenScreen(val screen: NavigationScreen, val args: Bundle? = null): INavActions

/**
 * Replace screen in chain
 *
 * @param screen screen fragment for open
 * @param args bundle for new fragment
 */
data class NavReplaceScreen(val screen: NavigationScreen, val args: Bundle? = null): INavActions

/**
 * Open new screen in chain
 *
 * @param associateId find fragment in associates array by key, exception if key not found
 * @param args bundle for new fragment
 */
data class NavOpenScreenById(val associateId: String, val args: Bundle? = null): INavActions

/**
 * Replace screen in chain
 *
 * @param associateId find fragment in associates array by key, exception if key not found
 * @param args bundle for new fragment
 */
data class NavReplaceScreenById(val associateId: String, val args: Bundle? = null): INavActions

/**
 * Clear current backstack
 */
object NavBackToRootScreen: INavActions

/**
 * Back to previous fragment
 *
 * @param systemAction add action from system (Example: Activity.finish())
 */
data class NavBack(val systemAction: (() -> Unit)? = null): INavActions

/**
 * Back to fragment by name
 *
 * @param fragmentTag name fragment to back (Example: use fragment class Fragment::class.simpleName)
 */
data class NavBackTo(val fragmentTag: String?): INavActions