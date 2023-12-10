package org.navigator.main.routes

import org.navigator.main.actions.INavActions
import org.navigator.main.navigators.INavigator
import org.navigator.main.navigators.INavigatorInternal
import org.navigator.main.utils.result.NavResultListener

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

    /**
     * Sets data listener with given key
     * After first call listener will be removed.
     */
    fun setResultListener(
        key: String,
        listener: NavResultListener
    )

    /**
     * Sends data to listener with given key.
     */
    fun sendResult(key: String, data: Any)

    /**
     * Remove result subscription. if was not used sendResult method
     */
    fun disposeResultListener(key: String)
}