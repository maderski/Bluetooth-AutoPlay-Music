package maderski.bluetoothautoplaymusic.Receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import maderski.bluetoothautoplaymusic.Analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.BluetoothActions.BTConnectActions;

/**
 * Created by Jason on 7/28/16.
 */
public class CustomReceiver extends BroadcastReceiver {
    public static final String TAG = CustomReceiver.class.getName();

    private static final String ACTION_POWER_LAUNCH = "maderski.bluetoothautoplaymusic.pluggedinlaunch";
    private static final String ACTION_OFF_TELE_LAUNCH = "maderski.bluetoothautoplaymusic.offtelephonelaunch";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = "None";
        if (intent != null) {
            if (intent.getAction() != null) {
                action = intent.getAction();
                performAction(action, context);
            }
        }
    }

    private void performAction(String action, Context context){
        BTConnectActions btConnectActions = new BTConnectActions(context);
        FirebaseHelper firebaseHelper = new FirebaseHelper(context);

        switch (action) {
            case ACTION_POWER_LAUNCH:
                Log.d(TAG, "POWER_LAUNCH");
                btConnectActions.OnBTConnect();
                firebaseHelper.bluetoothActionLaunch(FirebaseHelper.BTActionsLaunch.POWER);
                break;
            case ACTION_OFF_TELE_LAUNCH:
                Log.d(TAG, "OFF_TELE_LAUNCH");
                //Calling actionsOnBTConnect cause onBTConnect already ran
                btConnectActions.actionsOnBTConnect();
                firebaseHelper.bluetoothActionLaunch(FirebaseHelper.BTActionsLaunch.TELEPHONE);
                break;
        }
    }
}
