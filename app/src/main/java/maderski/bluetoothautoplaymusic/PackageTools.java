package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by Jason on 7/28/16.
 */
public class PackageTools {

    private static final String TAG = PackageTools.class.getName();

    //Package Names
    public static final String MAPS = "com.google.android.apps.maps";
    public static final String WAZE = "com.waze";
    public static final String GOOGLEPLAYMUSIC = "com.google.android.music";
    public static final String SPOTIFY = "com.spotify.music";
    public static final String PANDORA = "com.pandora.android";
    public static final String BEYONDPOD = "mobi.beyondpod";

    private Context context;

    public PackageTools(Context context){
        this.context = context;
    }

    //Launches App that is associated with that package that was put into method
    public void launchPackage(String pkg){
        if(BuildConfig.DEBUG)
            Log.i("Package intent: ", pkg + " started");
        Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(pkg);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
        context.startActivity(LaunchIntent);
    }

    //Returns true if Package is on phone
    public boolean checkPkgOnPhone(String pkg){
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

    //Returns Map App Name, intentionally only works with Google maps and Waze
    public String getMapAppName(String pkg){
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

    //List of Installed Packages on the phone
    public void listOfPackagesOnPhone(){
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> appInfo = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for(ApplicationInfo pkg:appInfo){
            if(BuildConfig.DEBUG)
                Log.i(TAG, "Installed Pkg: " + pkg.packageName);
        }
    }
}
