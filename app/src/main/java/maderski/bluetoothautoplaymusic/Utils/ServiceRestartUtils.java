package maderski.bluetoothautoplaymusic.Utils;

import android.app.KeyguardManager;
import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import java.util.Collections;
import java.util.Set;

import maderski.bluetoothautoplaymusic.Analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.Controls.WakeLockControl.ScreenONLock;
import maderski.bluetoothautoplaymusic.Helpers.A2DPHelper;
import maderski.bluetoothautoplaymusic.Helpers.BluetoothConnectHelper;
import maderski.bluetoothautoplaymusic.LaunchApp;
import maderski.bluetoothautoplaymusic.Services.BTStateChangedService;
import maderski.bluetoothautoplaymusic.Services.OnBTConnectService;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

/**
 * Created by Jason on 6/25/17.
 */

public class ServiceRestartUtils {
    private static final String TAG = "ServiceRestartUtils";

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

    public static void restartAdditionServices(final Context context){
        final AudioManager audioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        A2DPHelper a2dpHelper = new A2DPHelper(new A2DPHelper.A2DPCallbacks() {
            @Override
            public void connectedDeviceNames(Set<String> deviceNames) {
                Set<String> selectedBTDevices = BAPMPreferences.getBTDevices(context);

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
