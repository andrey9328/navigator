package org.navigator.navigator.navigators

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import org.navigator.navigator.models.NavigationScreen
import org.navigator.navigator.RouteNavigationContainer
import org.navigator.navigator.fragments.ScreenContainer
import org.navigator.navigator.actions.*
import org.navigator.navigator.routes.INavigationRoute

class MultiScreenNavigator(
    @IdRes val containerId: Int,
    private val fragmentManager: FragmentManager,
    private val actionSelectTab: (String) -> Unit,
    private val createScreen: (String) -> NavigationScreen,
    private val backStackBuilder: (List<String>, String?) -> List<String> = { list, tab ->
        val result = ArrayList<String>(list)
        tab?.let { result.add(it) }
        result
    }
) : INavigatorForRoute {
    private val backStack = arrayListOf<String>()
    private var currentTab: String? = null

    override fun executeAction(action: INavActions, routerTag: String?) {
        fragmentManager.executePendingTransactions()
        when (action) {
            is NavSelectTab -> selectScreen(action.tabId, createScreen.invoke(action.tabId), action.args)
            is NavBack -> backProcess(action)
        }
    }

    override fun isRootFragment(): Boolean {
        return fragmentManager.backStackEntryCount == 0
    }

    override fun getSavedData(): HashMap<String, Any?> {
        val saved = hashMapOf<String, Any?>()
        saved[BACK_STACK_KEY] = backStack
        saved[TAB_KEY] = currentTab
        return saved
    }

    @Suppress("UNCHECKED_CAST")
    override fun restoreState(saved: HashMap<String, Any?>) {
        val stack = saved[BACK_STACK_KEY] as? List<String>
        stack?.let {
            backStack.clear()
            backStack.addAll(it)
        }
        val tab = saved[TAB_KEY] as? String
        tab?.let { currentTab = it }
    }

    override fun getShowFragment(): Fragment? {
        return fragmentManager.fragments.find { it.isVisible }
    }

    private fun selectScreen(screenKey: String, screen: NavigationScreen, args: Bundle?) {
        val currentFragment = fragmentManager.fragments.find { it.isVisible }
        val newFragment = fragmentManager.findFragmentByTag(screenKey)
        if (newFragment != null && currentFragment != null && newFragment == currentFragment) {
            return
        }
        val newBackStack = backStackBuilder.invoke(backStack, currentTab)
        backStack.apply {
            clear()
            backStack.addAll(newBackStack)
        }
        val transaction = fragmentManager.beginTransaction()
        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }

        if (newFragment == null) {
            val container = ScreenContainer()
            container.arguments = bundleOf(ScreenContainer.ROUTER_TAG to screenKey)
            transaction.add(containerId, container, screenKey)
            getRouter(screenKey).addAction(NavOpenScreen(screen, isAddedToBackStack = false, args = args))
            actionSelectTab.invoke(screenKey)
            currentTab = screenKey
        } else if (args != null) {
            transaction.show(newFragment)
            getRouter(screenKey).addAction(NavBackToRootScreen)
            getRouter(screenKey).addAction(NavReplaceScreen(screen, isAddedToBackStack = false, args = args))
            actionSelectTab.invoke(screenKey)
            currentTab = screenKey
        } else {
            transaction.show(newFragment)
            actionSelectTab.invoke(screenKey)
            currentTab = screenKey
        }
        transaction.commitNow()
    }

    private fun backTab(screenKey: String) {
        val currentFragment = fragmentManager.fragments.find { it.isVisible }
        val newFragment = fragmentManager.findFragmentByTag(screenKey) ?: return
        val transaction = fragmentManager.beginTransaction()
        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }

        transaction.show(newFragment)
        actionSelectTab.invoke(screenKey)
        currentTab = screenKey
        transaction.commit()
    }

    private fun getRouter(tag: String?): INavigationRoute {
        return RouteNavigationContainer.getRouteByTag(tag)
    }

    private fun backProcess(action: NavBack) {
        val route = RouteNavigationContainer.getRouteByTag(currentTab)

        if (route.getCurrentNavigator()?.isRootFragment() == true) {
            backStack.removeLastOrNull()?.let { backTab(it) } ?: action.systemAction?.invoke()
            return
        }

        route.addAction(action)
    }

    companion object {
        private const val BACK_STACK_KEY = "BACK_STACK_KEY"
        private const val TAB_KEY = "TAB_KEY"
    }
}