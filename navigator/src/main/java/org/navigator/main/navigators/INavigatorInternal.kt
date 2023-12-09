package org.navigator.main.navigators
import org.navigator.main.actions.INavActions

interface INavigatorInternal: INavigator {
    /**
     * Add navigation action to stack
     * @param action start action
     * @param routerTag tag of current route
     */
    fun executeAction(action: INavActions, routerTag: String?)
}