package org.navigator.main

import org.navigator.main.models.NavigationScreen
import org.navigator.main.routes.INavigationRoute
import org.navigator.main.routes.NavigationRoute

object RouteNavigationContainer {
    private val routes = HashMap<String?, INavigationRoute>()
    val associatesMap = hashMapOf<String, NavigationScreen>()

    fun getRouteByTag(tag: String?): INavigationRoute {
        return routes.getOrPut(tag) { NavigationRoute(tag) }
    }

    fun isContainsRoute(key: String): Boolean {
        return routes.containsKey(key)
    }

    fun addAssociates(data: HashMap<String, NavigationScreen>) {
        associatesMap.putAll(data)
    }

    fun removeRouter(key: String?) {
        routes.remove(key)
    }
}