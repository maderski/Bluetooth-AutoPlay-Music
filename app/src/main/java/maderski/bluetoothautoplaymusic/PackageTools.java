package maderski.bluetoothautoplaymusic;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.StringDef;
import android.util.Log;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jason on 7/28/16.
 */
public class PackageTools {

    private static final String TAG = "PackageTools";

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
            PackageName.GOOGLEPODCASTS,
            PackageName.DEEZERMUSIC
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
        String GOOGLEPODCASTS = "com.google.android.apps.podcasts";
        String DEEZERMUSIC = "deezer.android.app";
    }

    // Launches App that is associated with that package that was put into method
    public void launchPackage(Context context, String pkg){
        Log.d("Package intent: ", pkg + " started");
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(pkg);
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
        if(launchIntent != null) {
            launchIntent.setAction(action);
            launchIntent.setData(data);
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

    //List of Mediaplayers that is installed on the phone
    public List<String> listOfInstalledMediaPlayers(Context context) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        List<ResolveInfo> pkgAppsList = context.getPackageManager().queryBroadcastReceivers(intent, 0);
        List<String> installedMediaPlayers = new ArrayList<>();

        for(ResolveInfo ri:pkgAppsList){
            String resolveInfo = ri.toString();
            if(resolveInfo.contains(".playback")
                    || resolveInfo.contains("music")
                    || resolveInfo.contains("Music")
                    || resolveInfo.contains("audioplayer")
                    || resolveInfo.contains("mobi.beyondpod")
                    || resolveInfo.contains("au.com.shiftyjelly.pocketcasts")
                    || resolveInfo.contains(PackageName.DEEZERMUSIC)
                    || resolveInfo.contains(PackageName.PANDORA)
                    || resolveInfo.contains(PackageName.DOUBLETWIST)
                    || resolveInfo.contains(PackageName.LISTENAUDIOBOOK)) {
                String[] resolveInfoSplit = resolveInfo.split(" ");
                String pkg = resolveInfoSplit[1].substring(0, resolveInfoSplit[1].indexOf("/"));
                if (!installedMediaPlayers.contains(pkg)) {
                    installedMediaPlayers.add(pkg);
                }
            }
        }

        // Check if Google Podcasts is installed
        boolean isGooglePodcastsInstalled = checkPkgOnPhone(context, PackageName.GOOGLEPODCASTS);
        if(isGooglePodcastsInstalled) {
            installedMediaPlayers.add(PackageName.GOOGLEPODCASTS);
        }

        return installedMediaPlayers;
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
        if(activityManager != null) {
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
