package org.navigator.main

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import org.navigator.main.fragments.ScreenContainer
import org.navigator.main.models.NavigationScreen
import org.navigator.main.navigators.INavigator
import org.navigator.main.navigators.MultiScreenNavigator
import org.navigator.main.navigators.Navigator
import org.navigator.main.routes.INavigationRoute

fun Fragment.getRouter(): INavigationRoute {
    return RouteNavigationContainer.getRouteByTag(
        tag = this.arguments?.getString(ScreenContainer.ROUTER_TAG)
    )
}

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
    createScreen: (String) -> NavigationScreen,
    backStackBuilder: (List<String>, String?, String?) -> List<String> = { list, current, _ ->
        val result = ArrayList<String>(list)
        current?.let { result.add(it) }
        result
    }
): INavigator {
    return MultiScreenNavigator(containerId, fragmentManager, actionSelectTab, createScreen, backStackBuilder)
}

