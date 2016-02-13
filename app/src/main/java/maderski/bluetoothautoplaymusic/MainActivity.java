package maderski.bluetoothautoplaymusic;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getName();
    private Set<String> saveBTDevices = new HashSet<String>();

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
                String mapAppName = "None";
                boolean wazeFound;

                if (LaunchApp.checkPkgOnPhone(context, ConstantStore.WAZE)) {
                    Log.i(TAG, "FOUND");
                    wazeFound = true;
                } else {
                    Log.i(TAG, "NOT FOUND");
                    wazeFound = false;
                }

                try {
                    ApplicationInfo appInfo = getPackageManager().getApplicationInfo(mapApp, 0);
                    mapAppName = getPackageManager().getApplicationLabel(appInfo).toString();
                    if (mapAppName.equalsIgnoreCase("MAPS")) {
                        mapAppName = "WAZE";
                    } else {
                        mapAppName = "GOOGLE MAPS";
                    }
                    VariableStore.toastMapApp = mapAppName;
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }

                if (wazeFound) {
                    Snackbar.make(view, "Change Maps Launch to", Snackbar.LENGTH_LONG)
                            .setActionTextColor(getResources().getColor(R.color.colorAccent))
                            .setAction(mapAppName, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (mapApp.equals(ConstantStore.WAZE))
                                        BAPMPreferences.setMapsChoice(context, ConstantStore.MAPS);
                                    else
                                        BAPMPreferences.setMapsChoice(context, ConstantStore.WAZE);
                                    Toast.makeText(context, "Changed to " + VariableStore.toastMapApp, Toast.LENGTH_LONG).show();
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
        if (id == R.id.action_settings) {
            aboutSelected();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Launches the AboutActivity when about is selected
    private void aboutSelected(){
        final View view = findViewById(R.id.toolbar);

        Snackbar.make(view, "Created by: Jason Maderski" + "\n" + "Version: " + showVersion(), Snackbar.LENGTH_LONG).show();
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

        if(BAPMPreferences.getUnlockScreen(this)){
            dismissKeyGuard(this);
        }

        checkIfWazeRemoved(this);
    }

    //Save the BTDevices when program is paused
    @Override
    protected  void onPause(){
        super.onPause();
        BAPMPreferences.setBTDevices(this, saveBTDevices);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    //Not used, check if BAPMService is Running
    private boolean isBAPMServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (BAPMService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    //Checks if WAZE was removed and if WAZE was set to the MapsChoice and if so, set MapsChoice in
    //SharedPrefs to MAPS
    private void checkIfWazeRemoved(Context context){
        String mapAppChoice = BAPMPreferences.getMapsChoice(context);
        if(mapAppChoice.equalsIgnoreCase(ConstantStore.WAZE)) {
            if (!LaunchApp.checkPkgOnPhone(this, ConstantStore.WAZE)) {
                Log.i(TAG, "Checked");
                BAPMPreferences.setMapsChoice(this, ConstantStore.MAPS);
            }else
                Log.i(TAG, "WAZE is installed");
        }
    }

    //Dismiss the KeyGuard
    private void dismissKeyGuard(Context context){

        if (!BAPMPreferences.getKeepScreenON(context)){
            ScreenONLock screenONLock = new ScreenONLock();
            screenONLock.enableWakeLock(context);
            Window window = ((Activity) context).getWindow();
            //window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            //window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            screenONLock.releaseWakeLock(context);
        }else{
            Window window = ((Activity) context).getWindow();
            //window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            //window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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

    //Not used
    private void sendAppToBackground(Context context){
        Intent i = new Intent();
        i.setAction(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(i);
    }

    //Used for testing, Lists Music players and BT devices in logcat
    private void listMusicplayersAndBTDevices(Context context){
        for(String pkg:VariousLists.listOfInstalledMediaPlayers(context)){
            Log.i("Pkg ", pkg);
        }

        for(String btDevice:VariousLists.listOfBluetoothDevices()){
            Log.i("BTDevice ", btDevice);
        }
    }

    //Setup the UI
    private void setupUIElements(Context context){
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
                if(BAPMPreferences.getBTDevices(this) != null)
                    checkBox.setChecked(BAPMPreferences.getBTDevices(this).contains(BTDevice));
                checkboxListener(checkBox, BTDevice);
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
    private void checkboxListener(CheckBox checkBox, String BTDevice){
        final CheckBox cb = checkBox;
        final String BTD = BTDevice;

        saveBTDevices = new HashSet<String>(BAPMPreferences.getBTDevices(this));

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!VariableStore.isBTConnected) {
                    if (cb.isChecked()) {
                        saveBTDevices.add(BTD);
                        Log.i(TAG, "TRUE" + " " + BTD);
                    } else {
                        saveBTDevices.remove(BTD);
                        Log.i(TAG, "FALSE" + " " + BTD);
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
        if(!VariableStore.isBTConnected) {
            if (on) {
                BAPMPreferences.setLaunchGoogleMaps(this, true);
                BAPMPreferences.setUnlockScreen(this, true);
                setButtonPreferences(this);
                Log.i(TAG, "MapButton is ON");
                Log.i(TAG, "Dismiss Keyguard is ON");
            } else {
                BAPMPreferences.setLaunchGoogleMaps(this, false);
                Log.i(TAG, "MapButton is OFF");
            }
        }else {
            ((ToggleButton) view).toggle();
            Snackbar.make(view, "Buttons are disabled while connected to Bluetooth Device", Snackbar.LENGTH_LONG).show();
        }
    }

    public void keepONToggleButton(View view){
        boolean on = ((ToggleButton) view).isChecked();
        if(!VariableStore.isBTConnected) {
            if (on) {
                BAPMPreferences.setKeepScreenON(this, true);
                Log.i(TAG, "Keep Screen ON Button is ON");
            } else {
                BAPMPreferences.setKeepScreenON(this, false);
                Log.i(TAG, "Keep Screen ON Button is OFF");
            }
        }else {
            ((ToggleButton) view).toggle();
            Snackbar.make(view, "Buttons are disabled while connected to Bluetooth Device", Snackbar.LENGTH_LONG).show();
        }
    }

    public void priorityToggleButton(View view){
        boolean on = ((ToggleButton) view).isChecked();
        if(!VariableStore.isBTConnected) {
            if (on) {
                BAPMPreferences.setPriorityMode(this, true);
                Log.i(TAG, "Priority Button is ON");
            } else {
                BAPMPreferences.setPriorityMode(this, false);
                Log.i(TAG, "Priority Button is OFF");
            }
        }else {
            ((ToggleButton) view).toggle();
            Snackbar.make(view, "Buttons are disabled while connected to Bluetooth Device", Snackbar.LENGTH_LONG).show();
        }
    }

    public void volumeMAXToggleButton(View view){
        boolean on = ((ToggleButton) view).isChecked();
        if(!VariableStore.isBTConnected) {
            if (on) {
                BAPMPreferences.setMaxVolume(this, true);
                Log.i(TAG, "Max Volume Button is ON");
            } else {
                BAPMPreferences.setMaxVolume(this, false);
                Log.i(TAG, "Max Volume Button is OFF");
            }
        }else {
            ((ToggleButton) view).toggle();
            Snackbar.make(view, "Buttons are disabled while connected to Bluetooth Device", Snackbar.LENGTH_LONG).show();
        }
    }

    public void launchMusicPlayerToggleButton(View view){
        boolean on = ((ToggleButton) view).isChecked();
        if(!VariableStore.isBTConnected) {
            if (on) {
                BAPMPreferences.setLaunchMusicPlayer(this, true);
                Log.i(TAG, "Launch Music Player Button is ON");
            } else {
                BAPMPreferences.setLaunchMusicPlayer(this, false);
                Log.i(TAG, "Launch Music Player Button is OFF");
            }
        }else {
            ((ToggleButton) view).toggle();
            Snackbar.make(view, "Buttons are disabled while connected to Bluetooth Device", Snackbar.LENGTH_LONG).show();
        }
    }

    public void unlockScreenToggleButton(View view){
        boolean on = ((ToggleButton) view).isChecked();
        if(!VariableStore.isBTConnected) {
            if (on) {
                BAPMPreferences.setUnlockScreen(this, true);
                Log.i(TAG, "Dismiss KeyGuard Button is ON");
            } else {
                BAPMPreferences.setUnlockScreen(this, false);
                Log.i(TAG, "Dismiss KeyGuard Button is OFF");
            }
        }else {
            ((ToggleButton) view).toggle();
            Snackbar.make(view, "Buttons are disabled while connected to Bluetooth Device", Snackbar.LENGTH_LONG).show();
        }
    }

}
