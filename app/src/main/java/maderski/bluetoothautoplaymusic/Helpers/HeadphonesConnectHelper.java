package maderski.bluetoothautoplaymusic.Helpers;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;

import maderski.bluetoothautoplaymusic.BluetoothActions.BTHeadphonesActions;
import maderski.bluetoothautoplaymusic.Controls.VolumeControl;
import maderski.bluetoothautoplaymusic.Services.BTStateChangedService;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;
import maderski.bluetoothautoplaymusic.Utils.ServiceUtils;

/**
 * Created by Jason on 11/4/17.
 */

public class HeadphonesConnectHelper {
    private static final String TAG = "HeadphonesConnectHelper";

    private final Context mContext;
    private final String mAction;
    private final int mState;

    public HeadphonesConnectHelper(Context context, String action, int state) {
        mContext = context;
        mAction = action;
        mState = state;
    }

    public void performActions() {
        boolean doesRequireA2DP = BAPMPreferences.getUseA2dpHeadphones(mContext);
        BTHeadphonesActions btHeadphonesActions = new BTHeadphonesActions(mContext);

        // Get Original volume
        VolumeControl volumeControl = new VolumeControl(mContext);
        volumeControl.saveOriginalVolume();
        Log.i(TAG, "Original Media Volume is: " + Integer.toString(BAPMDataPreferences.getOriginalMediaVolume(mContext)));
        
        if(doesRequireA2DP) {
            checkA2dpConnectionState(btHeadphonesActions);
        } else {
            checkBTConnectionState(btHeadphonesActions);
        }
    }
    
    private void checkA2dpConnectionState(BTHeadphonesActions btHeadphonesActions) {
        if(mAction.equals(BluetoothA2dp.ACTION_CONNECTION_STATE_CHANGED)) {
            switch (mState) {
                case BluetoothProfile.STATE_CONNECTING:
                    Log.d(TAG, "A2DP CONNECTING");
                    break;
                case BluetoothProfile.STATE_CONNECTED:
                    Log.d(TAG, "A2DP CONNECTED");
                    btHeadphonesActions.connectActions();
                    ServiceUtils.startService(mContext, BTStateChangedService.class, BTStateChangedService.TAG);
                    break;
                case BluetoothProfile.STATE_DISCONNECTING:
                    Log.d(TAG, "A2DP DISCONNECTING");
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.d(TAG, "A2DP DISCONNECTED");
                    btHeadphonesActions.disconnectActions();
                    break;
            }
        }
    }
    
    private void checkBTConnectionState(BTHeadphonesActions btHeadphonesActions) {
        switch (mAction) {
            case BluetoothDevice.ACTION_ACL_CONNECTED:
                btHeadphonesActions.connectActionsWithDelay();
                break;
            case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                btHeadphonesActions.disconnectActions();
                break;
        }
    }
}
