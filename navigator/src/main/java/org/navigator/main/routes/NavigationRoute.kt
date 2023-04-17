package org.navigator.main.routes

import org.navigator.NavigationException
import org.navigator.main.RouteNavigationContainer
import org.navigator.main.actions.IMultiNavActions
import org.navigator.main.actions.INavActions
import org.navigator.main.navigators.INavigator
import org.navigator.main.navigators.INavigatorInternal
import org.navigator.main.navigators.MultiScreenNavigator

class NavigationRoute(private val tag: String?): INavigationRoute {
    private var currentNavigator: INavigatorInternal? = null
    private val actions = arrayListOf<INavActions>()
    private val savedMap = hashMapOf<String, Any?>()

    override fun attachNavigator(navigator: INavigator) {
        val castNavigator = navigator as? INavigatorInternal ?: throw NavigationException("Base class for navigator must be INavigatorForRoute")
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

    private fun pendingActions() {
        if (currentNavigator == null) return
        actions.forEach { currentNavigator?.executeAction(it, tag) }
        actions.clear()
    }
}