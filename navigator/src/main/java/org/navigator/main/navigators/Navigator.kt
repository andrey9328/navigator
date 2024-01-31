package org.navigator.main.navigators

import android.os.Bundle
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import org.navigator.main.models.NavFragment
import org.navigator.main.RouteNavigationContainer.getSafeScreen
import org.navigator.main.actions.*
import org.navigator.main.utils.safeBeginTransaction

class Navigator(
    @IdRes val containerId: Int,
    private val fragmentManager: FragmentManager
): INavigatorInternal {

    override fun executeActionPull(actionPull: ActionPull, routerTag: String?) {
        actionPull.actions.forEach { executeAction(it) }
        fragmentManager.executePendingTransactions()
    }

    override fun isRootFragment(): Boolean {
        return fragmentManager.backStackEntryCount == 0
    }

    override fun saveBundleState(bundle: Bundle) {
       
    }

    override fun restoreBundleState(bundle: Bundle) {

    }

    private fun executeAction(action: INavActions) {
        when(action) {
            is NavOpenScreen -> {
                val screen = action.screen ?: action.associateScreenId.getSafeScreen()
                openNewScreen(screen, isAddedToBackStack = true, isReplace = false)
            }
            is NavBack -> { backAction(action) }
            is NavBackToRootScreen -> { fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE) }
            is NavReplaceScreen -> {
                val screen = action.screen ?: action.associateScreenId.getSafeScreen()
                openNewScreen(screen, isAddedToBackStack = false, isReplace = true)
            }
            is NavBackTo -> { fragmentManager.popBackStack(action.screenKey, 0) }
        }
        fragmentManager.executePendingTransactions()
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
        isReplace: Boolean
    ) {
        val transaction = fragmentManager.safeBeginTransaction()
        val fragment = screen.fragment.invoke()

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