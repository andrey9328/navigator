package org.navigator.navigator.models

import androidx.fragment.app.Fragment

data class NavigationScreen(val fragment: () -> Fragment)