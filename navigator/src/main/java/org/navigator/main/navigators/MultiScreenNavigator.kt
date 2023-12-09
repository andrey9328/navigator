package org.navigator.main.navigators

import android.os.Build
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
            is NavRemoveSubRouter -> { removeSubRouterAction(action.tabId, action.subRouterId) }
        }
    }

    override fun isRootFragment(): Boolean {
        return fragmentManager.backStackEntryCount == 0
    }

    override fun getShowFragment(): Fragment? {
        return fragmentManager.fragments
            .find { it.isVisible }
            ?.childFragmentManager
            ?.fragments
            ?.find { it.isVisible }
    }

    override fun saveBundleState(bundle: Bundle) {
        bundle.putStringArrayList(BACK_STACK_KEY, backStack)
        bundle.putString(TAB_KEY, currentRouter)
        bundle.putStringArrayList(CLEAR_CHAINS, screensForClear)
        bundle.putParcelableArrayList(SUB_ROUTERS, subRouters)
    }

    override fun restoreBundleState(bundle: Bundle) {
        bundle.getStringArrayList(BACK_STACK_KEY)?.let {
            backStack.clear()
            backStack.addAll(it)
        }

        bundle.getStringArrayList(CLEAR_CHAINS)?.let {
            screensForClear.clear()
            screensForClear.addAll(it)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelableArrayList("SUB_ROUTERS", SubRoutersContainer::class.java)
        } else {
            bundle.getParcelableArrayList("SUB_ROUTERS")
        }?.let {
            subRouters.clear()
            subRouters.addAll(it)
        }

        bundle.getString(TAB_KEY)?.let {
            currentRouter = it
        }
    }

    private fun removeSubRouterAction(tabId: String, subRouterId: String) {
        for (i in 0..< subRouters.size) {
            val currentRouter = subRouters[i]
            if (currentRouter.mainRouter == tabId && currentRouter.subRouter == subRouterId) {
                subRouters.removeAt(i)
                removeRouter(currentRouter.subRouter, fragmentManager.findFragmentByTag(currentRouter.subRouter))
                return
            }
        }
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
            removeRouter(currentRouter, fragmentManager.fragments.find { it.isVisible })
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

    private fun removeRouter(tabId: String?, container: Fragment?) {
        RouteNavigationContainer.removeRouter(tabId)
        if (container != null) {
            fragmentManager.beginTransaction().remove(container).commit()
        }
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