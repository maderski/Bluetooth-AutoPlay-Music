package maderski.bluetoothautoplaymusic;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class LaunchMapActivity extends AppCompatActivity {

    final static String TAG = LaunchMapActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_map);

        String mapAppName = BAPMPreferences.getMapsChoice(this);
        Log.i(TAG, mapAppName);
        launchMapApp(mapAppName);
        this.finish();
    }

    private void launchMapApp(String mapAppName){
        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(mapAppName);
        startActivity(LaunchIntent);
    }
}
