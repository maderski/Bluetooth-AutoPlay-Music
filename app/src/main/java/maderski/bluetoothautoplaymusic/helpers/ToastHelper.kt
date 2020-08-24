package maderski.bluetoothautoplaymusic.helpers

import android.content.Context
import android.widget.Toast

class ToastHelper (private val context: Context) {
    private var toast: Toast? = null

    fun displayMessage(message: String, isLongDisplayLength: Boolean = true) {
        toast?.cancel()
        toast = Toast(context).apply {
            setText(message)
            duration = if (isLongDisplayLength) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
        }
        toast?.show()
        toast = null
    }
}