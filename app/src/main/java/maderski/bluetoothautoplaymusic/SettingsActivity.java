package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.ToggleButton;

public class SettingsActivity extends AppCompatActivity {

    final static String TAG = SettingsActivity.class.getName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void onResume(){
        super.onResume();
        setButtonPreferences(this);
    }

    private void setButtonPreferences(Context context) {
        Boolean btnState;
        Switch setting_switch;

        btnState = BAPMPreferences.getAutoPlayMusic(context);
        setting_switch = (Switch) findViewById(R.id.auto_play);
        setting_switch.setChecked(btnState);

        btnState = BAPMPreferences.getPowerConnected(context);
        setting_switch = (Switch) findViewById(R.id.power_connected);
        setting_switch.setChecked(btnState);

    }

    public void autoPlaySwitch(View view){
        boolean on = ((Switch) view).isChecked();
        if (on) {
            BAPMPreferences.setAutoplayMusic(this, true);
            Log.i(TAG, "AutoPlaySwitch is ON");
        } else {
            BAPMPreferences.setAutoplayMusic(this, false);
            Log.i(TAG, "AutoPlaySwitch is OFF");
        }
    }

    public void powerConnectedSwitch(View view){
        boolean on = ((Switch) view).isChecked();
        if(on){
            BAPMPreferences.setPowerConnected(this, true);
            Log.i(TAG, "PowerConnected Switch is ON");
        }else{
            BAPMPreferences.setPowerConnected(this, false);
            Log.i(TAG, "PowerConnected Switch is OFF");
        }
    }
}
