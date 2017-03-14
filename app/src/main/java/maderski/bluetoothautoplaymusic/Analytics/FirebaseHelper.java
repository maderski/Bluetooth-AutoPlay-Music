package maderski.bluetoothautoplaymusic.Analytics;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Jason on 1/28/17.
 */

public class FirebaseHelper {
    private static final String TAG = "FirebaseHelper";

    @StringDef({
            Feature.LAUNCH_MAPS,
            Feature.KEEP_SCREEN_ON,
            Feature.PRIORITY_MODE,
            Feature.MAX_VOLUME,
            Feature.LAUNCH_MAPS,
            Feature.LAUNCH_MUSIC_PLAYER,
            Feature.DISMISS_KEYGUARD
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Feature {
        String LAUNCH_MAPS = "launch_maps";
        String KEEP_SCREEN_ON = "keep_screen_on";
        String PRIORITY_MODE = "priority_mode";
        String MAX_VOLUME = "max_volume";
        String LAUNCH_MUSIC_PLAYER = "launch_music_player";
        String DISMISS_KEYGUARD = "dismiss_keyguard";
    }

    @StringDef({
            Option.AUTO_BRIGHTNESS,
            Option.CALL_COMPLETED,
            Option.GO_HOME,
            Option.PLAY_MUSIC,
            Option.POWER_REQUIRED,
            Option.MAX_VOLUME_SET,
            Option.HEADPHONE_VOLUME_SET
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Option {
        String PLAY_MUSIC = "play_music";
        String POWER_REQUIRED = "power_required";
        String GO_HOME = "go_home";
        String CALL_COMPLETED = "call_completed";
        String AUTO_BRIGHTNESS = "auto_brightness";
        String MAX_VOLUME_SET = "max_volume_set";
        String HEADPHONE_VOLUME_SET = "headphone_volume_set";
    }

    @StringDef({
            Selection.ABOUT,
            Selection.BRIGHT_TIME,
            Selection.DIM_TIME,
            Selection.MAPS_WAZE_SELECTOR,
            Selection.OPTIONS,
            Selection.RATE_ME,
            Selection.SET_AUTOPLAY_ONLY,
            Selection.BLUETOOTH_DEVICE,
            Selection.HEADPHONE_DEVICE,
            Selection.SET_WIFI_OFF_DEVICE,
            Selection.MORNING_START_TIME,
            Selection.MORNING_END_TIME,
            Selection.EVENING_START_TIME,
            Selection.EVENING_END_TIME
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface Selection {
        String SET_AUTOPLAY_ONLY = "set_autoplay_only";
        String MAPS_WAZE_SELECTOR = "maps_waze_selector";
        String OPTIONS = "option";
        String RATE_ME = "rate_me";
        String ABOUT = "about";
        String BRIGHT_TIME = "bright_time";
        String DIM_TIME = "dim_time";
        String BLUETOOTH_DEVICE = "bluetooth_device";
        String HEADPHONE_DEVICE = "headphone_device";
        String SET_WIFI_OFF_DEVICE = "set_wifi_off_device";
        String MORNING_START_TIME = "morning_start_time";
        String MORNING_END_TIME = "morning_end_time";
        String EVENING_START_TIME = "evening_start_time";
        String EVENING_END_TIME = "evening_end_time";
    }

    @StringDef({
            ActivityName.MAIN,
            ActivityName.SETTINGS,
            ActivityName.LAUNCH_BAPM
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface ActivityName {
        String MAIN = "main_activity";
        String SETTINGS = "settings_activity";
        String LAUNCH_BAPM = "dismiss_keyguard_activity";
    }

    @StringDef({

    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface BTActionsLaunch {
        String TELEPHONE = "off_telephone_launch";
        String POWER = "power_plugged_in_launch";
        String BLUETOOTH = "bluetooth_connected_launch";
    }

    private FirebaseAnalytics mFirebaseAnalytics;
    private Activity mActivity;

    public FirebaseHelper(Context context){
        if(context instanceof Activity){
            mActivity = (Activity)context;
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    public void featureEnabled(String featureName, boolean isEnabled){
        Bundle bundle = new Bundle();
        bundle.putInt(FirebaseAnalytics.Param.VALUE, isEnabled ? 1 : 0);
        mFirebaseAnalytics.logEvent(featureName, bundle);
    }

    public void selectionMade(String selection){
        mFirebaseAnalytics.logEvent(selection, null);
    }

    public void useGoogleMaps(){
        mFirebaseAnalytics.logEvent("google_maps", null);
    }

    public void useWaze(){
        mFirebaseAnalytics.logEvent("waze", null);
    }

    public void timeSetSelected(@Selection String selection, boolean wasSet){
        Bundle bundle = new Bundle();
        bundle.putInt(FirebaseAnalytics.Param.VALUE, wasSet ? 1 : 0);
        mFirebaseAnalytics.logEvent(selection, bundle);
    }

    public void deviceAdd(String typeOfDevice, String deviceName, boolean addDevice){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, deviceName);
        bundle.putInt(FirebaseAnalytics.Param.VALUE, addDevice ? 1 : 0);
        mFirebaseAnalytics.logEvent(typeOfDevice, bundle);
    }

    public void musicPlayerChoice(String packageName, boolean musicChoiceChanged){
        try{
            PackageManager packageManager = mActivity.getPackageManager();
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packageName, 0);
            String musicPlayerName = packageManager.getApplicationLabel(appInfo).toString();

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, musicPlayerName);
            bundle.putInt(FirebaseAnalytics.Param.VALUE, musicChoiceChanged ? 1 : 0);
//            mFirebaseAnalytics.logEvent("music_player_selected", bundle);
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }catch (Exception e){
            Log.e(TAG, e.getMessage());
        }
    }

    public void musicAutoPlay(boolean success){
        Bundle bundle = new Bundle();
        bundle.putInt(FirebaseAnalytics.Param.VALUE, success ? 1 : 0);
        mFirebaseAnalytics.logEvent("auto_play", bundle);
    }

    public void wakelockRehold(){
        mFirebaseAnalytics.logEvent("wakelock_rehold", null);
    }

    public void connectViaA2DP(String deviceName, boolean success){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, deviceName);
        bundle.putInt(FirebaseAnalytics.Param.VALUE, success ? 1 :0);
        mFirebaseAnalytics.logEvent("connect_via_a2dp", bundle);
    }

    public void activityLaunched(String activityName){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, activityName);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
    }

    public void bluetoothActionLaunch(String launchEventTrigger){
        mFirebaseAnalytics.logEvent(launchEventTrigger, null);
    }
}
