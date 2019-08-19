package maderski.bluetoothautoplaymusic.analytics

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import maderski.bluetoothautoplaymusic.analytics.constants.SelectionConstants
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 1/28/17.
 */

class FirebaseHelper(context: Context): KoinComponent {
    private val preferences: BAPMPreferences by inject()

    private val firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)
    private val isFirebaseEnabled = preferences.getUseFirebaseAnalytics()

    fun showAnalyticsCollectionsConsent(activity: Activity) {
        val dialogBuilder = AlertDialog.Builder(activity)
                .setTitle("Firebase Analytics")
                .setMessage("Allow use of analytics?")
                .setPositiveButton("Yes") { _, _ ->
                    setAnalyticsCollectionEnabled(true)
                }
                .setNegativeButton("No") { _, _ ->
                    setAnalyticsCollectionEnabled(false)
                }
        val dialog = dialogBuilder.create()
        dialog.show()
    }

    private fun setAnalyticsCollectionEnabled(isEnabled: Boolean) {
        preferences.setUseFirebaseAnalytics(isEnabled)
        firebaseAnalytics.setAnalyticsCollectionEnabled(isEnabled)
    }

    fun featureEnabled(featureName: String, isEnabled: Boolean) {
        val bundle = Bundle()
        bundle.putInt(FirebaseAnalytics.Param.VALUE, if (isEnabled) 1 else 0)
        logEvent(featureName, bundle)
    }

    fun selectionMade(selection: String) = logEvent(selection, null)

    fun timeSetSelected(@SelectionConstants.Selection selection: String, wasSet: Boolean) {
        val bundle = Bundle()
        bundle.putInt(FirebaseAnalytics.Param.VALUE, if (wasSet) 1 else 0)
        logEvent(selection, bundle)
    }

    fun deviceAdd(typeOfDevice: String, deviceName: String, addDevice: Boolean) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, deviceName)
        bundle.putInt(FirebaseAnalytics.Param.VALUE, if (addDevice) 1 else 0)
        logEvent(typeOfDevice, bundle)
    }

    fun musicPlayerChoice(context: Context, packageName: String, musicChoiceChanged: Boolean) {
        try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val musicPlayerName = packageManager.getApplicationLabel(appInfo).toString()

            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, musicPlayerName)
            bundle.putInt(FirebaseAnalytics.Param.VALUE, if (musicChoiceChanged) 1 else 0)
            logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }
    }

    fun musicAutoPlay(success: Boolean) {
        val bundle = Bundle()
        bundle.putInt(FirebaseAnalytics.Param.VALUE, if (success) 1 else 0)
        logEvent("auto_play", bundle)
    }

    fun connectViaA2DP(deviceName: String, success: Boolean) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, deviceName)
        bundle.putInt(FirebaseAnalytics.Param.VALUE, if (success) 1 else 0)
        logEvent("connect_via_a2dp", bundle)
    }

    fun activityLaunched(activityName: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, activityName)
        logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

    fun bluetoothActionLaunch(launchEventTrigger: String) = logEvent(launchEventTrigger, null)

    private fun logEvent(eventName: String, bundle: Bundle?) {
        if (isFirebaseEnabled) {
            firebaseAnalytics.logEvent(eventName, bundle)
        }
    }

    companion object {
        private const val TAG = "FirebaseHelper"
    }
}
