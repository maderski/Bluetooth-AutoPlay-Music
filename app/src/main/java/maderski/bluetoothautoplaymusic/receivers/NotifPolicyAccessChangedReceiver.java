package maderski.bluetoothautoplaymusic.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import maderski.bluetoothautoplaymusic.controls.RingerControl;
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences;

/**
 * Created by Jason on 2/12/17.
 */

public class NotifPolicyAccessChangedReceiver extends BroadcastReceiver {
    private static final String TAG = "NotifPolicyAccessChange";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null){
            if(intent.getAction() != null){
                Log.d(TAG, "ACTION: " + intent.getAction());
                RingerControl ringerControl = new RingerControl(context);
                BAPMDataPreferences.setCurrentRingerSet(context, ringerControl.ringerSetting());
                ringerControl.soundsOFF();
                context.getApplicationContext().unregisterReceiver(this);
            }
        }
    }
}
