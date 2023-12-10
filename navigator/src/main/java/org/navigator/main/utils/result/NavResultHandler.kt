package org.navigator.main.utils.result

internal class NavResultHandler {
    private val listeners = mutableMapOf<String, NavResultListener>()

    fun setResultListener(key: String, listener: NavResultListener) {
        listeners[key] = listener
    }

    fun sendResult(key: String, data: Any) {
        listeners.remove(key)?.onResult(data)
    }

    fun dispose(key: String) {
        listeners.remove(key)
    }
}