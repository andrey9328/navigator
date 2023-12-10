package org.navigator.main.models

import androidx.fragment.app.Fragment

data class NavigationScreen(val screenKey: String? = null, val fragment: () -> Fragment)