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

class Navigator(
    @IdRes val containerId: Int,
    private val fragmentManager: FragmentManager
): INavigatorForRoute {

    override fun executeAction(action: INavActions, routerTag: String?) {
        fragmentManager.executePendingTransactions()
        when(action) {
            is NavOpenScreen -> openNewScreen(action.screen, action.isAddedToBackStack, routerTag, false, action.args)
            is NavBack -> { backAction(action) }
            is NavBackToRootScreen -> { fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE) }
            is NavReplaceScreen -> { openNewScreen(action.screen, action.isAddedToBackStack, routerTag, true, action.args) }
            is NavReplaceScreenById -> {
                val screen = RouteNavigationContainer.associatesMap[action.associateId] ?: throw Exception("Incorrect associate id")
                openNewScreen(screen, action.isAddedToBackStack, routerTag, true, action.args)
            }
            is NavOpenScreenById -> {
                val screen = RouteNavigationContainer.associatesMap[action.associateId] ?: throw Exception("Incorrect associate id")
                openNewScreen(screen, action.isAddedToBackStack, routerTag, true, action.args)
            }
            is NavBackTo -> { fragmentManager.popBackStack(action.fragmentTag, 0) }
        }
    }

    override fun isRootFragment(): Boolean {
        return fragmentManager.backStackEntryCount == 0
    }

    override fun getSavedData(): HashMap<String, Any?> {
        return hashMapOf()
    }

    override fun restoreState(saved: HashMap<String, Any?>) {

    }

    override fun getShowFragment(): Fragment? {
        return fragmentManager.fragments.find { it.isVisible }
    }

    private fun backAction(action: NavBack) {
        if (isRootFragment() && action.systemAction != null) {
            action.systemAction.invoke()
        } else {
            fragmentManager.popBackStack()
        }
    }

    private fun openNewScreen(
        screen: NavigationScreen,
        isAddedToBackStack: Boolean,
        tag: String?,
        isReplace: Boolean,
        bundle: Bundle?
    ) {
        val transaction = fragmentManager.beginTransaction()
        transaction.setReorderingAllowed(true)
        val fragment = screen.fragment.invoke()
        val newBundle = bundleOf(ScreenContainer.ROUTER_TAG to tag)
        bundle?.let { newBundle.putAll(it) }
        fragment.arguments = newBundle

        if (isReplace) {
            transaction.replace(containerId, fragment, fragment::class.java.simpleName)
        } else {
            transaction.add(containerId, fragment, fragment::class.java.simpleName)
        }

        if (isAddedToBackStack) {
            transaction.addToBackStack(fragment::class.java.simpleName)
        }
        transaction.commit()
    }
}