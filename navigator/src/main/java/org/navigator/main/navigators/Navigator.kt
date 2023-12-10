package org.navigator.main.navigators

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import org.navigator.NavigationException
import org.navigator.main.models.NavigationScreen
import org.navigator.main.RouteNavigationContainer
import org.navigator.main.fragments.ScreenContainer
import org.navigator.main.actions.*

class Navigator(
    @IdRes val containerId: Int,
    private val fragmentManager: FragmentManager
): INavigatorInternal {

    override fun executeAction(action: INavActions, routerTag: String?) {
        fragmentManager.executePendingTransactions()
        when(action) {
            is NavOpenScreen -> openNewScreen(action.screen, true, routerTag, false, action.args)
            is NavBack -> { backAction(action) }
            is NavBackToRootScreen -> { fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE) }
            is NavReplaceScreen -> { openNewScreen(action.screen, false, routerTag, true, action.args) }
            is NavReplaceScreenById -> {
                val screen = RouteNavigationContainer.getScreen(action.associateId) ?: throw NavigationException("Incorrect associate id")
                openNewScreen(screen, false, routerTag, true, action.args)
            }
            is NavOpenScreenById -> {
                val screen = RouteNavigationContainer.getScreen(action.associateId) ?: throw NavigationException("Incorrect associate id")
                openNewScreen(screen, true, routerTag, true, action.args)
            }
            is NavBackTo -> { fragmentManager.popBackStack(action.fragmentTag, 0) }
        }
    }

    override fun isRootFragment(): Boolean {
        return fragmentManager.backStackEntryCount == 0
    }

    override fun saveBundleState(bundle: Bundle) {
       
    }

    override fun restoreBundleState(bundle: Bundle) {

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