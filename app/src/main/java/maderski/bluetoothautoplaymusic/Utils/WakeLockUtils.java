package maderski.bluetoothautoplaymusic.Utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.media.AudioManager;

import maderski.bluetoothautoplaymusic.Analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.Controls.WakeLockControl.ScreenONLock;
import maderski.bluetoothautoplaymusic.LaunchApp;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

/**
 * Created by Jason on 6/25/17.
 */

public class WakeLockUtils {
    public static void reHoldWakeLock(Context context){
        boolean shouldKeepScreenOn = BAPMPreferences.getKeepScreenON(context);

        if(shouldKeepScreenOn) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            boolean ranBAPM = BAPMDataPreferences.getRanActionsOnBtConnect(context);
            boolean isConnectedToBT = audioManager.isBluetoothA2dpOn();

            if (ranBAPM && isConnectedToBT) {
                // Rehold wakelock
                ScreenONLock screenONLock = ScreenONLock.getInstance();
                screenONLock.releaseWakeLock();
                screenONLock.enableWakeLock(context);

                // Check if Keyguard is locked, if so unlock it
                boolean isKeyguardLocked = ((KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE)).isKeyguardLocked();
                LaunchApp launchApp = new LaunchApp();
                if(isKeyguardLocked) {
                    launchApp.launchBAPMActivity(context);
                }

                // Log rehold wakelock event
                FirebaseHelper firebaseHelper = new FirebaseHelper(context);
                firebaseHelper.wakelockRehold();
            }
        }
    }
}
