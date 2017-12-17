package maderski.bluetoothautoplaymusic.asynctasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import maderski.bluetoothautoplaymusic.services.BAPMService;

/**
 * Created by Jason on 3/2/17.
 */

public class StartServiceTask extends AsyncTask<Context, Void, Void> {
    @Override
    protected Void doInBackground(Context... contexts) {
        Intent serviceIntent = new Intent(contexts[0], BAPMService.class);
        contexts[0].startService(serviceIntent);
        return null;
    }
}
