package maderski.bluetoothautoplaymusic.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import maderski.bluetoothautoplaymusic.workers.OnBootWorker

/**
 * Created by Jason on 1/5/16.
 */
class BootCompletedReceiver : BroadcastReceiver() {

    // Start BAPMService on phone boot
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (action != null && action == Intent.ACTION_BOOT_COMPLETED) {
                Log.d(TAG, "On Boot Completed, enqueue BAPM work!")
                // Create and add work request to work manager
                val workOnBootRequest = OneTimeWorkRequestBuilder<OnBootWorker>()
                        .addTag(OnBootWorker.TAG)
                        .build()
                WorkManager.getInstance(context).enqueue(workOnBootRequest)
            }
        }
    }

    companion object {
        private const val TAG = "BootCompletedReceiver"
    }
}
