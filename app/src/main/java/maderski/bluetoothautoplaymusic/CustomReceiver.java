package maderski.bluetoothautoplaymusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

/**
 * Created by Jason on 7/28/16.
 */
public class CustomReceiver extends BroadcastReceiver {
    private static final String ACTION_POWER_LAUNCH = "maderski.bluetoothautoplaymusic.pluggedinlaunch";
    private static final String ACTION_OFF_TELE_LAUNCH = "maderski.bluetoothautoplaymusic.offtelephonelaunch";

    private BluetoothActions bluetoothActions;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = "None";
        if(intent != null) {
            if (intent.getAction() != null) {
                action = intent.getAction();
                AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                bluetoothActions = new BluetoothActions(
                        context,
                        audioManager,
                        new ScreenONLock(),
                        new Notification(),
                        new VolumeControl(audioManager));
            }
        }

        switch (action){
            case ACTION_POWER_LAUNCH:
                bluetoothActions.OnBTConnect();
                break;
            case ACTION_OFF_TELE_LAUNCH:
                //Calling actionsOnBTConnect cause onBTConnect already ran
                bluetoothActions.actionsOnBTConnect();
                break;
        }
    }
}
