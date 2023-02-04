package org.navigator.navigator

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import org.navigator.navigator.fragments.ScreenContainer
import org.navigator.navigator.models.NavigationScreen
import org.navigator.navigator.navigators.INavigator
import org.navigator.navigator.navigators.MultiScreenNavigator
import org.navigator.navigator.navigators.Navigator
import org.navigator.navigator.routes.INavigationRoute

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
    backStackBuilder: (List<String>, String?) -> List<String> = { list, tab ->
        val result = ArrayList<String>(list)
        tab?.let { result.add(it) }
        result
    }
): INavigator {
    return MultiScreenNavigator(containerId, fragmentManager, actionSelectTab, createScreen, backStackBuilder)
}