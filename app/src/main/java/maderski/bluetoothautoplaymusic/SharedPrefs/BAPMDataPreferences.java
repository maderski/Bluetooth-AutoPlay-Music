package maderski.bluetoothautoplaymusic.SharedPrefs;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Jason on 8/6/16.
 */
public class BAPMDataPreferences{
    private static SharedPreferences.Editor _editor;

    private static String MY_PREFS_NAME = "BAPMDataPreference";

    private static final String IS_SELECTED_KEY = "IsSelectedDevice";
    private static final String RAN_ACTIONS_ON_BT_CONNECT_KEY = "RanActionsOnBTConnect";
    private static final String CURRENT_RINGER_SET = "CurrentRingerSet";
    private static final String ORIGINAL_MEDIA_VOLUME = "OriginalMediaVolume";
    private static final String LAUNCH_NOTIF_PRESENT = "LaunchNotifPresent";
    private static final String IS_HEADPHONES_DEVICE = "IsHeadphonesDevice";

    //Writes to SharedPreferences, but still need to commit setting to save it
    private static SharedPreferences.Editor editor(Context context){

        if(_editor == null){
            _editor = context.getSharedPreferences(MY_PREFS_NAME, context.MODE_PRIVATE).edit();
            _editor.commit();
        }

        return _editor;
    }

    //Reads SharedPreferences value
    private static SharedPreferences reader(Context context){

        return context.getSharedPreferences(MY_PREFS_NAME, context.MODE_PRIVATE);
    }

    //Commits write to SharedPreferences
    private static void commit(Context context){
        editor(context).commit();
        _editor = null;
    }

    public static void setIsHeadphonesDevice(Context context, boolean isHeadphones){
        editor(context).putBoolean(IS_HEADPHONES_DEVICE, isHeadphones);
        commit(context);
    }

    public static boolean getIsAHeadphonesDevice(Context context){
        return reader(context).getBoolean(IS_HEADPHONES_DEVICE, false);
    }

    public static void setLaunchNotifPresent(Context context, boolean enabled){
        editor(context).putBoolean(LAUNCH_NOTIF_PRESENT, enabled);
        commit(context);
    }

    public static boolean getLaunchNotifPresent(Context context){
        return reader(context).getBoolean(LAUNCH_NOTIF_PRESENT, false);
    }

    public static void setOriginalMediaVolume(Context context, int volumeLevel){
        editor(context).putInt(ORIGINAL_MEDIA_VOLUME, volumeLevel);
        commit(context);
    }

    public static int getOriginalMediaVolume(Context context){
        return reader(context).getInt(ORIGINAL_MEDIA_VOLUME, 7);
    }

    public static void setCurrentRingerSet(Context context, int ringerSetting){
        editor(context).putInt(CURRENT_RINGER_SET, ringerSetting);
        commit(context);
    }

    public static int getCurrentRingerSet(Context context){
        return reader(context).getInt(CURRENT_RINGER_SET, 2);
    }

    public static void setIsSelected(Context context, boolean enabled){
        editor(context).putBoolean(IS_SELECTED_KEY, enabled);
        commit(context);
    }

    public static boolean getIsSelected(Context context){
        return reader(context).getBoolean(IS_SELECTED_KEY, false);
    }

    public static void setRanActionsOnBtConnect(Context context, boolean enabled){
        editor(context).putBoolean(RAN_ACTIONS_ON_BT_CONNECT_KEY, enabled);
        commit(context);
    }

    public static boolean getRanActionsOnBtConnect(Context context){
        return reader(context).getBoolean(RAN_ACTIONS_ON_BT_CONNECT_KEY, false);
    }
}
