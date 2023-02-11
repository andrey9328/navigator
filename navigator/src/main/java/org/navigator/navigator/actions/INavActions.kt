package org.navigator.navigator.actions

import android.os.Bundle
import org.navigator.navigator.models.NavigationScreen

interface INavActions

/**
 * Open sub screen
 *
 * @param tabId id scope screen
 * @param args bundle for this fragment if bundle is not null clear all chain and recreate root fragment
 * @param isRecreateAll true clear all chain and recreate root fragment, default false
 */
data class NavSelectTab(val tabId: String, val isRecreateAll: Boolean = false, val args: Bundle? = null): INavActions

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


/**
 * Recreates fragments after a tab is selected if you need to recreate not immediately, but after opening the tab
 *
 * @param ids id scope screen need restatrt later
 */
data class NavClearChainTabLater(val ids: List<String>): INavActions


/**
 * Create new sub navigation in input tab. This navigator will exist until the backstack all of the new navigator is cleared
 *
 * @param tabId id of bottom bar item
 * @param newNavigatorId key for new navigator шы гтшй
 * @param screen parent screen for new navigator
 * @param args bundle for new fragment
 */
data class NavCreateSubNavigator(
    val tabId: String,
    val newNavigatorId: String,
    val screen: NavigationScreen,
    val args: Bundle? = null
) : INavActions

/**
 * Remove sub navigation if create by NavCreateSubNavigator clear fragment container and it router
 *
 * @param navigatorId id navigator for delete
 */
data class NavClearSubNavigator(val navigatorId: String) : INavActions