<a href="https://android-arsenal.com/api?level=21"><img src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat" alt="API" /></a>
# RNavigation
RNavigation(Ready navigation) is a lightweight library for building navigation in android applications. Designed for single activity architecture.
Library ready for multi module arhiticture. MVP/MVVM/MVI and also their variations fit well with the library.
<table>
    <tr>
        <td>
            <img src="https://github.com/andrey9328/navigator/blob/master/media/Demonstartion.gif" width="256"/>
        </td>
    </tr>
</table>

## Main advantages
+ Very lightweight (Aproximately ~400 lines)
+ Safe lifecycle navigation transactions and save states
+ Simple for use
+ Work with multi navigation with save state
+ Does not require a context to call a transaction
+ Easy extension for specific tasks

# Use single navigation without multi stack logic
Create navigator in activity
```kotlin
private val navigator = createNavigator(R.id.mainContainer, supportFragmentManager)
```
Where:
+ _R.id.mainContainer_ is your container for show fragment

After this need set lifecycle state for navigator
```kotlin
override fun onResume() {
    super.onResume()
    getRouter(ROUTER_NAME).attachNavigator(navigator)
}
    
override fun onPause() {
    super.onPause()
    getRouter(ROUTER_NAME).detachNavigator(isFinishing)
}
```
Where:
+ _ROUTER_NAME_ any string value or null. Empty for default null
+ _isFinishing_ is default activity method <a href="https://developer.android.com/reference/android/app/Activity#isFinishing()"/>https://developer.android.com/reference/android/app/Activity#isFinishing()</a><br>

You can add back event handling
```kotlin
 onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
     override fun handleOnBackPressed() {
     getRouter(ROUTER_NAME).addAction(NavBack { finish() })
  }
})
```

To create navigation, you need to contact the router through the extension and call the appropriate action
```kotlin
 getRouter(ROUTER_NAME).addAction(
    NavReplaceScreen(
       NavigationScreen { HistoryFragment() },
             args = bundleOf("close" to true)
          )
     )
})
```

# Use multi navigation logic
Create multi stack navigator
```kotlin
  private val navigator = createMultiStackNavigator(R.id.mainContainer, supportFragmentManager,
        actionSelectTab = { findViewById<BottomNavigationView>(R.id.bottomView).menu.findItem(it.toInt()).isChecked = true },
        createScreen = {
            when(it) {
                R.id.appsMenu.toString() -> NavigationScreen { AppsMenuFragment() }
                R.id.historyMenu .toString() -> NavigationScreen { HistoryFragment() }
                else -> NavigationScreen { ProfileFragment() }
            }
        }
    )
```
+ _actionSelectTab_ call when navigator is switch. For example use for select bottom tab
+ _createScreen_ binds the tab id to the parent fragment
+ _backStackBuilder_ use for override logic of backstack queue

After this need set lifecycle state for navigator
```kotlin
override fun onResume() {
    super.onResume()
    getRouter().attachNavigator(navigator)
}
    
override fun onPause() {
    super.onPause()
    getRouter().detachNavigator(isFinishing)
}
```

Add save state logic
```kotlin
override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    navigator.saveBundleState(outState)
}

override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    navigator.restoreBundleState(savedInstanceState)
}
```

You can add back event handling
```kotlin
 onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
     override fun handleOnBackPressed() {
     getRouter().addAction(NavBack { finish() })
  }
})
```

Event _NavSelectTab_ need to be called to open a tab
```kotlin
 getRouter().addAction(NavSelectTab(R.id.appsMenu.toString()))
```

Examble with android bottombar
```kotlin
  bottom.setOnItemSelectedListener {
     when (it.itemId) {
         R.id.appsMenu -> getRouter().addAction(NavSelectTab(it.itemId.toString()))
         R.id.historyMenu -> getRouter().addAction(NavSelectTab(it.itemId.toString()))
         R.id.profileMenu -> getRouter().addAction(NavSelectTab(it.itemId.toString()))
      }
      true
     }
   if (savedInstanceState == null) {
      findViewById<BottomNavigationView>(R.id.bottomView).selectedItemId = R.id.appsMenu
  }
```


# Work with parameters with result listeners. 
It is important to send and receive data within the same router!!!

Add subscription for listen result
```kotlin
getRouter().setResultListener("result") { data ->
    val str = data as String
    //use data here
}
getRouter().addAction(NavSelectTab("tab_id2"))
```

Send data to result listener
```kotlin
getRouter().sendResult("result", "data str")
getRouter().addAction(NavSelectTab("tab_id1"))
```

All action find <a href="https://github.com/andrey9328/navigator/blob/master/navigator/src/main/java/org/navigator/main/actions/INavActions.kt"/>this</a><br>
