package maderski.bluetoothautoplaymusic;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.StringDef;
import android.util.Log;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * Created by Jason on 7/28/16.
 */
public class PackageTools {

    private static final String TAG = PackageTools.class.getName();

    // Package Names
    @StringDef({
            PackageName.MAPS,
            PackageName.WAZE,
            PackageName.GOOGLEPLAYMUSIC,
            PackageName.SPOTIFY,
            PackageName.PANDORA,
            PackageName.BEYONDPOD,
            PackageName.APPLEMUSIC,
            PackageName.FMINDIA,
            PackageName.POWERAMP,
            PackageName.DOUBLETWIST,
            PackageName.LISTENAUDIOBOOK,
            PackageName.DEEZERMUSIC,
            PackageName.GOOGLEPODCASTS
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PackageName {
        String MAPS = "com.google.android.apps.maps";
        String WAZE = "com.waze";
        String GOOGLEPLAYMUSIC = "com.google.android.music";
        String SPOTIFY = "com.spotify.music";
        String PANDORA = "com.pandora.android";
        String BEYONDPOD = "mobi.beyondpod";
        String APPLEMUSIC = "com.apple.android.music";
        String FMINDIA = "com.fmindia.activities";
        String POWERAMP = "com.maxmpz.audioplayer";
        String DOUBLETWIST = "com.doubleTwist.androidPlayer";
        String LISTENAUDIOBOOK = "com.acmeandroid.listen";
        String DEEZERMUSIC = "deezer.android.app";
        String GOOGLEPODCASTS = "com.google.android.apps.podcasts";
    }

    // Launches App that is associated with that package that was put into method
    public void launchPackage(Context context, String pkg){
        Log.d("Package intent: ", pkg + " started");
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(pkg);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if(launchIntent != null) {
            context.startActivity(launchIntent);
        } else {
            String toastMsg = pkg.equals(PackageName.MAPS) || pkg.equals(PackageName.WAZE) ?
                    "Unable to launch MAPS or WAZE" : "Unable to launch Music player";
            Toast.makeText(context, toastMsg, Toast.LENGTH_LONG).show();
        }
    }

    public void launchPackage(Context context, String packageName, Uri data, String action){
        Log.d("Package intent: ", packageName + " started");
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        launchIntent.setAction(action);
        launchIntent.setData(data);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        if(launchIntent != null) {
            context.startActivity(launchIntent);
        } else {
            String toastMsg = packageName.equals(PackageName.MAPS) || packageName.equals(PackageName.WAZE) ?
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
        if (activityManager != null) {
            List<ActivityManager.RunningAppProcessInfo> processInfos = activityManager.getRunningAppProcesses();

            for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }

        return false;
    }
}
