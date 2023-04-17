package org.navigator.main.routes

import org.navigator.main.actions.INavActions
import org.navigator.main.navigators.INavigator
import org.navigator.main.navigators.INavigatorInternal

interface INavigationRoute {
    /**
     * Add navigator to route recommended call in onResume()
     * @param navigator add navigator to route
     */
    fun attachNavigator(navigator: INavigator)

    /**
     * Add navigator to route recommended call in onPause()
     * @param isRemoveRouter if flag is true this route was remove from stack.
     * Use for multi activity app and activity full destroy
     */
    fun detachNavigator(isRemoveRouter: Boolean = false)

    /**
     * Add navigator to route recommended call in onResume()
     * @param action add action to stack
     */
    fun addAction(action: INavActions)

    /**
     * Return current attach navigator
     */
    fun getCurrentNavigator(): INavigatorInternal?
}