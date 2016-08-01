package maderski.bluetoothautoplaymusic;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private Set<String> saveBTDevices = new HashSet<String>();
    private boolean isBTConnected = false;
    private LaunchApp launchApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context context = this;

        if(BAPMPreferences.getFirstInstallKey(this))
            runOnFirstInstall();

        checkLocationPermission();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //When Floating Action Button is clicked show snackbar with MAPS/WAZE selection
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String mapApp = BAPMPreferences.getMapsChoice(context);
                String mapAppName = launchApp.getMapAppName(mapApp);
                //Checks if waze is on the phone
                boolean wazeFound = launchApp.checkPkgOnPhone(PackageTools.WAZE);

                if (wazeFound) {
                    Snackbar.make(view, "Change Maps Launch to", Snackbar.LENGTH_LONG)
                            .setActionTextColor(getResources().getColor(R.color.colorAccent))
                            .setAction(mapAppName, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (mapApp.equals(PackageTools.WAZE)) {
                                        BAPMPreferences.setMapsChoice(context, PackageTools.MAPS);
                                        Toast.makeText(context, "Changed to GOOGLE MAPS", Toast.LENGTH_LONG).show();
                                    }
                                    else {
                                        BAPMPreferences.setMapsChoice(context, PackageTools.WAZE);
                                        Toast.makeText(context, "Changed to WAZE", Toast.LENGTH_LONG).show();
                                    }
                                    if(BuildConfig.DEBUG)
                                        Log.i(TAG, "Maps set to: " + BAPMPreferences.getMapsChoice(context));
                                    setMapsButtonText(context);
                                }
                            }).show();
                } else {
                    Snackbar.make(view, "Supports Launching of WAZE when installed", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //Display about when the three dots is clicked on
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.about_menu) {
            aboutSelected();
            return true;
        } else if (id == R.id.settings_menu) {
            settingsSelected();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Launches the AboutActivity when about is selected
    private void aboutSelected(){
        final View view = findViewById(R.id.toolbar);

        Snackbar.make(view, "Created by: Jason Maderski" + "\n" + "Version: " + showVersion(), Snackbar.LENGTH_LONG).show();
    }

    private void settingsSelected(){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    //Show Version of the BAPM App
    private String showVersion(){
        String version = "none";

        try {
            PackageInfo pkgInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pkgInfo.versionName;
        }catch(Exception e){
            Log.e(TAG, e.getMessage());
        }

        return version;
    }

    @Override
    protected void onResume(){
        super.onResume();
        setupUIElements(this);
        launchApp = new LaunchApp(this);
        checkIfWazeRemoved(this);
        isBTConnected = Singleton.getInstance().getRanActionsOnBTConnect();
    }

    //Save the BTDevices when program is paused
    @Override
    protected  void onPause(){
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    //Checks if WAZE was removed and if WAZE was set to the MapsChoice and if so, set MapsChoice in
    //SharedPrefs to MAPS
    private void checkIfWazeRemoved(Context context){
        String mapAppChoice = BAPMPreferences.getMapsChoice(context);
        if(mapAppChoice.equalsIgnoreCase(PackageTools.WAZE)) {
            if (!launchApp.checkPkgOnPhone(PackageTools.WAZE)) {
                if(BuildConfig.DEBUG)
                    Log.i(TAG, "Checked");
                BAPMPreferences.setMapsChoice(this, PackageTools.MAPS);
            }else {
                if (BuildConfig.DEBUG)
                    Log.i(TAG, "WAZE is installed");
            }
        }
    }

    //On initial install so saveBTdevices is not null
    private void runOnFirstInstall(){
        Set<String> firstRun = new HashSet<>();
        saveBTDevices = new HashSet<>(VariousLists.listOfBluetoothDevices());

        //firstRun.add(saveBTDevices.iterator().next());

        BAPMPreferences.setBTDevices(this, firstRun);
        BAPMPreferences.setFirstInstall(this, false);
    }

    //Used for testing, Lists Music players and BT devices in logcat
    private void listMusicplayersAndBTDevices(Context context){
        if(BuildConfig.DEBUG) {
            for (String pkg : VariousLists.listOfInstalledMediaPlayers(context)) {
                Log.i("Pkg ", pkg);
            }

            for (String btDevice : VariousLists.listOfBluetoothDevices()) {
                Log.i("BTDevice ", btDevice);
            }
        }
    }

    //Setup the UI
    private void setupUIElements(Context context){
        setFonts();
        radiobuttonCreator(context);
        checkboxCreator();
        setButtonPreferences(context);
        radioButtonListener();
        setMapsButtonText(context);
    }

    //Create Checkboxes
    private void checkboxCreator() {

        CheckBox checkBox;
        TextView textView;

        LinearLayout BTDeviceCkBoxLL = (LinearLayout) findViewById(R.id.checkBoxLL);
        BTDeviceCkBoxLL.removeAllViews();

        if (VariousLists.listOfBluetoothDevices().contains("No Bluetooth Device found") ||
                VariousLists.listOfBluetoothDevices().isEmpty()){
            textView = new TextView(this);
            textView.setText(R.string.no_BT_found);
            BTDeviceCkBoxLL.addView(textView);
        }else{
            for (String BTDevice : VariousLists.listOfBluetoothDevices()) {
                checkBox = new CheckBox(this);
                checkBox.setText(BTDevice);
                checkBox.setTextColor(getResources().getColor(R.color.colorPrimary));
                checkBox.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/TitilliumText400wt.otf"));
                if(BAPMPreferences.getBTDevices(this) != null)
                    checkBox.setChecked(BAPMPreferences.getBTDevices(this).contains(BTDevice));
                checkboxListener(this, checkBox, BTDevice);
                BTDeviceCkBoxLL.addView(checkBox);
            }
        }

    }

    //Used for testing to list BTDevices in logcat
    private void listSetString(){
        for(String item : BAPMPreferences.getBTDevices(this)){
            Log.i(TAG, item);
            Log.i(TAG, Integer.toString(BAPMPreferences.getBTDevices(this).size()));
        }
    }

    //Get Selected Checkboxes
    private void checkboxListener(Context context, CheckBox checkBox, String BTDevice){
        final CheckBox cb = checkBox;
        final String BTD = BTDevice;
        final Context ctx = context;

        saveBTDevices = new HashSet<String>(BAPMPreferences.getBTDevices(this));

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isBTConnected) {
                    if (cb.isChecked()) {
                        saveBTDevices.add(BTD);
                        if(BuildConfig.DEBUG)
                            Log.i(TAG, "TRUE" + " " + BTD);
                        BAPMPreferences.setBTDevices(ctx, saveBTDevices);
                        if(BuildConfig.DEBUG)
                            Log.i(TAG, "SAVED");
                    } else {
                        saveBTDevices.remove(BTD);
                        if(BuildConfig.DEBUG)
                            Log.i(TAG, "FALSE" + " " + BTD);
                        BAPMPreferences.setBTDevices(ctx, saveBTDevices);
                        if(BuildConfig.DEBUG)
                            Log.i(TAG, "SAVED");
                    }
                }else{
                    cb.toggle();
                    Snackbar.make(v, "Checkboxes are disabled while connected to Bluetooth Device", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    //Get list of installed Mediaplayers and create Radiobuttons
    private void radiobuttonCreator(Context context){

        RadioButton rdoButton;
        ApplicationInfo appInfo;
        String mediaPlayer = "No Name";
        PackageManager pm = getPackageManager();

        RadioGroup rdoMPGroup = (RadioGroup) findViewById(R.id.rdoMusicPlayers);
        rdoMPGroup.removeAllViews();

        for(String packageName : VariousLists.listOfInstalledMediaPlayers(context)){

            try{
                appInfo = pm.getApplicationInfo(packageName, 0);
                mediaPlayer = pm.getApplicationLabel(appInfo).toString();

            }catch (Exception e){
                Log.e(TAG, e.getMessage());
            }

            rdoButton = new RadioButton(this);
            rdoButton.setText(mediaPlayer);
            rdoButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            rdoButton.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/TitilliumText400wt.otf"));
            rdoMPGroup.addView(rdoButton);
        }
    }

    //Get Selected Radiobutton
    private void radioButtonListener(){
        final Context context = this;
        RadioGroup group = (RadioGroup) findViewById(R.id.rdoMusicPlayers);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(radioButton);
                BAPMPreferences.setSelectedMusicPlayer(context, index);
                //Log.i(TAG, Integer.toString(index));
                //Log.i(TAG, Integer.toString(BAPMPreferences.getSelectedMusicPlayer(context)));
                //Log.i(TAG, Integer.toString(radioGroup.getCheckedRadioButtonId()));
            }
        });
    }

    private void checkLocationPermission() {
        PackageManager packageManager = getPackageManager();
        int hasPermission = packageManager.checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
                getPackageName());
        //Check if Permission is granted
        if(hasPermission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PackageManager.PERMISSION_GRANTED);
        }
    }

    //Change the Maps button text to Maps or Waze depending on what Maps the user is launching
    private void setMapsButtonText(Context context){
        String mapChoice = BAPMPreferences.getMapsChoice(context);
        PackageManager packageManager = getPackageManager();
        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(mapChoice, 0);
            mapChoice = packageManager.getApplicationLabel(appInfo).toString();
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
            mapChoice = "Maps";
        }

        TextView textView = (TextView)findViewById(R.id.textView4);
        textView.setText("Launch " + mapChoice);
    }

    //Set the button and radiobutton states
    private void setButtonPreferences(Context context){
        Boolean btnState;
        ToggleButton toggleButton;

        btnState = BAPMPreferences.getLaunchGoogleMaps(context);
        toggleButton = (ToggleButton)findViewById(R.id.MapsToggleButton);
        toggleButton.setChecked(btnState);

        btnState = BAPMPreferences.getKeepScreenON(context);
        toggleButton = (ToggleButton)findViewById(R.id.KeepONToggleButton);
        toggleButton.setChecked(btnState);

        btnState = BAPMPreferences.getPriorityMode(context);
        toggleButton = (ToggleButton)findViewById(R.id.PriorityToggleButton);
        toggleButton.setChecked(btnState);

        btnState = BAPMPreferences.getMaxVolume(context);
        toggleButton = (ToggleButton)findViewById(R.id.VolumeMAXToggleButton);
        toggleButton.setChecked(btnState);

        btnState = BAPMPreferences.getLaunchMusicPlayer(context);
        toggleButton = (ToggleButton)findViewById(R.id.LaunchMusicPlayerToggleButton);
        toggleButton.setChecked(btnState);

        btnState = BAPMPreferences.getUnlockScreen(context);
        toggleButton = (ToggleButton)findViewById(R.id.UnlockToggleButton);
        toggleButton.setChecked(btnState);

        try {
            RadioGroup rdoGroup = (RadioGroup) findViewById(R.id.rdoMusicPlayers);
            int index = BAPMPreferences.getSelectedMusicPlayer(context);
            RadioButton radioButton = (RadioButton) rdoGroup.getChildAt(index);
            radioButton.setChecked(true);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    //***Toggle button actions are below, basically set SharedPref value for specified button***

    public void mapsToggleButton(View view){
        boolean on = ((ToggleButton) view).isChecked();
        if (on) {
            BAPMPreferences.setLaunchGoogleMaps(this, true);
            BAPMPreferences.setUnlockScreen(this, true);
            setButtonPreferences(this);
            if(BuildConfig.DEBUG) {
                Log.i(TAG, "MapButton is ON");
                Log.i(TAG, "Dismiss Keyguard is ON");
            }
        } else {
            BAPMPreferences.setLaunchGoogleMaps(this, false);
            if(BuildConfig.DEBUG)
                Log.i(TAG, "MapButton is OFF");
        }
    }

    public void keepONToggleButton(View view){
        boolean on = ((ToggleButton) view).isChecked();
        if(!isBTConnected) {
            if (on) {
                BAPMPreferences.setKeepScreenON(this, true);
                if(BuildConfig.DEBUG)
                    Log.i(TAG, "Keep Screen ON Button is ON");
            } else {
                BAPMPreferences.setKeepScreenON(this, false);
                if(BuildConfig.DEBUG)
                    Log.i(TAG, "Keep Screen ON Button is OFF");
            }
        }else {
            ((ToggleButton) view).toggle();
            Snackbar.make(view, "Button disabled while connected to Bluetooth Device", Snackbar.LENGTH_LONG).show();
        }
    }

    public void priorityToggleButton(View view){
        boolean on = ((ToggleButton) view).isChecked();
        if(!isBTConnected) {
            if (on) {
                BAPMPreferences.setPriorityMode(this, true);
                if(BuildConfig.DEBUG)
                    Log.i(TAG, "Priority Button is ON");
            } else {
                BAPMPreferences.setPriorityMode(this, false);
                if(BuildConfig.DEBUG)
                    Log.i(TAG, "Priority Button is OFF");
            }
        }else {
            ((ToggleButton) view).toggle();
            Snackbar.make(view, "Button disabled while connected to Bluetooth Device", Snackbar.LENGTH_LONG).show();
        }
    }

    public void volumeMAXToggleButton(View view){
        boolean on = ((ToggleButton) view).isChecked();
        if(!isBTConnected) {
            if (on) {
                BAPMPreferences.setMaxVolume(this, true);
                if(BuildConfig.DEBUG)
                    Log.i(TAG, "Max Volume Button is ON");
            } else {
                BAPMPreferences.setMaxVolume(this, false);
                if(BuildConfig.DEBUG)
                    Log.i(TAG, "Max Volume Button is OFF");
            }
        }else {
            ((ToggleButton) view).toggle();
            Snackbar.make(view, "Button disabled while connected to Bluetooth Device", Snackbar.LENGTH_LONG).show();
        }
    }

    public void launchMusicPlayerToggleButton(View view){
        boolean on = ((ToggleButton) view).isChecked();
        if(!isBTConnected) {
            if (on) {
                BAPMPreferences.setLaunchMusicPlayer(this, true);
                if(BuildConfig.DEBUG)
                    Log.i(TAG, "Launch Music Player Button is ON");
            } else {
                BAPMPreferences.setLaunchMusicPlayer(this, false);
                if(BuildConfig.DEBUG)
                    Log.i(TAG, "Launch Music Player Button is OFF");
            }
        }else {
            ((ToggleButton) view).toggle();
            Snackbar.make(view, "Button disabled while connected to Bluetooth Device", Snackbar.LENGTH_LONG).show();
        }
    }

    public void unlockScreenToggleButton(View view){
        boolean on = ((ToggleButton) view).isChecked();
        if (on) {
            BAPMPreferences.setUnlockScreen(this, true);
            if(BuildConfig.DEBUG)
                Log.i(TAG, "Dismiss KeyGuard Button is ON");
        } else {
            BAPMPreferences.setUnlockScreen(this, false);
            if(BuildConfig.DEBUG)
                Log.i(TAG, "Dismiss KeyGuard Button is OFF");
        }
    }

    private void setFonts(){
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/TitilliumText400wt.otf");
        Typeface typeface_bold = Typeface.createFromAsset(getAssets(), "fonts/TitilliumText600wt.otf");

        TextView textView = (TextView)findViewById(R.id.textView);
        textView.setTypeface(typeface_bold);

        textView = (TextView)findViewById(R.id.textView2);
        textView.setTypeface(typeface_bold);

        textView = (TextView) findViewById(R.id.textView3);
        textView.setTypeface(typeface_bold);

        textView = (TextView)findViewById(R.id.textView4);
        textView.setTypeface(typeface);

        textView = (TextView)findViewById(R.id.textView5);
        textView.setTypeface(typeface);

        textView = (TextView) findViewById(R.id.textView6);
        textView.setTypeface(typeface);

        textView = (TextView)findViewById(R.id.textView7);
        textView.setTypeface(typeface);

        textView = (TextView)findViewById(R.id.textView8);
        textView.setTypeface(typeface);

        textView = (TextView)findViewById(R.id.textView9);
        textView.setTypeface(typeface);

    }

}
