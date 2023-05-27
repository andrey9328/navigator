package org.navigator.main.navigators

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import org.navigator.main.models.NavigationScreen
import org.navigator.main.RouteNavigationContainer
import org.navigator.main.fragments.ScreenContainer
import org.navigator.main.actions.*
import org.navigator.main.container.SubRoutersContainer
import org.navigator.main.routes.INavigationRoute

class MultiScreenNavigator(
    @IdRes val containerId: Int,
    private val fragmentManager: FragmentManager,
    private val actionSelectTab: (String) -> Unit,
    private val createScreen: (String) -> NavigationScreen,
    private val backStackBuilder: (List<String>, String?, String?) -> List<String> = { list, current, _ ->
        val result = ArrayList<String>(list)
        current?.let { result.add(it) }
        result
    }
) : INavigatorMultiInternal {
    private var backStack = arrayListOf<String>()
    private var currentRouter: String? = null
    private val screensForClear = arrayListOf<String>()
    private val subRouters = arrayListOf<SubRoutersContainer>()

    override fun executeAction(action: INavActions, routerTag: String?) {
        fragmentManager.executePendingTransactions()
        when (action) {
            is NavSelectTab -> selectTab(
                action.tabId,
                createScreen.invoke(action.tabId),
                action.isRecreateAll,
                action.args
            )
            is NavBack -> backProcess(action)
            is NavClearChainTabsLater -> rootScreenLater(action.tabIds)
            is NavCreateSubRouter -> {
                subRouters.add(SubRoutersContainer(action.tabId, action.subRouterId))
                selectTab(
                    action.tabId,
                    action.screen,
                    false,
                    action.args
                )
            }
        }
    }

    override fun isRootFragment(): Boolean {
        return fragmentManager.backStackEntryCount == 0
    }

    override fun getSavedData(): HashMap<String, Any?> {
        val saved = hashMapOf<String, Any?>()
        saved[BACK_STACK_KEY] = backStack
        saved[TAB_KEY] = currentRouter
        saved[CLEAR_CHAINS] = screensForClear
        saved[SUB_ROUTERS] = subRouters
        return saved
    }
    @Suppress("UNCHECKED_CAST")
    override fun restoreState(saved: HashMap<String, Any?>) {
        val stack = saved[BACK_STACK_KEY] as? List<String>
        stack?.let {
            backStack.clear()
            backStack.addAll(it)
        }

        val chains = saved[CLEAR_CHAINS] as? List<String>
        chains?.let {
            screensForClear.clear()
            screensForClear.addAll(it)
        }

        val routers = saved[CLEAR_CHAINS] as? List<SubRoutersContainer>
        routers?.let {
            subRouters.clear()
            subRouters.addAll(it)
        }

        val tab = saved[TAB_KEY] as? String
        tab?.let { currentRouter = it }
    }

    override fun getShowFragment(): Fragment? {
        return fragmentManager.fragments
            .find { it.isVisible }
            ?.childFragmentManager
            ?.fragments
            ?.find { it.isVisible }
    }

    private fun selectTab(tabId: String, screen: NavigationScreen, isClearStack: Boolean, args: Bundle?) {
        val routerId = subRouters.find { it.mainRouter == tabId }?.subRouter ?: tabId

        val currentFragment = fragmentManager.fragments.find { it.isVisible }
        val newFragment = fragmentManager.findFragmentByTag(routerId)
        if (newFragment != null && currentFragment != null && newFragment == currentFragment) {
            return
        }

        backStack = ArrayList(backStackBuilder.invoke(backStack, currentRouter, routerId))
        val transaction = fragmentManager.beginTransaction()
        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }

        if (newFragment == null) {
            screensForClear.remove(routerId)
            val container = ScreenContainer()
            container.arguments = bundleOf(ScreenContainer.ROUTER_TAG to routerId)
            transaction.add(containerId, container, routerId)
            getRouter(routerId).addAction(NavReplaceScreen(screen, args = args))
            actionSelectTab.invoke(routerId)

        } else if (args != null || isClearStack || screensForClear.contains(routerId)) {
            screensForClear.remove(routerId)
            transaction.show(newFragment)
            getRouter(routerId).addAction(NavBackToRootScreen)
            getRouter(routerId).addAction(NavReplaceScreen(screen, args = args))
            actionSelectTab.invoke(routerId)
        } else {
            transaction.show(newFragment)
            actionSelectTab.invoke(routerId)
        }
        currentRouter = routerId
        transaction.commitNow()
    }

    private fun getRouter(tag: String?): INavigationRoute {
        return RouteNavigationContainer.getRouteByTag(tag)
    }

    private fun backProcess(action: NavBack) {
        val route = getRouter(currentRouter)

        if (route.getCurrentNavigator()?.isRootFragment() == true) {
            val isRemoveRouter = subRouters.removeLastItem { it.subRouter == currentRouter }
            backStack.removeLastOrNull()?.let { backTab(it, isRemoveRouter) } ?: action.systemAction?.invoke()
            return
        }

        route.addAction(action)
    }

    private fun backTab(screenKey: String, isRemoveRouter: Boolean) {
        val currentFragment = fragmentManager.fragments.find { it.isVisible }

        if (isRemoveRouter) {
            fragmentManager.fragments.find { it.isVisible }?.let {
                fragmentManager.beginTransaction().remove(it).commit()
            }
            RouteNavigationContainer.removeRouter(currentRouter)
        }

        val newFragment = fragmentManager.findFragmentByTag(screenKey)
        if (newFragment == null) {
            selectTab(screenKey, createScreen.invoke(screenKey), false, null)
            return
        }
        val transaction = fragmentManager.beginTransaction()
        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }

        transaction.show(newFragment)
        actionSelectTab.invoke(screenKey)
        currentRouter = screenKey
        transaction.commit()
    }

    private fun rootScreenLater(ids: List<String>) {
        val newIds = ids.toMutableList()
        val currentId = currentRouter
        if (currentId != null && ids.contains(currentId)) {
            getRouter(currentId).addAction(NavBackToRootScreen)
            getRouter(currentId).addAction(NavReplaceScreen(createScreen.invoke(currentId)))
            newIds.remove(currentId)
        }
        screensForClear.clear()
        screensForClear.addAll(newIds)
    }

    private fun <T> ArrayList<T>.removeLastItem(predicate: (T) -> Boolean): Boolean {
        val indexForRemove = this.indexOfLast { predicate.invoke(it) }.takeIf { it >= 0 } ?: return false
        this.removeAt(indexForRemove)
        return true
    }

    companion object {
        private const val BACK_STACK_KEY = "BACK_STACK_KEY"
        private const val TAB_KEY = "TAB_KEY"
        private const val CLEAR_CHAINS = "CLEAR_CHAINS"
        private const val SUB_ROUTERS = "SUB_ROUTERS"
    }
}