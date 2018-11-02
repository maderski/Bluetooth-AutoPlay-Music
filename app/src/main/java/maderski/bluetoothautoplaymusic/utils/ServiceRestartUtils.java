package maderski.bluetoothautoplaymusic.utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import java.util.Collections;
import java.util.Set;

import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.controls.wakelockcontrol.ScreenONLock;
import maderski.bluetoothautoplaymusic.helpers.A2DPHelper;
import maderski.bluetoothautoplaymusic.helpers.LaunchAppHelper;
import maderski.bluetoothautoplaymusic.services.BTStateChangedService;
import maderski.bluetoothautoplaymusic.services.OnBTConnectService;
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences;

/**
 * Created by Jason on 6/25/17.
 */

public class ServiceRestartUtils {
    private static final String TAG = "ServiceRestartUtils";

    public static void reHoldWakeLock(Context context){
        boolean shouldKeepScreenOn = BAPMPreferences.INSTANCE.getKeepScreenON(context);

        if(shouldKeepScreenOn) {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            boolean ranBAPM = BAPMDataPreferences.INSTANCE.getRanActionsOnBtConnect(context);
            boolean isConnectedToBT = audioManager.isBluetoothA2dpOn();

            if (ranBAPM && isConnectedToBT) {
                // Rehold wakelock
                ScreenONLock screenONLock = ScreenONLock.getInstance();
                screenONLock.releaseWakeLock();
                screenONLock.enableWakeLock(context);

                // Check if Keyguard is locked, if so unlock it
                boolean isKeyguardLocked = ((KeyguardManager)context.getSystemService(Context.KEYGUARD_SERVICE)).isKeyguardLocked();
                LaunchAppHelper launchAppHelper = new LaunchAppHelper();
                if(isKeyguardLocked) {
                    launchAppHelper.launchBAPMActivity(context);
                }

                // Log rehold wakelock event
                FirebaseHelper firebaseHelper = new FirebaseHelper(context);
                firebaseHelper.wakelockRehold();
            }
        }
    }

    public static void restartAdditionServices(final Context context){
        final AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        A2DPHelper a2dpHelper = new A2DPHelper(new A2DPHelper.A2DPCallbacks() {
            @Override
            public void connectedDeviceNames(Set<String> deviceNames) {
                Set<String> selectedBTDevices = BAPMPreferences.INSTANCE.getBTDevices(context);

                boolean isBTConnected = audioManager.isBluetoothA2dpOn();
                boolean isOnBTConnectServiceRunning = ServiceUtils.isServiceRunning(context, OnBTConnectService.class);
                boolean isBTStateChangedServiceRunning = ServiceUtils.isServiceRunning(context, BTStateChangedService.class);
                boolean isASelectedBTDevice = !Collections.disjoint(selectedBTDevices, deviceNames);
                Log.d(TAG, "CONNECTED DEVICE IS SELECTED: " + String.valueOf(isASelectedBTDevice)
                        + " " + String.valueOf(deviceNames.size()));
                boolean shouldStartServices = isBTConnected && isASelectedBTDevice &&
                        !isBTStateChangedServiceRunning && !isOnBTConnectServiceRunning;
                Log.d(TAG, "SHOULD START SERVICES: " + String.valueOf(shouldStartServices));
                if(shouldStartServices){
                    ServiceUtils.startService(context, OnBTConnectService.class, OnBTConnectService.TAG);
                    ServiceUtils.startService(context, BTStateChangedService.class, BTStateChangedService.TAG);
                }
            }
        });

        a2dpHelper.getConnectedA2DPDevices(context);

    }
}
