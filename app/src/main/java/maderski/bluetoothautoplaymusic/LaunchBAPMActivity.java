package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LaunchBAPMActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_bapm);

        AudioManager audioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);

        BluetoothActions bluetoothActions = new BluetoothActions();

        bluetoothActions.delayGetOrigVol(this);

        finish();
    }
}
