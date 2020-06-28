package maderski.bluetoothautoplaymusic.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.workers.OnAppUpdateWorker
import maderski.bluetoothautoplaymusic.workers.OnBootWorker
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 6/10/17.
 */

class OnAppUpdateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (action != null && action == Intent.ACTION_PACKAGE_REPLACED) {
                Log.d(TAG, "On Update BAPM, enqueue BAPM work!")
                // Create and add work request to work manager
                val workOnAppUpdateRequest = OneTimeWorkRequestBuilder<OnAppUpdateWorker>()
                        .addTag(OnBootWorker.TAG)
                        .build()
                WorkManager.getInstance(context).enqueue(workOnAppUpdateRequest)
            }
        }
    }

    companion object {
        private const val TAG = "OnAppUpdateReceiver"
    }
}
