package maderski.bluetoothautoplaymusic.UI;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.HashSet;
import java.util.Set;

import maderski.bluetoothautoplaymusic.Analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.Permissions;
import maderski.bluetoothautoplaymusic.R;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

public class SettingsActivity extends AppCompatActivity implements TimePickerFragment.TimePickerDialogListener {

    private static final String TAG = SettingsActivity.class.getName();

    FirebaseHelper mFirebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mFirebaseHelper = new FirebaseHelper(this);
        mFirebaseHelper.activityLaunched(FirebaseHelper.ActivityName.SETTINGS);
    }

    @Override
    protected void onResume(){
        super.onResume();
        setFonts();
        setButtonPreferences(this);
        setDaysToLaunchLabel();
        setCheckBoxes();
        setMaxVolumeSeekBar();
    }

    @Override
    protected void onPause(){
        super.onPause();
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

        btnState = BAPMPreferences.getSendToBackground(context);
        setting_switch = (Switch) findViewById(R.id.send_to_background);
        setting_switch.setChecked(btnState);

        btnState = BAPMPreferences.getWaitTillOffPhone(context);
        setting_switch = (Switch) findViewById(R.id.wait_till_off_phone);
        setting_switch.setChecked(btnState);

        Permissions permissions = new Permissions();
        if(!permissions.isLocationPermissionGranted(this))
            BAPMPreferences.setAutoBrightness(this, false);
        btnState = BAPMPreferences.getAutoBrightness(context);
        setting_switch = (Switch) findViewById(R.id.auto_brightness);
        setting_switch.setChecked(btnState);

    }

    public void autoPlaySwitch(View view){
        boolean on = ((Switch) view).isChecked();
        mFirebaseHelper.featureEnabled(FirebaseHelper.Option.PLAY_MUSIC, on);
        if (on) {
            BAPMPreferences.setAutoplayMusic(this, true);
            if(BuildConfig.DEBUG)
                Log.i(TAG, "AutoPlaySwitch is ON");
        } else {
            BAPMPreferences.setAutoplayMusic(this, false);
            if(BuildConfig.DEBUG)
                Log.i(TAG, "AutoPlaySwitch is OFF");
        }
    }

    public void powerConnectedSwitch(View view){
        boolean on = ((Switch) view).isChecked();
        mFirebaseHelper.featureEnabled(FirebaseHelper.Option.POWER_REQUIRED, on);
        if(on){
            BAPMPreferences.setPowerConnected(this, true);
            if(BuildConfig.DEBUG)
                Log.i(TAG, "PowerConnected Switch is ON");
        }else{
            BAPMPreferences.setPowerConnected(this, false);
            if(BuildConfig.DEBUG)
                Log.i(TAG, "PowerConnected Switch is OFF");
        }
    }

    public void sendToBackgroundSwitch(View view){
        boolean on = ((Switch) view).isChecked();
        mFirebaseHelper.featureEnabled(FirebaseHelper.Option.GO_HOME, on);
        if(on){
            BAPMPreferences.setSendToBackground(this, true);
            if(BuildConfig.DEBUG)
                Log.i(TAG, "SendToBackground Switch is ON");
        }else{
            BAPMPreferences.setSendToBackground(this, false);
            if(BuildConfig.DEBUG)
                Log.i(TAG, "SendToBackground Switch is OFF");
        }
    }

    public void waitTillOffPhoneSwitch(View view){
        boolean on = ((Switch) view).isChecked();
        mFirebaseHelper.featureEnabled(FirebaseHelper.Option.CALL_COMPLETED, on);
        if(on){
            BAPMPreferences.setWaitTillOffPhone(this, true);
            if(BuildConfig.DEBUG)
                Log.i(TAG, "WaitTillOffPhone Switch is ON");
        }else{
            BAPMPreferences.setWaitTillOffPhone(this, false);
            if(BuildConfig.DEBUG)
                Log.i(TAG, "WaitTillOffPhone Switch is OFF");
        }
    }

    public void autoBrightnessSwitch(View view){
        boolean on = ((Switch) view).isChecked();
        mFirebaseHelper.featureEnabled(FirebaseHelper.Option.AUTO_BRIGHTNESS, on);
        if(on){
            Permissions permissions = new Permissions();
            permissions.checkLocationPermission(this);

            BAPMPreferences.setAutoBrightness(this, true);

            if(BuildConfig.DEBUG)
                Log.i(TAG, "WaitTillOffPhone Switch is ON");
        }else{
            BAPMPreferences.setAutoBrightness(this, false);
            if(BuildConfig.DEBUG)
                Log.i(TAG, "WaitTillOffPhone Switch is OFF");
        }
    }

    public void dimBrightnessButton(View view){
        DialogFragment newFragment = TimePickerFragment.newInstance(true, BAPMPreferences.getDimTime(this), "Set Dim Time");
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void brightBrightnessButton(View view){
        DialogFragment newFragment = TimePickerFragment.newInstance(false, BAPMPreferences.getBrightTime(this), "Set Bright Time");
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void setMaxVolumeSeekBar(){
        AudioManager audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        SeekBar volumeSeekBar = (SeekBar)findViewById(R.id.max_volume_seekBar);
        volumeSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeSeekBar.setProgress(BAPMPreferences.getUserSetMaxVolume(this));
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BAPMPreferences.setUserSetMaxVolume(getApplicationContext(), progress);
                if(BuildConfig.DEBUG){
                    Log.d(TAG, "User set MAX volume: " + Integer.toString(BAPMPreferences.getUserSetMaxVolume(getApplicationContext())));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onTimeSet(boolean isDim, TimePicker view, int hourOfDay, int minute) {
        int timeSet = (hourOfDay * 100) + minute;
        if(isDim){
            mFirebaseHelper.manualTimeSet(FirebaseHelper.Selection.DIM_TIME, true);
            BAPMPreferences.setDimTime(this, timeSet);
            if(BuildConfig.DEBUG)
                Log.i("Settings", "Dim brightness");
        }else{
            mFirebaseHelper.manualTimeSet(FirebaseHelper.Selection.BRIGHT_TIME, true);
            BAPMPreferences.setBrightTime(this, timeSet);
            if(BuildConfig.DEBUG)
                Log.i("Settings", "Bright brightness");
        }
    }

    @Override
    public void onTimeCancel(boolean isDim) {
        mFirebaseHelper.manualTimeSet(isDim ? FirebaseHelper.Selection.DIM_TIME : FirebaseHelper.Selection.BRIGHT_TIME, false);
    }

    private void setFonts(){
        Typeface typeface_bold = Typeface.createFromAsset(getAssets(), "fonts/TitilliumText600wt.otf");

        TextView textView = (TextView)findViewById(R.id.settingsText);
        textView.setTypeface(typeface_bold);

        textView = (TextView)findViewById(R.id.auto_play);
        textView.setTypeface(typeface_bold);

        textView = (TextView)findViewById(R.id.power_connected);
        textView.setTypeface(typeface_bold);

        textView = (TextView)findViewById(R.id.send_to_background);
        textView.setTypeface(typeface_bold);

        textView = (TextView)findViewById(R.id.wait_till_off_phone);
        textView.setTypeface(typeface_bold);

        textView = (TextView)findViewById(R.id.auto_brightness);
        textView.setTypeface(typeface_bold);

        textView = (TextView)findViewById(R.id.manualBrtLabel);
        textView.setTypeface(typeface_bold);

        textView = (TextView)findViewById(R.id.daysToLaunchLabel);
        textView.setTypeface(typeface_bold);

        textView = (TextView)findViewById(R.id.userSetMaxVolumeLabel);
        textView.setTypeface(typeface_bold);

    }

    private void setDaysToLaunchLabel(){
        String mapChoice = BAPMPreferences.getMapsChoice(this);
        PackageManager packageManager = getPackageManager();
        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(mapChoice, 0);
            mapChoice = packageManager.getApplicationLabel(appInfo).toString();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            mapChoice = "Maps";
        }

        TextView textView = (TextView)findViewById(R.id.daysToLaunchLabel);
        textView.setText("DAYS to launch " + mapChoice);
    }

    private void setCheckBoxes(){
        CheckBox checkBox;
        String[] entireWeek = {"1", "2", "3", "4", "5", "6", "7"};
        Set<String> daysToLaunchSet = BAPMPreferences.getDaysToLaunchMaps(this);

        LinearLayout daysToLaunchChkBoxLL = (LinearLayout) findViewById(R.id.daysChkBoxLL);
        daysToLaunchChkBoxLL.removeAllViews();

        for(String day : entireWeek){
            checkBox = new CheckBox(this);
            checkBox.setText(getNameOfDay(day));
            checkBox.setTextColor(getResources().getColor(R.color.colorPrimary));
            checkBox.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/TitilliumText400wt.otf"));
            checkBox.setChecked(daysToLaunchSet.contains(day));
            checkboxListener(checkBox, day);
            daysToLaunchChkBoxLL.addView(checkBox);
        }
    }

    private void checkboxListener(CheckBox checkBox, String dayNumber){
        final Context ctx = this;
        final CheckBox cb = checkBox;
        final String dn = dayNumber;

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> daysToLaunch = new HashSet<>(BAPMPreferences.getDaysToLaunchMaps(ctx));
                if(cb.isChecked()){
                    daysToLaunch.add(dn);
                    BAPMPreferences.setDaysToLaunchMaps(ctx, daysToLaunch);
                }else{
                    daysToLaunch.remove(dn);
                    BAPMPreferences.setDaysToLaunchMaps(ctx, daysToLaunch);
                }
            }
        });
    }

    private String getNameOfDay(String dayNumber){
        switch(dayNumber){
            case "1":
                return "Sunday";
            case "2":
                return "Monday";
            case "3":
                return "Tuesday";
            case "4":
                return "Wednesday";
            case "5":
                return "Thursday";
            case "6":
                return "Friday";
            case "7":
                return "Saturday";
            default:
                return "Unknown Day Number";
        }
    }
}
