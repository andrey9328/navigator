package org.navigator.main.navigators
import org.navigator.main.actions.ActionPull

interface INavigatorInternal: INavigator {
    /**
     * Add navigation action to stack
     * @param actionPull start pull action
     * @param routerTag tag of current route
     */
    fun executeActionPull(actionPull: ActionPull, routerTag: String?)

    /**
     * Return current fragment is last in backstack
     */
    fun isRootFragment() : Boolean
}