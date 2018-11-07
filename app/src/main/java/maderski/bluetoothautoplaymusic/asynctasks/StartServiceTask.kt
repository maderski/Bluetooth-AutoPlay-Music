package maderski.bluetoothautoplaymusic.asynctasks

import android.content.Context
import android.content.Intent
import android.os.AsyncTask

import maderski.bluetoothautoplaymusic.services.BAPMService

/**
 * Created by Jason on 3/2/17.
 */

class StartServiceTask : AsyncTask<Context, Void, Void>() {
    override fun doInBackground(vararg contexts: Context): Void? {
        val serviceIntent = Intent(contexts[0], BAPMService::class.java)
        contexts[0].startService(serviceIntent)
        return null
    }
}
