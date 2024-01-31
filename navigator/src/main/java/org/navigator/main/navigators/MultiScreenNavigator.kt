package org.navigator.main.navigators

import android.os.Build
import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import org.navigator.main.models.NavFragment
import org.navigator.main.RouteNavigationContainer
import org.navigator.main.RouteNavigationContainer.getSafeScreen
import org.navigator.main.fragments.ScreenContainer
import org.navigator.main.actions.*
import org.navigator.main.container.SubRoutersContainer
import org.navigator.main.routes.INavigationRoute
import org.navigator.main.utils.safeBeginTransaction

class MultiScreenNavigator(
    @IdRes val containerId: Int,
    private val fragmentManager: FragmentManager,
    private val actionSelectTab: (String) -> Unit,
    private val createScreen: (String) -> NavFragment,
    private val backStackBuilder: (List<String>, String?, String?) -> List<String> = { list, current, _ ->
        val result = ArrayList<String>(list)
        current?.let { result.add(it) }
        result
    }
) : INavigatorMultiInternal {
    private var backStack = arrayListOf<String>()
    private var currentRouter: String? = null
    private val subRouters = arrayListOf<SubRoutersContainer>()

    override fun executeActionPull(actionPull: ActionPull, routerTag: String?) {
        for (item in actionPull.actions) {
            when(item) {
                is IMultiNavActions -> executeAction(item)
                else -> getCurrentRouter(currentRouter).addAction(item)
            }
        }
    }

    override fun isRootFragment(): Boolean {
        return fragmentManager.backStackEntryCount == 0
    }

    override fun saveBundleState(bundle: Bundle) {
        bundle.putStringArrayList(BACK_STACK_KEY, backStack)
        bundle.putString(TAB_KEY, currentRouter)
        bundle.putParcelableArrayList(SUB_ROUTERS, subRouters)
    }

    @Suppress("DEPRECATION")
    override fun restoreBundleState(bundle: Bundle) {
        bundle.getStringArrayList(BACK_STACK_KEY)?.let {
            backStack.clear()
            backStack.addAll(it)
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

    private fun executeAction(action: INavActions) {
        when (action) {
            is NavSelectTab -> selectTab(
                action.tabId,
                createScreen.invoke(action.tabId),
                action.isRecreateAll,
                action.args
            )
            is NavBack -> backProcess(action)
            is TabBack -> backWithSubRouter(action)
            is NavCreateSubRouter -> {
                subRouters.add(SubRoutersContainer(action.tabId, action.subRouterId))
                val screen = action.screen ?: action.associateScreenId.getSafeScreen()
                selectTab(
                    action.tabId,
                    screen,
                    false,
                    action.args
                )
            }
            is NavCloseSubRouter -> { removeSubRouterAction() }
        }
        fragmentManager.executePendingTransactions()
    }

    private fun removeSubRouterAction() {
        if (subRouters.isEmpty()) {
            return
        }

        val removeIndex = subRouters.indexOfLast { it.subRouter == currentRouter }.takeIf { it >= 0 } ?: return
        val removeItem = subRouters.removeAt(removeIndex)
        removeRouter(removeItem.subRouter, fragmentManager.findFragmentByTag(removeItem.subRouter))
        executeAction(NavSelectTab(removeItem.mainRouter, false))
        backStack.removeLastItem { it == removeItem.subRouter }
    }

    private fun selectTab(tabId: String, screen: NavFragment, isClearStack: Boolean, args: Bundle?) {
        val routerId = subRouters.find { it.mainRouter == tabId }?.subRouter ?: tabId

        val currentFragment = fragmentManager.fragments.find { it.isVisible }
        val newFragment = fragmentManager.findFragmentByTag(routerId)
        if (newFragment != null && currentFragment != null && newFragment == currentFragment) {
            return
        }

        backStack = ArrayList(backStackBuilder.invoke(backStack, currentRouter, routerId))
        val transaction = fragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)

        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }

        if (newFragment == null) {
            val container = ScreenContainer()
            container.arguments = bundleOf(ScreenContainer.ROUTER_TAG to routerId)
            transaction.add(containerId, container, routerId)
            getCurrentRouter(routerId).addAction(NavReplaceScreen(screen, args = args))
        } else if (args != null || isClearStack) {
            transaction.show(newFragment)
            getCurrentRouter(routerId).addAction(NavBackToRootScreen)
            getCurrentRouter(routerId).addAction(NavReplaceScreen(screen, args = args))
        } else {
            transaction.show(newFragment)
        }

        if (currentRouter != routerId) {
            actionSelectTab.invoke(routerId)
        }

        currentRouter = routerId
        transaction.commit()
    }

    private fun getCurrentRouter(tag: String?): INavigationRoute {
        return RouteNavigationContainer.getRouteByTag(tag)
    }

    private fun backProcess(action: NavBack) {
        val route = getCurrentRouter(currentRouter)

        if (route.getCurrentNavigator()?.isRootFragment() == true) {
            backWithSubRouter(action)
            return
        }

        route.addAction(action)
    }

    private fun backWithSubRouter(action: INavActions) {
        val systemAction = when(action) {
            is NavBack -> action.systemAction
            is TabBack -> action.systemAction
            else -> return
        }
        val isRemoveRouter = subRouters.removeLastItem { it.subRouter == currentRouter }
        backStack.removeLastOrNull()?.let { backTab(it, isRemoveRouter) } ?: systemAction?.invoke()
    }

    private fun backTab(screenKey: String, isRemoveRouter: Boolean) {
        val currentFragment = fragmentManager.fragments.find { it.isVisible }

        if (isRemoveRouter) {
            removeRouter(currentRouter, fragmentManager.fragments.find { it.isVisible })
        }

        val newFragment = fragmentManager.findFragmentByTag(screenKey)
        if (newFragment == null) {
            executeAction(NavSelectTab(screenKey))
            return
        }
        val transaction = fragmentManager.safeBeginTransaction()

        if (currentFragment != null) {
            transaction.hide(currentFragment)
        }

        transaction.show(newFragment)
        if (currentRouter != screenKey) {
            actionSelectTab.invoke(screenKey)
        }

        currentRouter = screenKey
        transaction.commit()
    }

    private fun removeRouter(tabId: String?, container: Fragment?) {
        RouteNavigationContainer.removeRouter(tabId)
        if (container != null) {
            fragmentManager
                .safeBeginTransaction()
                .remove(container)
                .commit()
            fragmentManager.executePendingTransactions()
        }
    }

    private fun <T> ArrayList<T>.removeLastItem(predicate: (T) -> Boolean): Boolean {
        val indexForRemove = this.indexOfLast { predicate.invoke(it) }.takeIf { it >= 0 } ?: return false
        this.removeAt(indexForRemove)
        return true
    }

    companion object {
        private const val BACK_STACK_KEY = "BACK_STACK_KEY"
        private const val TAB_KEY = "TAB_KEY"
        private const val SUB_ROUTERS = "SUB_ROUTERS"
    }
}