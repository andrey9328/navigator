package org.navigator.main.utils

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import org.navigator.main.RouteNavigationContainer
import org.navigator.main.container.SubRoutersContainer
import org.navigator.main.fragments.ScreenContainer
import org.navigator.main.models.NavFragment
import org.navigator.main.navigators.INavigator
import org.navigator.main.navigators.MultiScreenNavigator
import org.navigator.main.navigators.Navigator
import org.navigator.main.routes.INavigationRoute

fun getRouter(tag: String? = null): INavigationRoute {
    return RouteNavigationContainer.getRouteByTag(tag = tag)
}

fun createNavigator(@IdRes containerId: Int, fragmentManager: FragmentManager): INavigator {
    return Navigator(containerId, fragmentManager)
}

fun createMultiStackNavigator(
    @IdRes containerId: Int,
    fragmentManager: FragmentManager,
    actionSelectTab: (String) -> Unit,
    createScreen: (String) -> NavFragment,
    backStackBuilder: (List<String>, String?, String?) -> List<String> = { list, current, _ ->
        val result = ArrayList<String>(list)
        current?.let { result.add(it) }
        result
    }
): INavigator {
    return MultiScreenNavigator(containerId, fragmentManager, actionSelectTab, createScreen, backStackBuilder)
}

internal fun FragmentManager.safeBeginTransaction(): FragmentTransaction {
    return this.beginTransaction().setReorderingAllowed(true)
}

internal fun ScreenContainer.getRouter(): INavigationRoute {
    return RouteNavigationContainer.getRouteByTag(
        tag = this.arguments?.getString(ScreenContainer.ROUTER_TAG)
    )
}

