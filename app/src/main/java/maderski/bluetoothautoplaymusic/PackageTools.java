package maderski.bluetoothautoplaymusic;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Jason on 7/28/16.
 */
public class PackageTools {

    private static final String TAG = PackageTools.class.getName();

    // Package Names
    public static final String MAPS = "com.google.android.apps.maps";
    public static final String WAZE = "com.waze";
    public static final String GOOGLEPLAYMUSIC = "com.google.android.music";
    public static final String SPOTIFY = "com.spotify.music";
    public static final String PANDORA = "com.pandora.android";
    public static final String BEYONDPOD = "mobi.beyondpod";

    // Launches App that is associated with that package that was put into method
    public void launchPackage(Context context, String pkg){
        Log.d("Package intent: ", pkg + " started");
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(pkg);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if(launchIntent != null) {
            context.startActivity(launchIntent);
        } else {
            String toastMsg = pkg.equals(PackageTools.MAPS) || pkg.equals(PackageTools.WAZE) ?
                    "Unable to launch MAPS or WAZE" : "Unable to launch Music player";
            Toast.makeText(context, toastMsg, Toast.LENGTH_LONG).show();
        }
    }

    //Returns true if Package is on phone
    public boolean checkPkgOnPhone(Context context, String pkg){
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm = context.getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(pkg))
                return true;
        }
        return false;
    }

    // Returns Map App Name, intentionally only works with Google maps and Waze
    public String getMapAppName(Context context, String pkg){
        String mapAppName = "Not Found";
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(pkg, 0);
            mapAppName = context.getPackageManager().getApplicationLabel(appInfo).toString();
            if (mapAppName.equalsIgnoreCase("MAPS")) {
                mapAppName = "WAZE";
            } else {
                mapAppName = "GOOGLE MAPS";
            }
        }catch(Exception e){
            Log.e(TAG, e.getMessage());
        }
        return mapAppName;
    }

    // List of Installed Packages on the phone
    public void listOfPackagesOnPhone(Context context){
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> appInfo = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for(ApplicationInfo pkg:appInfo){
            Log.d(TAG, "Installed Pkg: " + pkg.packageName);
        }
    }

    // Is app running on phone
    public boolean isAppRunning(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();

        for(ActivityManager.RunningAppProcessInfo processInfo : processInfos){
            if(processInfo.processName.equals(packageName)){
                return true;
            }
        }
        return false;
    }
}
