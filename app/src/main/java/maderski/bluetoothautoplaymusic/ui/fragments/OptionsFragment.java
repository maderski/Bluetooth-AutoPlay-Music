package maderski.bluetoothautoplaymusic.ui.fragments;

import android.Manifest;
import android.content.Context;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.helpers.PermissionHelper;
import maderski.bluetoothautoplaymusic.R;
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences;

public class OptionsFragment extends Fragment {
    private static final String TAG = "OptionsFragment";

    private FirebaseHelper mFirebaseHelper;

    public OptionsFragment() {
        // Required empty public constructor
    }

    public static OptionsFragment newInstance() {
        OptionsFragment fragment = new OptionsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFirebaseHelper = new FirebaseHelper(getActivity());
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_options, container, false);

        autoPlaySwitch(rootView);
        autoBrightnessSwitch(rootView);
        powerConnectedSwitch(rootView);
        sendToBackgroundSwitch(rootView);
        waitTillOffPhoneSwitch(rootView);
        showNotificationSwitch(rootView);
        wifiOffUseTimeSpansSwitch(rootView);
        brightBrightnessButton(rootView);
        dimBrightnessButton(rootView);
        usePriorityModeSwitch(rootView);

        setFonts(rootView, getContext());
        setButtonPreferences(rootView, getActivity());
        setMaxVolumeSeekBar(rootView);
        setupRestoreOriginalVolumeCheckBox(rootView);

        wifiOffDeviceButton(rootView);

        setWifiUseTimeSpansCaption(rootView);

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(mFirebaseHelper == null){
            mFirebaseHelper = new FirebaseHelper(getActivity());
        }
    }

    private void setWifiUseTimeSpansCaption(View view){
        String captionText;
        TextView wifiUseTimeSpanCaption = (TextView)view.findViewById(R.id.wifi_use_time_spans_desc);
        if(BAPMPreferences.INSTANCE.getCanLaunchDirections(getContext())){
            captionText = view.getResources().getString(R.string.wifi_use_time_spans_caption_work_home);
        } else {
            captionText = view.getResources().getString(R.string.wifi_use_time_spans_caption_morning_evening);
        }

        wifiUseTimeSpanCaption.setText(captionText);
    }

    private void setButtonPreferences(View view, Context context) {
        Boolean btnState;
        Switch setting_switch;

        btnState = BAPMPreferences.INSTANCE.getAutoPlayMusic(context);
        setting_switch = (Switch) view.findViewById(R.id.auto_play);
        setting_switch.setChecked(btnState);

        btnState = BAPMPreferences.INSTANCE.getPowerConnected(context);
        setting_switch = (Switch) view.findViewById(R.id.power_connected);
        setting_switch.setChecked(btnState);

        btnState = BAPMPreferences.INSTANCE.getSendToBackground(context);
        setting_switch = (Switch) view.findViewById(R.id.send_to_background);
        setting_switch.setChecked(btnState);

        btnState = BAPMPreferences.INSTANCE.getWaitTillOffPhone(context);
        setting_switch = (Switch) view.findViewById(R.id.wait_till_off_phone);
        setting_switch.setChecked(btnState);

        btnState = BAPMPreferences.INSTANCE.getShowNotification(context);
        setting_switch = (Switch) view.findViewById(R.id.show_notification);
        setting_switch.setChecked(btnState);

        btnState = BAPMPreferences.INSTANCE.getWifiUseMapTimeSpans(context);
        setting_switch = (Switch) view.findViewById(R.id.wifi_use_time_spans);
        setting_switch.setChecked(btnState);

        if(!PermissionHelper.isPermissionGranted(context, PermissionHelper.Permission.COARSE_LOCATION))
            BAPMPreferences.INSTANCE.setAutoBrightness(context, false);
        btnState = BAPMPreferences.INSTANCE.getAutoBrightness(context);
        setting_switch = (Switch) view.findViewById(R.id.auto_brightness);
        setting_switch.setChecked(btnState);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btnState = BAPMPreferences.INSTANCE.getUsePriorityMode(context);
            setting_switch = (Switch) view.findViewById(R.id.sw_priority_mode);
            TextView switch_explaination = (TextView) view.findViewById(R.id.tv_priority_mode_explaination);

            setting_switch.setVisibility(View.VISIBLE);
            setting_switch.setChecked(btnState);

            switch_explaination.setVisibility(View.VISIBLE);
        }

    }

    public void wifiOffUseTimeSpansSwitch(View view){
        Switch autoPlaySwitch = (Switch)view.findViewById(R.id.wifi_use_time_spans);
        autoPlaySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((Switch) view).isChecked();
                mFirebaseHelper.featureEnabled(FirebaseHelper.Option.WIFI_OFF_USE_TIME_SPANS, on);
                if (on) {
                    BAPMPreferences.INSTANCE.setWifiUseMapTimeSpans(getActivity(), true);
                    Log.d(TAG, "WIFI OFF Use Time Spans Switch is ON");
                } else {
                    BAPMPreferences.INSTANCE.setWifiUseMapTimeSpans(getActivity(), false);
                    Log.d(TAG, "WIFI OFF Use Time Spans Switch is OFF");
                }
            }
        });
    }

    public void autoPlaySwitch(View view){
        Switch autoPlaySwitch = (Switch)view.findViewById(R.id.auto_play);
        autoPlaySwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((Switch) view).isChecked();
                mFirebaseHelper.featureEnabled(FirebaseHelper.Option.PLAY_MUSIC, on);
                if (on) {
                    BAPMPreferences.INSTANCE.setAutoplayMusic(getActivity(), true);
                    Log.d(TAG, "AutoPlaySwitch is ON");
                } else {
                    BAPMPreferences.INSTANCE.setAutoplayMusic(getActivity(), false);
                    Log.d(TAG, "AutoPlaySwitch is OFF");
                }
            }
        });
    }

    public void powerConnectedSwitch(View view){
        Switch powerConnectedSwitch = (Switch)view.findViewById(R.id.power_connected);
        powerConnectedSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((Switch) view).isChecked();
                mFirebaseHelper.featureEnabled(FirebaseHelper.Option.POWER_REQUIRED, on);
                if(on){
                    BAPMPreferences.INSTANCE.setPowerConnected(getActivity(), true);
                    Log.d(TAG, "PowerConnected Switch is ON");
                }else{
                    BAPMPreferences.INSTANCE.setPowerConnected(getActivity(), false);
                    Log.d(TAG, "PowerConnected Switch is OFF");
                }
            }
        });
    }

    public void sendToBackgroundSwitch(View view){
        Switch sendToBackgroundSwitch = (Switch)view.findViewById(R.id.send_to_background);
        sendToBackgroundSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((Switch) view).isChecked();
                mFirebaseHelper.featureEnabled(FirebaseHelper.Option.GO_HOME, on);
                if(on){
                    BAPMPreferences.INSTANCE.setSendToBackground(getActivity(), true);
                    Log.d(TAG, "SendToBackground Switch is ON");
                }else{
                    BAPMPreferences.INSTANCE.setSendToBackground(getActivity(), false);
                    Log.d(TAG, "SendToBackground Switch is OFF");
                }
            }
        });
    }

    public void waitTillOffPhoneSwitch(View view){
        Switch waitTillOffPhoneSwitch = (Switch)view.findViewById(R.id.wait_till_off_phone);
        waitTillOffPhoneSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((Switch) view).isChecked();
                mFirebaseHelper.featureEnabled(FirebaseHelper.Option.CALL_COMPLETED, on);
                if(on){
                    BAPMPreferences.INSTANCE.setWaitTillOffPhone(getActivity(), true);
                    Log.d(TAG, "WaitTillOffPhone Switch is ON");
                }else{
                    BAPMPreferences.INSTANCE.setWaitTillOffPhone(getActivity(), false);
                    Log.d(TAG, "WaitTillOffPhone Switch is OFF");
                }
            }
        });
    }

    public void showNotificationSwitch(View view) {
        Switch autoBrightnessSwitch = (Switch)view.findViewById(R.id.show_notification);
        autoBrightnessSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((Switch) view).isChecked();
                mFirebaseHelper.featureEnabled(FirebaseHelper.Option.SHOW_NOTIFICATION, on);
                if(on){
                    BAPMPreferences.INSTANCE.setShowNotification(getActivity(), true);
                    Log.d(TAG, "Show Notification Switch is ON");
                }else{
                    BAPMPreferences.INSTANCE.setShowNotification(getActivity(), false);
                    Log.d(TAG, "Show Notification Switch is OFF");
                }
            }
        });
    }

    public void autoBrightnessSwitch(View view){
        Switch autoBrightnessSwitch = (Switch)view.findViewById(R.id.auto_brightness);
        autoBrightnessSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((Switch) view).isChecked();
                mFirebaseHelper.featureEnabled(FirebaseHelper.Option.AUTO_BRIGHTNESS, on);
                if(on){
                    PermissionHelper.checkPermission(getActivity(), PermissionHelper.Permission.COARSE_LOCATION);

                    BAPMPreferences.INSTANCE.setAutoBrightness(getActivity(), true);

                    Log.d(TAG, "AutoBrightness Switch is ON");
                }else{
                    BAPMPreferences.INSTANCE.setAutoBrightness(getActivity(), false);
                    Log.d(TAG, "AutoBrightness Switch is OFF");
                }
            }
        });
    }

    public void usePriorityModeSwitch(View view) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Switch priorityModeSwitch = (Switch)view.findViewById(R.id.sw_priority_mode);
            priorityModeSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean on = ((Switch) view).isChecked();
                    mFirebaseHelper.featureEnabled(FirebaseHelper.Option.PRIORITY_MODE, on);
                    if(on){
                        String permission = Manifest.permission.ACCESS_NOTIFICATION_POLICY;
                        PermissionHelper.checkPermission(getActivity(), permission);

                        BAPMPreferences.INSTANCE.setUsePriorityMode(getActivity(), true);

                        Log.d(TAG, "AutoBrightness Switch is ON");
                    }else{
                        BAPMPreferences.INSTANCE.setUsePriorityMode(getActivity(), false);
                        Log.d(TAG, "AutoBrightness Switch is OFF");
                    }
                }
            });
        }
    }

    public void dimBrightnessButton(View view){
        Button dimBrightnessButton = (Button)view.findViewById(R.id.dimtimebutton);
        dimBrightnessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerFragment.newInstance(TimePickerFragment.TypeOfTimeSet.SCREEN_BRIGHTNESS_TIME, true, BAPMPreferences.INSTANCE.getDimTime(getActivity()), "Set Dim Time");
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });
    }

    public void brightBrightnessButton(View view){
        Button brightBrightnessButton = (Button)view.findViewById(R.id.brighttimebutton);
        brightBrightnessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerFragment.newInstance(TimePickerFragment.TypeOfTimeSet.SCREEN_BRIGHTNESS_TIME, false, BAPMPreferences.INSTANCE.getBrightTime(getActivity()), "Set Bright Time");
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });
    }

    public void setMaxVolumeSeekBar(View view){
        AudioManager audioManager = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
        SeekBar volumeSeekBar = (SeekBar)view.findViewById(R.id.max_volume_seekBar);

        volumeSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeSeekBar.setProgress(BAPMPreferences.INSTANCE.getUserSetMaxVolume(getActivity()));
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BAPMPreferences.INSTANCE.setUserSetMaxVolume(getActivity().getApplicationContext(), progress);
                Log.d(TAG, "User set MAX volume: " + Integer.toString(BAPMPreferences.INSTANCE.getUserSetMaxVolume(getActivity().getApplicationContext())));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setupRestoreOriginalVolumeCheckBox(View view){
        final Context context = view.getContext();
        boolean isEnabled = BAPMPreferences.INSTANCE.getRestoreNotificationVolume(context);
        CheckBox restoreVolumeCheckBox = (CheckBox)view.findViewById(R.id.cb_restore_original_volume);

        restoreVolumeCheckBox.setChecked(isEnabled);

        restoreVolumeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                BAPMPreferences.INSTANCE.setRestoreNotificationVolume(context, isChecked);
            }
        });
    }

    public void wifiOffDeviceButton(View view){
        Button wifiOffDeviceButton = (Button)view.findViewById(R.id.wifi_off_button);
        wifiOffDeviceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFirebaseHelper.selectionMade(FirebaseHelper.Selection.SET_WIFI_OFF_DEVICE);
                DialogFragment newFragment = WifiOffFragment.newInstance();
                newFragment.show(getActivity().getSupportFragmentManager(), "wifiOffFragment");
            }
        });
    }

    private void setFonts(View view, Context context){
        Typeface typeface_bold = Typeface.createFromAsset(context.getAssets(), "fonts/TitilliumText600wt.otf");

        TextView textView = (TextView)view.findViewById(R.id.settingsText);
        textView.setTypeface(typeface_bold);

        textView = (TextView)view.findViewById(R.id.auto_play);
        textView.setTypeface(typeface_bold);

        textView = (TextView)view.findViewById(R.id.power_connected);
        textView.setTypeface(typeface_bold);

        textView = (TextView)view.findViewById(R.id.send_to_background);
        textView.setTypeface(typeface_bold);

        textView = (TextView)view.findViewById(R.id.wait_till_off_phone);
        textView.setTypeface(typeface_bold);

        textView = (TextView)view.findViewById(R.id.auto_brightness);
        textView.setTypeface(typeface_bold);

        textView = (TextView)view.findViewById(R.id.manualBrtLabel);
        textView.setTypeface(typeface_bold);

        textView = (TextView)view.findViewById(R.id.userSetMaxVolumeLabel);
        textView.setTypeface(typeface_bold);

        textView = (TextView)view.findViewById(R.id.show_notification);
        textView.setTypeface(typeface_bold);

        textView = (TextView)view.findViewById(R.id.wifi_use_time_spans);
        textView.setTypeface(typeface_bold);
    }
}
