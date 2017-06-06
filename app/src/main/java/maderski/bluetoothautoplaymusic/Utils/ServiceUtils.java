package maderski.bluetoothautoplaymusic.Utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Jason on 6/6/17.
 */

public class ServiceUtils {
    public static void startService(Context context, Class<?> serviceClass, String tag) {
        Intent intent = new Intent(context, serviceClass);
        intent.addCategory(tag);
        context.startService(intent);
    }

    public static void stopService(Context context, Class<?> serviceClass, String tag) {
        Intent intent = new Intent(context, serviceClass);
        intent.addCategory(tag);
        context.stopService(intent);
    }
}
