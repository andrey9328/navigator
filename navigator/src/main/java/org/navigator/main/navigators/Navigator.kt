package org.navigator.main.navigators

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import org.navigator.main.models.NavFragment
import org.navigator.main.RouteNavigationContainer.getSafeScreen
import org.navigator.main.fragments.ScreenContainer
import org.navigator.main.actions.*

class Navigator(
    @IdRes val containerId: Int,
    private val fragmentManager: FragmentManager
): INavigatorInternal {

    override fun executeAction(action: INavActions, routerTag: String?) {
        fragmentManager.executePendingTransactions()
        when(action) {
            is NavOpenScreen -> {
                val screen = action.screen ?: action.associateScreenId.getSafeScreen()
                openNewScreen(screen, true, routerTag, false, action.args)
            }
            is NavBack -> { backAction(action) }
            is NavBackToRootScreen -> { fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE) }
            is NavReplaceScreen -> {
                val screen = action.screen ?: action.associateScreenId.getSafeScreen()
                openNewScreen(screen, false, routerTag, true, action.args)
            }
            is NavBackTo -> { fragmentManager.popBackStack(action.screenKey, 0) }
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
        screen: NavFragment,
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
            transaction.replace(containerId, fragment, screen.screenKey ?: fragment::class.java.simpleName)
        } else {
            transaction.add(containerId, fragment, screen.screenKey ?: fragment::class.java.simpleName)
        }

        if (isAddedToBackStack) {
            transaction.addToBackStack(screen.screenKey ?: fragment::class.java.simpleName)
        }
        transaction.commit()
    }
}