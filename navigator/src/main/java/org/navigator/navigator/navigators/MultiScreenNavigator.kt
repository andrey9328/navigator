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
import org.navigator.navigator.containers.SubNavigatorContainer
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
    private val screensForClear = arrayListOf<String>()
    private val subNavigators = arrayListOf<SubNavigatorContainer>()

    override fun executeAction(action: INavActions, routerTag: String?) {
        fragmentManager.executePendingTransactions()
        when (action) {
            is NavSelectTab -> selectScreen(
                action.tabId,
                null,
                createScreen.invoke(action.tabId),
                action.isRecreateAll,
                action.args
            )
            is NavBack -> backProcess(action)
            is NavClearChainTabLater -> rootScreenLater(action.ids)
            is NavCreateSubNavigator -> selectScreen(
                action.tabId,
                action.newNavigatorId,
                action.screen,
                false,
                action.args
            )
            is NavClearSubNavigator -> removeSubNavigator(action.navigatorId)
        }
    }

    override fun isRootFragment(): Boolean {
        return fragmentManager.backStackEntryCount == 0
    }

    override fun getSavedData(): HashMap<String, Any?> {
        val saved = hashMapOf<String, Any?>()
        saved[BACK_STACK_KEY] = backStack
        saved[TAB_KEY] = currentTab
        saved[CLEAR_CHAINS] = screensForClear
        saved[SUB_NAVIGATORS] = subNavigators
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

        val navigators = saved[SUB_NAVIGATORS] as? List<SubNavigatorContainer>
        navigators?.let {
            subNavigators.clear()
            subNavigators.addAll(it)
        }

        val tab = saved[TAB_KEY] as? String
        tab?.let { currentTab = it }
    }

    override fun getShowFragment(): Fragment? {
        return fragmentManager.fragments
            .find { it.isVisible }
            ?.childFragmentManager
            ?.fragments
            ?.find { it.isVisible }
    }

    private fun selectScreen(screenKey: String, subNavigatorId: String?, screen: NavigationScreen, isClearStack: Boolean, args: Bundle?) {
        val subScreenKey = if (subNavigatorId != null) {
            subNavigators.add(SubNavigatorContainer(screenKey, subNavigatorId))
            subNavigatorId
        } else {
            subNavigators.find { it.mainNavId == screenKey }?.subNavId ?: screenKey
        }

        val currentFragment = fragmentManager.fragments.find { it.isVisible }
        val newFragment = fragmentManager.findFragmentByTag(subScreenKey)
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
            screensForClear.remove(subScreenKey)
            val container = ScreenContainer()
            container.arguments = bundleOf(ScreenContainer.ROUTER_TAG to subScreenKey)
            transaction.add(containerId, container, subScreenKey)
            getRouter(subScreenKey).addAction(NavReplaceScreen(screen, args = args))
            actionSelectTab.invoke(screenKey)
            currentTab = subScreenKey
        } else if (args != null || isClearStack || screensForClear.contains(subScreenKey)) {
            screensForClear.remove(subScreenKey)
            transaction.show(newFragment)
            getRouter(subScreenKey).addAction(NavBackToRootScreen)
            getRouter(subScreenKey).addAction(NavReplaceScreen(screen, args = args))
            actionSelectTab.invoke(subScreenKey)
            currentTab = subScreenKey
        } else {
            transaction.show(newFragment)
            actionSelectTab.invoke(screenKey)
            currentTab = subScreenKey
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
        val tab = subNavigators.find { it.subNavId == screenKey }?.mainNavId ?: screenKey
        actionSelectTab.invoke(tab)
        currentTab = screenKey
        transaction.commit()
    }

    private fun getRouter(tag: String?): INavigationRoute {
        return RouteNavigationContainer.getRouteByTag(tag)
    }

    private fun backProcess(action: NavBack) {
        val route = RouteNavigationContainer.getRouteByTag(currentTab)

        if (route.getCurrentNavigator()?.isRootFragment() == true) {
            backStack.removeLastOrNull()?.let {
                removeSubNavigator(currentTab)
                backTab(it)
            } ?: action.systemAction?.invoke()
            return
        }

        route.addAction(action)
    }

    private fun removeSubNavigator(idNavigator: String?) {
        val currentId = idNavigator ?: return
        if (subNavigators.any { it.subNavId == currentId }) {
            val currentFragment = fragmentManager.findFragmentByTag(currentId) ?: return
            val transaction = fragmentManager.beginTransaction()
            transaction.remove(currentFragment)
            transaction.commit()
            val index = subNavigators.indexOfFirst { it.subNavId == currentId }
            if (index != -1) {
                subNavigators.removeAt(index)
            }
        }
    }

    private fun rootScreenLater(ids: List<String>) {
        val newIds = ids.toMutableList()
        val currentId = currentTab
        if (currentId != null && ids.contains(currentId)) {
            getRouter(currentId).addAction(NavBackToRootScreen)
            getRouter(currentId).addAction(NavReplaceScreen(createScreen.invoke(currentId)))
            newIds.remove(currentId)
        }
        screensForClear.clear()
        screensForClear.addAll(newIds)
    }

    companion object {
        private const val BACK_STACK_KEY = "BACK_STACK_KEY"
        private const val TAB_KEY = "TAB_KEY"
        private const val CLEAR_CHAINS = "CLEAR_CHAINS"
        private const val SUB_NAVIGATORS = "SUB_NAVIGATORS"
    }
}