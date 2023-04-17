package org.navigator.main.models

import androidx.fragment.app.Fragment

data class NavigationScreen(val fragment: () -> Fragment)