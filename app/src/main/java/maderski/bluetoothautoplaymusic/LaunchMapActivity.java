package maderski.bluetoothautoplaymusic;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LaunchMapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_map);

        String mapAppName = BAPMPreferences.getMapsChoice(this);
        if(mapAppName.equals("com.waze")){
            if(LaunchApp.checkPkgOnPhone(this, "com.waze")){
                launchMapApp(mapAppName);
            }
        }else{
            launchMapApp(mapAppName);
        }
        this.finish();
    }

    private void launchMapApp(String mapAppName){
        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage(mapAppName);
        startActivity(LaunchIntent);
    }
}
