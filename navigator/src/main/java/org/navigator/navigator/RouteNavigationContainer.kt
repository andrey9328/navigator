package org.navigator.navigator

import org.navigator.navigator.models.NavigationScreen
import org.navigator.navigator.routes.INavigationRoute
import org.navigator.navigator.routes.NavigationRoute

object RouteNavigationContainer {
    private val routes = HashMap<String?, INavigationRoute>()
    val associatesMap = hashMapOf<String, NavigationScreen>()

    fun getRouteByTag(tag: String?): INavigationRoute {
        return routes.getOrPut(tag) { NavigationRoute(tag) }
    }

    fun addAssociates(data: HashMap<String, NavigationScreen>) {
        associatesMap.putAll(data)
    }

    fun removeRouter(key: String?) {
        routes.remove(key)
    }
}