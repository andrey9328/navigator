package org.navigator.main.models

import androidx.fragment.app.Fragment
/**
 * @param screenKey use for added and search screens in fragment backstack if screen key is null use fragment::class.name
 */

data class NavigationScreen(val screenKey: String? = null, val fragment: () -> Fragment)