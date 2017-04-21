package maderski.bluetoothautoplaymusic.UI;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import maderski.bluetoothautoplaymusic.Analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.Helpers.BluetoothDeviceHelper;
import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.LaunchApp;
import maderski.bluetoothautoplaymusic.PackageTools;
import maderski.bluetoothautoplaymusic.Helpers.PermissionHelper;
import maderski.bluetoothautoplaymusic.R;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private Set<String> saveBTDevices = new HashSet<>();
    private LaunchApp launchApp;
    private List<String> installedMediaPlayers = new ArrayList<>();
    private FirebaseHelper mFirebaseHelper;

    public HomeFragment() {
        // Required empty public constructor
    }
    
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        mapsToggleButton(rootView, getActivity());
        launchMusicPlayerToggleButton(rootView, getActivity());
        autoplayOnlyButton(rootView);
        keepONToggleButton(rootView, getActivity());
        priorityToggleButton(rootView, getActivity());
        volumeMAXToggleButton(rootView, getActivity());
        unlockScreenToggleButton(rootView, getActivity());

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
        installedMediaPlayers = listOfInstalledMediaPlayers();

        setupUIElements(getView(), getContext());
        launchApp = new LaunchApp();
        checkIfWazeRemoved(getContext());

    }

    //Checks if WAZE was removed and if WAZE was set to the MapsChoice and if so, set MapsChoice in
    //SharedPrefs to MAPS
    private void checkIfWazeRemoved(Context context){
        String mapAppChoice = BAPMPreferences.getMapsChoice(context);
        if(mapAppChoice.equalsIgnoreCase(PackageTools.PackageName.WAZE)) {
            if (!launchApp.checkPkgOnPhone(context, PackageTools.PackageName.WAZE)) {
                Log.d(TAG, "Checked");
                BAPMPreferences.setMapsChoice(context, PackageTools.PackageName.MAPS);
            }else {
                Log.d(TAG, "WAZE is installed");
            }
        }
    }

    //Setup the UI
    private void setupUIElements(View view, Context context){
        setFonts(view, context);
        radiobuttonCreator(view, context);
        checkboxCreator(view, context);
        setButtonPreferences(view, context);
        radioButtonListener(view, context);
        setMapsButtonText(view, context);
    }

    //Create Checkboxes
    public void checkboxCreator(View view, Context context) {

        CheckBox checkBox;
        TextView textView;

        LinearLayout BTDeviceCkBoxLL = (LinearLayout) view.findViewById(R.id.checkBoxLL);
        BTDeviceCkBoxLL.removeAllViews();
        List<String> listOfBTDevices = BluetoothDeviceHelper.listOfBluetoothDevices();
        if (listOfBTDevices.contains("No Bluetooth Device found") ||
                listOfBTDevices.isEmpty()){
            textView = new TextView(context);
            textView.setText(R.string.no_BT_found);
            BTDeviceCkBoxLL.addView(textView);
        }else{
            for (String BTDevice : listOfBTDevices) {
                int textColor = R.color.colorPrimary;
                checkBox = new CheckBox(context);
                checkBox.setText(BTDevice);
                if(BAPMPreferences.getHeadphoneDevices(context).contains(BTDevice)) {
                    textColor = R.color.lightGray;
                    int states[][] = {{android.R.attr.state_checked}};
                    int colors[] = {textColor, textColor};
                    CompoundButtonCompat.setButtonTintList(checkBox, new ColorStateList(states, colors));
                    checkBox.setClickable(false);
                    checkBox.setChecked(true);
                } else if(BAPMPreferences.getBTDevices(context) != null) {
                    checkBox.setChecked(BAPMPreferences.getBTDevices(context).contains(BTDevice));
                }
                checkBox.setTextColor(ContextCompat.getColor(getContext(), textColor));
                checkBox.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/TitilliumText400wt.otf"));

                if(!BAPMPreferences.getHeadphoneDevices(context).contains(BTDevice)) {
                    checkboxListener(checkBox, BTDevice, context);
                }
                BTDeviceCkBoxLL.addView(checkBox);
            }
        }

    }

    //Get Selected Checkboxes
    private void checkboxListener(CheckBox checkBox, String BTDevice, final Context context){
        final CheckBox cb = checkBox;
        final String BTD = BTDevice;

        saveBTDevices = new HashSet<String>(BAPMPreferences.getBTDevices(context));

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb.isChecked()) {
                    saveBTDevices.add(BTD);
                    if(BuildConfig.DEBUG) {
                        Log.d(TAG, "TRUE" + " " + BTD);
                    }
                    if(BuildConfig.DEBUG) {
                        Log.d(TAG, "SAVED");
                    }
                } else {
                    saveBTDevices.remove(BTD);
                    if(BuildConfig.DEBUG) {
                        Log.d(TAG, "FALSE" + " " + BTD);
                    }
                    if(BuildConfig.DEBUG) {
                        Log.d(TAG, "SAVED");
                    }
                }
                BAPMPreferences.setBTDevices(context, saveBTDevices);
                mFirebaseHelper.deviceAdd(FirebaseHelper.Selection.BLUETOOTH_DEVICE, BTD, cb.isChecked());
            }
        });
    }

    //Get list of installed Mediaplayers and create Radiobuttons
    private void radiobuttonCreator(View view, Context context){

        RadioButton rdoButton;
        ApplicationInfo appInfo;
        String mediaPlayer = "No Name";
        PackageManager pm = context.getPackageManager();

        RadioGroup rdoMPGroup = (RadioGroup) view.findViewById(R.id.rdoMusicPlayers);
        rdoMPGroup.removeAllViews();

        for(String packageName : installedMediaPlayers){

            try{
                appInfo = pm.getApplicationInfo(packageName, 0);
                mediaPlayer = pm.getApplicationLabel(appInfo).toString();

            }catch (Exception e){
                Log.e(TAG, e.getMessage());
            }

            rdoButton = new RadioButton(context);
            rdoButton.setText(mediaPlayer);
            rdoButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            rdoButton.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/TitilliumText400wt.otf"));
            rdoMPGroup.addView(rdoButton);
        }
    }

    //Get Selected Radiobutton
    private void radioButtonListener(final View view, final Context context){
        RadioGroup group = (RadioGroup) view.findViewById(R.id.rdoMusicPlayers);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(radioButton);
                String packageName = installedMediaPlayers.get(index);
                BAPMPreferences.setPkgSelectedMusicPlayer(context, packageName);
                if(BAPMPreferences.getPkgSelectedMusicPlayer(context) != null) {
                    if(!BAPMPreferences.getPkgSelectedMusicPlayer(context).equalsIgnoreCase(packageName)) {
                        mFirebaseHelper.musicPlayerChoice(packageName, true);
                    } else {
                        mFirebaseHelper.musicPlayerChoice(packageName, false);
                    }
                }

                setAppleMusicRequirements(view, context, packageName);

                Log.d(TAG, Integer.toString(index));
                Log.d(TAG, BAPMPreferences.getPkgSelectedMusicPlayer(context));
                Log.d(TAG, Integer.toString(radioGroup.getCheckedRadioButtonId()));
            }
        });
    }

    private void setAppleMusicRequirements(View view, Context context, String packageName){
        // Set Launch App and Unlock screen to true since it is required by Apple Music to play
        if(packageName.equals(PackageTools.PackageName.APPLEMUSIC)){
            Set<String> autoplayOnly = BAPMPreferences.getHeadphoneDevices(context);
            if(!autoplayOnly.isEmpty()){
                BAPMPreferences.setHeadphoneDevices(context, new HashSet<String>());
                checkboxCreator(view, getContext());
                Toast.makeText(getContext(), "Autoplay ONLY not supported with Apple Music", Toast.LENGTH_LONG).show();
            }

            ToggleButton launchMusicPlayerToggleButton = (ToggleButton)view.findViewById(R.id.LaunchMusicPlayerToggleButton);
            ToggleButton unlockScreenToggleButton = (ToggleButton)view.findViewById(R.id.UnlockToggleButton);
            ToggleButton launchMapsToggleButton = (ToggleButton)view.findViewById(R.id.MapsToggleButton);
            if(!BAPMPreferences.getLaunchMusicPlayer(context) || !BAPMPreferences.getUnlockScreen(context)
                    || BAPMPreferences.getLaunchGoogleMaps(context)) {

                if(BAPMPreferences.getLaunchGoogleMaps(context)){
                    Toast.makeText(context, "Launching of Maps/Waze not supported with Apple music", Toast.LENGTH_LONG).show();
                }

                launchMusicPlayerToggleButton.setChecked(true);
                unlockScreenToggleButton.setChecked(true);
                launchMapsToggleButton.setChecked(false);
                BAPMPreferences.setLaunchMusicPlayer(context, true);
                BAPMPreferences.setUnlockScreen(context, true);
                BAPMPreferences.setLaunchGoogleMaps(context, false);
            }
        }
    }

    //Change the Maps button text to Maps or Waze depending on what Maps the user is launching
    private void setMapsButtonText(View view, Context context){
        String mapChoice = BAPMPreferences.getMapsChoice(context);
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(mapChoice, 0);
            mapChoice = packageManager.getApplicationLabel(appInfo).toString();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            mapChoice = "Maps";
        }

        TextView textView = (TextView)view.findViewById(R.id.textView4);
        textView.setText("Launch " + mapChoice);
    }

    //Set the button and radiobutton states
    private void setButtonPreferences(View view, Context context){
        Boolean btnState;
        ToggleButton toggleButton;

        btnState = BAPMPreferences.getLaunchGoogleMaps(context);
        toggleButton = (ToggleButton)view.findViewById(R.id.MapsToggleButton);
        toggleButton.setChecked(btnState);

        btnState = BAPMPreferences.getKeepScreenON(context);
        toggleButton = (ToggleButton)view.findViewById(R.id.KeepONToggleButton);
        toggleButton.setChecked(btnState);

        btnState = BAPMPreferences.getPriorityMode(context);
        toggleButton = (ToggleButton)view.findViewById(R.id.PriorityToggleButton);
        toggleButton.setChecked(btnState);

        btnState = BAPMPreferences.getMaxVolume(context);
        toggleButton = (ToggleButton)view.findViewById(R.id.VolumeMAXToggleButton);
        toggleButton.setChecked(btnState);

        btnState = BAPMPreferences.getLaunchMusicPlayer(context);
        toggleButton = (ToggleButton)view.findViewById(R.id.LaunchMusicPlayerToggleButton);
        toggleButton.setChecked(btnState);

        btnState = BAPMPreferences.getUnlockScreen(context);
        toggleButton = (ToggleButton)view.findViewById(R.id.UnlockToggleButton);
        toggleButton.setChecked(btnState);

        try {
            if(!installedMediaPlayers.isEmpty()) {
                RadioGroup rdoGroup = (RadioGroup) view.findViewById(R.id.rdoMusicPlayers);
                int index = getRadioButtonIndex();
                RadioButton radioButton = (RadioButton) rdoGroup.getChildAt(index);
                radioButton.setChecked(true);
            } else {
                LinearLayout musicPlayersLL = (LinearLayout)view.findViewById(R.id.MusicPlayers);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
                musicPlayersLL.setLayoutParams(layoutParams);
                TextView textView = new TextView(context);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                textView.setText("No music players found");
                musicPlayersLL.addView(textView);
            }
        }catch (Exception e){
            String error = e.getMessage() == null ? "RadioButton Error" : e.getMessage();
            Log.e(TAG, error);
        }
    }

    private int getRadioButtonIndex(){
        String selectedMusicPlayer = BAPMPreferences.getPkgSelectedMusicPlayer(getContext());
        return installedMediaPlayers.indexOf(selectedMusicPlayer);
    }

    public void autoplayOnlyButton(View view){
        Button autoplayOnlyButton = (Button)view.findViewById(R.id.autoplay_only_button);
        autoplayOnlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Display message that Apple Music not supported by autoplay only
                if(BAPMPreferences.getPkgSelectedMusicPlayer(getContext()).equals(PackageTools.PackageName.APPLEMUSIC)){
                    Toast.makeText(getContext(), "Autoplay ONLY not supported with Apple Music", Toast.LENGTH_LONG).show();
                    return;
                }

                mFirebaseHelper.selectionMade(FirebaseHelper.Selection.SET_AUTOPLAY_ONLY);
                DialogFragment newFragment = HeadphonesFragment.newInstance();
                newFragment.show(getActivity().getSupportFragmentManager(), "autoplayOnlyFragment");
            }
        });
    }

    //***Toggle button actions are below, basically set SharedPref value for specified button***
    public void mapsToggleButton(View view, final Context context){
        Button mapsToggleButton = (Button)view.findViewById(R.id.MapsToggleButton);
        mapsToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((ToggleButton) view).isChecked();
                mFirebaseHelper.featureEnabled(FirebaseHelper.Feature.LAUNCH_MAPS, on);
                if (on) {
                    BAPMPreferences.setLaunchGoogleMaps(context, true);
                    Log.i(TAG, "MapButton is ON");
                    Log.i(TAG, "Dismiss Keyguard is ON");
                } else {
                    BAPMPreferences.setLaunchGoogleMaps(context, false);
                    Log.d(TAG, "MapButton is OFF");
                }
            }
        });
    }

    public void keepONToggleButton(View view, final Context context){
        Button keepOnToggleButton = (Button)view.findViewById(R.id.KeepONToggleButton);
        keepOnToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((ToggleButton) view).isChecked();
                mFirebaseHelper.featureEnabled(FirebaseHelper.Feature.KEEP_SCREEN_ON, on);
                if (on) {
                    BAPMPreferences.setKeepScreenON(context, true);
                    Log.d(TAG, "Keep Screen ON Button is ON");
                } else {
                    BAPMPreferences.setKeepScreenON(context, false);
                    Log.d(TAG, "Keep Screen ON Button is OFF");
                }
            }
        });
    }

    public void priorityToggleButton(View view, final Context context){
        Button priorityToggleButton = (Button)view.findViewById(R.id.PriorityToggleButton);
        priorityToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((ToggleButton) view).isChecked();
                mFirebaseHelper.featureEnabled(FirebaseHelper.Feature.PRIORITY_MODE, on);
                if(on) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        PermissionHelper.checkDoNotDisturbPermission(context, 0);
                    }
                    BAPMPreferences.setPriorityMode(context, true);
                    Log.d(TAG, "Priority Button is ON");
                } else {
                    BAPMPreferences.setPriorityMode(context, false);
                    Log.d(TAG, "Priority Button is OFF");
                }
            }
        });
    }

    public void volumeMAXToggleButton(View view, final Context context){
        Button volumeMAXToggleButton = (Button)view.findViewById(R.id.VolumeMAXToggleButton);
        volumeMAXToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((ToggleButton) view).isChecked();
                mFirebaseHelper.featureEnabled(FirebaseHelper.Feature.MAX_VOLUME, on);
                if (on) {
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        PermissionHelper.checkDoNotDisturbPermission(context, 0);
                    }
                    BAPMPreferences.setMaxVolume(context, true);
                    Log.d(TAG, "Max Volume Button is ON");
                } else {
                    BAPMPreferences.setMaxVolume(context, false);
                        Log.d(TAG, "Max Volume Button is OFF");
                }
            }
        });
    }

    public void launchMusicPlayerToggleButton(View view, final Context context){
        Button launchMusicPlayerToggleButton = (Button)view.findViewById(R.id.LaunchMusicPlayerToggleButton);
        launchMusicPlayerToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((ToggleButton) view).isChecked();
                mFirebaseHelper.featureEnabled(FirebaseHelper.Feature.LAUNCH_MUSIC_PLAYER, on);
                if (on) {
                    BAPMPreferences.setLaunchMusicPlayer(context, true);
                    Log.d(TAG, "Launch Music Player Button is ON");
                } else {
                    BAPMPreferences.setLaunchMusicPlayer(context, false);
                    Log.d(TAG, "Launch Music Player Button is OFF");
                }
            }
        });
    }

    public void unlockScreenToggleButton(View view, final Context context){
        Button unlockScreenToggleButton = (Button)view.findViewById(R.id.UnlockToggleButton);
        unlockScreenToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((ToggleButton) view).isChecked();
                mFirebaseHelper.featureEnabled(FirebaseHelper.Feature.DISMISS_KEYGUARD, on);
                if (on) {
                    BAPMPreferences.setUnlockScreen(context, true);
                    Log.i(TAG, "Dismiss KeyGuard Button is ON");
                } else {
                    BAPMPreferences.setUnlockScreen(context, false);
                    Log.i(TAG, "Dismiss KeyGuard Button is OFF");
                }
            }
        });
    }

    private void setFonts(View view, Context context){
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/TitilliumText400wt.otf");
        Typeface typeface_bold = Typeface.createFromAsset(context.getAssets(), "fonts/TitilliumText600wt.otf");

        TextView textView = (TextView)view.findViewById(R.id.textView);
        textView.setTypeface(typeface_bold);

        textView = (TextView)view.findViewById(R.id.textView2);
        textView.setTypeface(typeface_bold);

        textView = (TextView)view.findViewById(R.id.textView3);
        textView.setTypeface(typeface_bold);

        textView = (TextView)view.findViewById(R.id.textView4);
        textView.setTypeface(typeface);

        textView = (TextView)view.findViewById(R.id.textView5);
        textView.setTypeface(typeface);

        textView = (TextView)view.findViewById(R.id.textView6);
        textView.setTypeface(typeface);

        textView = (TextView)view.findViewById(R.id.textView7);
        textView.setTypeface(typeface);

        textView = (TextView)view.findViewById(R.id.textView8);
        textView.setTypeface(typeface);

        textView = (TextView)view.findViewById(R.id.textView9);
        textView.setTypeface(typeface);

    }

    //List of Mediaplayers that is installed on the phone
    private List<String> listOfInstalledMediaPlayers(){
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        List<ResolveInfo> pkgAppsList = getContext().getPackageManager().queryBroadcastReceivers(intent, 0);
        List<String> installedMediaPlayers = new ArrayList<>();

        for(ResolveInfo ri:pkgAppsList){
            String resolveInfo = ri.toString();
            if(resolveInfo.contains("pandora")
                    || resolveInfo.contains(".playback")
                    || resolveInfo.contains("music")
                    || resolveInfo.contains("Music")
                    || resolveInfo.contains("audioplayer")
                    || resolveInfo.contains("mobi.beyondpod")
                    || resolveInfo.contains("au.com.shiftyjelly.pocketcasts")) {
                String[] resolveInfoSplit = resolveInfo.split(" ");
                String pkg = resolveInfoSplit[1].substring(0, resolveInfoSplit[1].indexOf("/"));
                if (!installedMediaPlayers.contains(pkg)) {
                    installedMediaPlayers.add(pkg);
                }
            }
        }

        return installedMediaPlayers;
    }
}
