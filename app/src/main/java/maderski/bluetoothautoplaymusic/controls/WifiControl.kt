package maderski.bluetoothautoplaymusic.controls

import maderski.bluetoothautoplaymusic.helpers.ToastHelper
import maderski.bluetoothautoplaymusic.wrappers.StringResourceWrapper
import maderski.bluetoothautoplaymusic.wrappers.SystemServicesWrapper

/**
 * Created by Jason on 2/26/17.
 */

class WifiControl(
        private val systemServicesWrapper: SystemServicesWrapper,
        private val stringResourceWrapper: StringResourceWrapper,
        private val toastHelper: ToastHelper
) {
    fun wifiON(enable: Boolean) {
        val wifiManager = systemServicesWrapper.wifiManager
        wifiManager.isWifiEnabled = enable
        val toastMessage = if (enable) stringResourceWrapper.wifiTurnedOn else stringResourceWrapper.wifiTurnedOff
        toastHelper.displayMessage(toastMessage)
    }

    fun isWifiON(): Boolean {
        val wifiManager = systemServicesWrapper.wifiManager
        return wifiManager.isWifiEnabled
    }
}
