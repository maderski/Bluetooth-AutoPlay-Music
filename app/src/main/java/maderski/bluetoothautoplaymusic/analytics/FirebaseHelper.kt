package maderski.bluetoothautoplaymusic.analytics

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.annotation.StringDef
import android.util.Log

import com.google.firebase.analytics.FirebaseAnalytics
import maderski.bluetoothautoplaymusic.analytics.constants.SelectionConstants

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Created by Jason on 1/28/17.
 */

class FirebaseHelper(private val context: Context) {

    private val mFirebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun featureEnabled(featureName: String, isEnabled: Boolean) {
        val bundle = Bundle()
        bundle.putInt(FirebaseAnalytics.Param.VALUE, if (isEnabled) 1 else 0)
        mFirebaseAnalytics.logEvent(featureName, bundle)
    }

    fun selectionMade(selection: String) {
        mFirebaseAnalytics.logEvent(selection, null)
    }

    fun useGoogleMaps() {
        mFirebaseAnalytics.logEvent("google_maps", null)
    }

    fun useWaze() {
        mFirebaseAnalytics.logEvent("waze", null)
    }

    fun timeSetSelected(@SelectionConstants.Selection selection: String, wasSet: Boolean) {
        val bundle = Bundle()
        bundle.putInt(FirebaseAnalytics.Param.VALUE, if (wasSet) 1 else 0)
        mFirebaseAnalytics.logEvent(selection, bundle)
    }

    fun deviceAdd(typeOfDevice: String, deviceName: String, addDevice: Boolean) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, deviceName)
        bundle.putInt(FirebaseAnalytics.Param.VALUE, if (addDevice) 1 else 0)
        mFirebaseAnalytics.logEvent(typeOfDevice, bundle)
    }

    fun musicPlayerChoice(packageName: String, musicChoiceChanged: Boolean) {
        try {
            val packageManager = context.packageManager
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            val musicPlayerName = packageManager.getApplicationLabel(appInfo).toString()

            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, musicPlayerName)
            bundle.putInt(FirebaseAnalytics.Param.VALUE, if (musicChoiceChanged) 1 else 0)
            //            mFirebaseAnalytics.logEvent("music_player_selected", bundle);
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)
        } catch (e: Exception) {
            Log.e(TAG, e.message)
        }

    }

    fun musicAutoPlay(success: Boolean) {
        val bundle = Bundle()
        bundle.putInt(FirebaseAnalytics.Param.VALUE, if (success) 1 else 0)
        mFirebaseAnalytics.logEvent("auto_play", bundle)
    }

    fun wakelockRehold() {
        mFirebaseAnalytics.logEvent("wakelock_rehold", null)
    }

    fun connectViaA2DP(deviceName: String, success: Boolean) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, deviceName)
        bundle.putInt(FirebaseAnalytics.Param.VALUE, if (success) 1 else 0)
        mFirebaseAnalytics.logEvent("connect_via_a2dp", bundle)
    }

    fun activityLaunched(activityName: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, activityName)
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle)
    }

    fun bluetoothActionLaunch(launchEventTrigger: String) {
        mFirebaseAnalytics.logEvent(launchEventTrigger, null)
    }

    companion object {
        private val TAG = "FirebaseHelper"
    }
}
