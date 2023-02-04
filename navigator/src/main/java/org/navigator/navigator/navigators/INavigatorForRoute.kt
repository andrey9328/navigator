package org.navigator.navigator.navigators
import org.navigator.navigator.actions.INavActions

interface INavigatorForRoute: INavigator {
    /**
     * Add navigation action to stack
     * @param action start action
     * @param routerTag tag of current route
     */
    fun executeAction(action: INavActions, routerTag: String?)

    /**
     * Save data before rotation screen
     */
    fun getSavedData(): HashMap<String, Any?>

    /**
     * Restore data after rotation
     * @param saved data for restore
     */
    fun restoreState(saved: HashMap<String, Any?>)
}