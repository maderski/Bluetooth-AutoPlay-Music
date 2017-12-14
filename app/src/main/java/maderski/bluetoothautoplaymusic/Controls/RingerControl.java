package maderski.bluetoothautoplaymusic.Controls;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import maderski.bluetoothautoplaymusic.Helpers.PermissionHelper;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

/**
 * Created by Jason on 12/8/15.
 */
public class RingerControl {
    private static final String TAG = RingerControl.class.getName();

    private AudioManager am;
    private NotificationManager mNotificationManager;
    private Context mContext;

    public RingerControl(Context context){
        mContext = context;
        am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    //turns phone sounds OFF & initialize AudioManager
    public void soundsOFF(){
        boolean isAlreadySilent = am.getRingerMode() == AudioManager.RINGER_MODE_SILENT;
        if(!isAlreadySilent) {
            boolean usePriorityMode = BAPMPreferences.getUsePriorityMode(mContext);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean hasNAPPermission = mNotificationManager.isNotificationPolicyAccessGranted();
                if (usePriorityMode && hasNAPPermission) {
                    mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY);
                    Log.d(TAG, "RingerControl: " + "Priority");
                } else {
                    putPhoneInSilentMode();
                }
            } else {
                putPhoneInSilentMode();
            }
        } else {
            Log.d(TAG, "Ringer is Already silent");
        }
    }

    private void putPhoneInSilentMode() {
        am.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        Log.d(TAG, "RingerControl: " + "Silent");
    }

    //turns phone sounds ON
    public void soundsON(){
        boolean usePriorityMode = BAPMPreferences.getUsePriorityMode(mContext);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String permission = Manifest.permission.ACCESS_NOTIFICATION_POLICY;
            boolean hasNAPPermission = PermissionHelper.isPermissionGranted(mContext, permission);
            if(usePriorityMode && hasNAPPermission) {
                mNotificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
                Log.d(TAG, "RingerControl: " + "Normal");
            } else {
                am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                Log.d(TAG, "RingerControl: " + "Normal");
            }
        } else {
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            Log.d(TAG, "RingerControl: " + "Normal");
        }
    }

    public void vibrateOnly(){
        am.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    }

    public int ringerSetting(){
        return am.getRingerMode();
    }
}
