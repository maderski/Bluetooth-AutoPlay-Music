package maderski.bluetoothautoplaymusic.UI;

import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import maderski.bluetoothautoplaymusic.Analytics.FirebaseHelper;
import maderski.bluetoothautoplaymusic.Interfaces.BluetoothState;
import maderski.bluetoothautoplaymusic.Services.BAPMService;
import maderski.bluetoothautoplaymusic.BluetoothDeviceHelper;
import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.LaunchApp;
import maderski.bluetoothautoplaymusic.PackageTools;
import maderski.bluetoothautoplaymusic.Permissions;
import maderski.bluetoothautoplaymusic.R;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMDataPreferences;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

public class MainActivity extends AppCompatActivity implements HeadphonesFragment.OnFragmentInteractionListener,
        TimePickerFragment.TimePickerDialogListener, WifiOffFragment.OnFragmentInteractionListener {

    private static final String TAG = MainActivity.class.getName();
    private static final String TAG_HOME_FRAGMENT = "home_fragment";
    private static final String TAG_MAPS_FRAGMENT = "maps_fragment";
    private static final String TAG_OPTIONS_FRAGMENT = "options_fragment";

    private FirebaseHelper mFirebaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mFirebaseHelper = new FirebaseHelper(this);
        mFirebaseHelper.activityLaunched(FirebaseHelper.ActivityName.MAIN);

        if(BAPMPreferences.getAutoBrightness(this)) {
            Permissions permissions = new Permissions();
            permissions.checkLocationPermission(this);
        }

        checkIfBAPMServiceRunning();

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, HomeFragment.newInstance(), TAG_HOME_FRAGMENT)
                .commit();

        final BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if(itemId == R.id.menu_home){
                    Fragment homeFragment = HomeFragment.newInstance();
                    handleNavigationSelection(homeFragment, TAG_HOME_FRAGMENT);
                    return true;
                } else if(itemId == R.id.menu_maps){
                  Fragment mapsFragment = MapsFragment.newInstance();
                    handleNavigationSelection(mapsFragment, TAG_MAPS_FRAGMENT);
                    return true;
                } else if(itemId == R.id.menu_options){
                    Fragment optionsFragment = OptionsFragment.newInstance();
                    handleNavigationSelection(optionsFragment, TAG_OPTIONS_FRAGMENT);
                    mFirebaseHelper.selectionMade(FirebaseHelper.Selection.OPTIONS);
                    return true;
                } else {
                    return false;
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
            mFirebaseHelper.selectionMade(FirebaseHelper.Selection.ABOUT);
            aboutSelected();
            return true;
        } else if (id == R.id.link_menu){
            mFirebaseHelper.selectionMade(FirebaseHelper.Selection.RATE_ME);
            linkSelected();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Launches the AboutActivity when about is selected
    private void aboutSelected(){
        final View bottomNavBar = findViewById(R.id.bottom_navigation);
        bottomNavBar.animate().alpha(0).start();
        final View view = findViewById(R.id.toolbar);
        Snackbar snackbar = Snackbar.make(view, "Created by: Jason Maderski" + "\n" + "Version: " + showVersion(), Snackbar.LENGTH_LONG);
        snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View view) {}

            @Override
            public void onViewDetachedFromWindow(View view) {
                bottomNavBar.animate().alpha(1).start();
            }
        });
        snackbar.show();

    }

    private void handleNavigationSelection(Fragment fragment, String fragmentTAG){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment, fragmentTAG)
                .commit();
    }

    private void linkSelected(){
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Bluetooth Autoplay Music");
        alertDialog.setMessage("Google Play Store location");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Launch Store",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("market://details?id=maderski.bluetoothautoplaymusic"));
                        startActivity(intent);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Copy Link",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager clipboard = (ClipboardManager)
                                getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("PlayStoreLink",
                                "https://play.google.com/store/apps/details?id=maderski.bluetoothautoplaymusic");
                        clipboard.setPrimaryClip(clip);
                        showClipboardToast();
                    }
                });
        alertDialog.show();
    }

    private void showClipboardToast(){
        Toast.makeText(this, "Play Store link copied to clipboard",
                Toast.LENGTH_LONG).show();
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

    //Starts BAPMService if it is not running
    private void checkIfBAPMServiceRunning(){
        if(!isServiceRunning(BAPMService.class)){
            Intent serviceIntent = new Intent(this, BAPMService.class);
            startService(serviceIntent);
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass){
        ActivityManager activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClass.getName())){
                return true;
            }
        }
        return false;
    }

    @Override
    public void onTimeSet(boolean isDim, TimePicker view, int hourOfDay, int minute) {
        int timeSet = (hourOfDay * 100) + minute;
        if(isDim){
            mFirebaseHelper.manualTimeSet(FirebaseHelper.Selection.DIM_TIME, true);
            BAPMPreferences.setDimTime(this, timeSet);
            Log.d("Settings", "Dim brightness");
        }else{
            mFirebaseHelper.manualTimeSet(FirebaseHelper.Selection.BRIGHT_TIME, true);
            BAPMPreferences.setBrightTime(this, timeSet);
            Log.d("Settings", "Bright brightness");
        }
    }

    @Override
    public void onTimeCancel(boolean isDim) {
        mFirebaseHelper.manualTimeSet(isDim ? FirebaseHelper.Selection.DIM_TIME : FirebaseHelper.Selection.BRIGHT_TIME, false);
    }

    @Override
    public void setHeadphoneDevices(HashSet<String> headphoneDevices) {
        BAPMPreferences.setHeadphoneDevices(getApplicationContext(), headphoneDevices);
        if(BuildConfig.DEBUG) {
            for (String deviceName : headphoneDevices) {
                Log.d(TAG, "device name: " + deviceName);
            }
        }
    }

    @Override
    public void headphonesDoneClicked(HashSet<String> removeDevices) {
        Set<String> headphoneDevices = BAPMPreferences.getHeadphoneDevices(this);
        Set<String> btDevices = BAPMPreferences.getBTDevices(this);

        for(String deviceName : removeDevices){
            if(headphoneDevices.contains(deviceName))
                headphoneDevices.remove(deviceName);
        }

        if(BuildConfig.DEBUG) {
            for (String deviceName : headphoneDevices) {
                Log.d(TAG, "saveBTDevice: " + deviceName);
            }
        }

        btDevices.addAll(headphoneDevices);
        BAPMPreferences.setBTDevices(this, btDevices);

        HomeFragment homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(TAG_HOME_FRAGMENT);
        if(homeFragment != null) {
            View view = homeFragment.getView();
            homeFragment.checkboxCreator(view, this);
        }
    }

    @Override
    public void headDeviceSelection(String deviceName, boolean addDevice) {
        if(mFirebaseHelper != null)
            mFirebaseHelper.deviceAdd(FirebaseHelper.Selection.HEADPHONE_DEVICE, deviceName, addDevice);
    }

    @Override
    public void setWifiOffDevices(HashSet<String> wifiOffDevices) {
        BAPMPreferences.setTurnWifiOffDevices(this, wifiOffDevices);
    }
}
