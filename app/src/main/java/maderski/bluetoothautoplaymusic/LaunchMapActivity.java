package maderski.bluetoothautoplaymusic;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class LaunchMapActivity extends AppCompatActivity {

    private static final String TAG = LaunchMapActivity.class.getName();

    //Get Map App and then run launchMapApp and close activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_map);

        String mapAppName = BAPMPreferences.getMapsChoice(this);
        Log.i(TAG, mapAppName);
        launchMapApp(mapAppName);
        this.finish();
    }

    //Launch Maps or Waze
    private void launchMapApp(String mapAppName){
        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(mapAppName);
        startActivity(LaunchIntent);
    }
}
