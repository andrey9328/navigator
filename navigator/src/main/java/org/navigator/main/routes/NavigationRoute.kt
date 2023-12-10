package org.navigator.main.routes

import org.navigator.NavigationException
import org.navigator.main.RouteNavigationContainer
import org.navigator.main.actions.IMultiNavActions
import org.navigator.main.actions.INavActions
import org.navigator.main.navigators.INavigator
import org.navigator.main.navigators.INavigatorInternal
import org.navigator.main.navigators.MultiScreenNavigator
import org.navigator.main.utils.result.NavResultHandler
import org.navigator.main.utils.result.NavResultListener

class NavigationRoute(private val routerTag: String?): INavigationRoute {
    private var currentNavigator: INavigatorInternal? = null
    private val actions = arrayListOf<INavActions>()
    private val navigationResult = NavResultHandler()

    override fun attachNavigator(navigator: INavigator) {
        val castNavigator = navigator as? INavigatorInternal ?: throw NavigationException("Base class for navigator must be INavigatorForRoute")
        currentNavigator = castNavigator
        pendingActions()
    }

    override fun detachNavigator(isRemoveRouter: Boolean) {
        currentNavigator = null
        if (isRemoveRouter) {
            RouteNavigationContainer.removeRouter(routerTag)
        }
    }

    override fun addAction(action: INavActions) {
        if (currentNavigator != null
            && currentNavigator !is MultiScreenNavigator && action is IMultiNavActions
        ) {
            throw NavigationException(
                "Use multi navigation action for default navigator. " +
                        "More likely incorrect navigator tag for getRouter method"
            )
        }
        actions.add(action)
        pendingActions()
    }

    override fun getCurrentNavigator(): INavigatorInternal? {
        return currentNavigator
    }

    override fun setResultListener(key: String, listener: NavResultListener) {
        navigationResult.setResultListener(key, listener)
    }

    override fun sendResult(key: String, data: Any) {
        navigationResult.sendResult(key, data)
    }

    override fun disposeResultListener(key: String) {
        navigationResult.dispose(key)
    }

    private fun pendingActions() {
        if (currentNavigator == null) return
        actions.forEach { currentNavigator?.executeAction(it, routerTag) }
        actions.clear()
    }
}