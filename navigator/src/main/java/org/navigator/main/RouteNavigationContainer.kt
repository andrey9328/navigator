package org.navigator.main

import org.navigator.NavigationException
import org.navigator.main.models.NavFragment
import org.navigator.main.routes.INavigationRoute
import org.navigator.main.routes.NavigationRoute

object RouteNavigationContainer {
    private val routes = hashMapOf<String?, INavigationRoute>()
    private val associatesMap = hashMapOf<String, NavFragment>()

    fun getRouteByTag(tag: String?): INavigationRoute {
        return routes.getOrPut(tag) { NavigationRoute(tag) }
    }

    fun addAssociates(data: HashMap<String, NavFragment>) {
        associatesMap.putAll(data)
    }

    fun getScreen(key: String): NavFragment? {
        return associatesMap[key]
    }

    fun removeRouter(key: String?) {
        routes.remove(key)
    }

    internal fun String?.getSafeScreen(): NavFragment {
        return this?.let { getScreen(it) } ?: throw NavigationException("Incorrect associate id")
    }
}