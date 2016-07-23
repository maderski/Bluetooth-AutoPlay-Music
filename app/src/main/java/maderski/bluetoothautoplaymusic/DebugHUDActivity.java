package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DebugHUDActivity extends AppCompatActivity {

    private BluetoothActions bluetoothActions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_hud);
    }

    @Override
    protected void onResume(){
        super.onResume();
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        bluetoothActions = new BluetoothActions(this, audioManager);
        VolumeControl.originalMediaVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    public void connectBTButton(View view){
        bluetoothActions.OnBTConnect();
    }

    public void disconnectBTButton(View view){
        bluetoothActions.actionsOnBTDisconnect();
    }
}
