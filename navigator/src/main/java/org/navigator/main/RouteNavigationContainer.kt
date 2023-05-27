package org.navigator.main

import org.navigator.main.actions.NavBackToRootScreen
import org.navigator.main.models.NavigationScreen
import org.navigator.main.routes.INavigationRoute
import org.navigator.main.routes.NavigationRoute

object RouteNavigationContainer {
    private val routes = hashMapOf<String?, INavigationRoute>()
    private val associatesMap = hashMapOf<String, NavigationScreen>()

    fun getRouteByTag(tag: String?): INavigationRoute {
        return routes.getOrPut(tag) { NavigationRoute(tag) }
    }

    fun addAssociates(data: HashMap<String, NavigationScreen>) {
        associatesMap.putAll(data)
    }

    fun getScreen(key: String): NavigationScreen? {
        return associatesMap[key]
    }

    fun removeRouter(key: String?) {
        routes.remove(key)
    }
}