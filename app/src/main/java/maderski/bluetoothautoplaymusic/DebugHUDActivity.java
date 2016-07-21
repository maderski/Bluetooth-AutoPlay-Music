package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class DebugHUDActivity extends AppCompatActivity {

    private AudioManager am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_hud);
    }

    @Override
    protected void onResume(){
        super.onResume();
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    }

    public void connectBTButton(View view){
        BluetoothActions bluetoothActions = new BluetoothActions();
        bluetoothActions.OnBTConnect(this, am);
    }

    public void disconnectBTButton(View view){
        BluetoothActions bluetoothActions = new BluetoothActions();
        bluetoothActions.BTDisconnectDoStuff(this, am);
    }
}
