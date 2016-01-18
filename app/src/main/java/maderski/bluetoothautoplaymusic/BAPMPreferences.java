package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jason on 1/5/16.
 */
public class BAPMPreferences {

    private static SharedPreferences.Editor _editor;

    public static String MY_PREFS_NAME = "BAPMPreference";

    public static final String LAUNCH_MAPS_KEY =  "googleMaps";
    public static final String KEEP_SCREEN_ON_KEY = "keepScreenON";
    public static final String PRIORITY_MODE_KEY = "priorityMode";
    public static final String MAX_VOLUME_KEY = "maxVolume";
    public static final String LAUNCH_MUSIC_PLAYER_KEY = "launchMusic";
    public static final String BTDEVICE_TOAST_MSG_KEY = "BTDeviceToastMsg";
    public static final String SELECTED_MUSIC_PLAYER_KEY = "SelectedMusicPlayer";

    private static SharedPreferences.Editor editor(Context context){

        if(_editor == null){
            _editor = context.getSharedPreferences(MY_PREFS_NAME, context.MODE_MULTI_PROCESS).edit();
            _editor.commit();
        }

        return _editor;
    }

    private static SharedPreferences reader(Context context){

        return context.getSharedPreferences(MY_PREFS_NAME, context.MODE_MULTI_PROCESS);
    }

    public static void setLaunchGoogleMaps(Context context, boolean enabled){
        editor(context).putBoolean(LAUNCH_MAPS_KEY, enabled);
        commit(context);
    }

    public static boolean getLaunchGoogleMaps(Context context){
        return reader(context).getBoolean(LAUNCH_MAPS_KEY, false);
    }

    public static void setKeepScreenON(Context context, boolean enabled){
        editor(context).putBoolean(KEEP_SCREEN_ON_KEY, enabled);
        commit(context);
    }

    public static boolean getKeepScreenON(Context context) {
        return reader(context).getBoolean(KEEP_SCREEN_ON_KEY, false);
    }

    public static void setPriorityMode(Context context, boolean enabled){
        editor(context).putBoolean(PRIORITY_MODE_KEY, enabled);
        commit(context);
    }

    public static boolean getPriorityMode(Context context){
        return reader(context).getBoolean(PRIORITY_MODE_KEY, false);
    }

    public static void setMaxVolume(Context context, boolean enabled){
        editor(context).putBoolean(MAX_VOLUME_KEY, enabled);
        commit(context);
    }

    public static boolean getMaxVolume(Context context){
        return reader(context).getBoolean(MAX_VOLUME_KEY, false);
    }

    public static void setLaunchMusicPlayer(Context context, boolean enabled){
        editor(context).putBoolean(LAUNCH_MUSIC_PLAYER_KEY, enabled);
        commit(context);
    }

    public static boolean getLaunchMusicPlayer(Context context){
        return reader(context).getBoolean(LAUNCH_MUSIC_PLAYER_KEY, false);
    }

    public static void setBTDeviceToastMsg(Context context, boolean enabled){
        editor(context).putBoolean(BTDEVICE_TOAST_MSG_KEY, enabled);
        commit(context);
    }

    public static boolean getBTDeviceToastMsg(Context context){
        return reader(context).getBoolean(BTDEVICE_TOAST_MSG_KEY, false);
    }

    public static void setSelectedMusicPlayer(Context context, String musicPlayer){
        editor(context).putString(SELECTED_MUSIC_PLAYER_KEY, musicPlayer);
        commit(context);
    }

    public static String getSelectedMusicPlayer(Context context){
        return reader(context).getString(SELECTED_MUSIC_PLAYER_KEY, null);
    }

    private static void commit(Context context){
        editor(context).commit();
        _editor = null;
    }
}
