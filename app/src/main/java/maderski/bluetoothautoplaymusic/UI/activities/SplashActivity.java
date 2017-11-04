package maderski.bluetoothautoplaymusic.UI.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import maderski.bluetoothautoplaymusic.UI.activities.MainActivity;

/**
 * Created by Jason on 8/28/16.
 */
public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
