package org.navigator.navigator.routes

import org.navigator.navigator.RouteNavigationContainer
import org.navigator.navigator.actions.INavActions
import org.navigator.navigator.navigators.INavigator
import org.navigator.navigator.navigators.INavigatorForRoute

class NavigationRoute(private val tag: String?): INavigationRoute {
    private var currentNavigator: INavigatorForRoute? = null
    private val actions = arrayListOf<INavActions>()
    private val savedMap = hashMapOf<String, Any?>()

    override fun attachNavigator(navigator: INavigator) {
        val castNavigator = navigator as? INavigatorForRoute ?: throw Exception("Base class for navigator must be INavigatorForRoute")
        currentNavigator = castNavigator
        currentNavigator?.restoreState(savedMap)
        pendingActions()
    }

    override fun detachNavigator(isRemoveRouter: Boolean) {
        currentNavigator?.let { savedMap.putAll(it.getSavedData()) }
        currentNavigator = null
        if (isRemoveRouter) {
            RouteNavigationContainer.removeRouter(tag)
        }
    }

    override fun addAction(action: INavActions) {
        actions.add(action)
        pendingActions()
    }

    override fun getCurrentNavigator(): INavigatorForRoute? {
        return currentNavigator
    }

    private fun pendingActions() {
        if (currentNavigator == null) return
        actions.forEach { currentNavigator?.executeAction(it, tag) }
        actions.clear()
    }
}