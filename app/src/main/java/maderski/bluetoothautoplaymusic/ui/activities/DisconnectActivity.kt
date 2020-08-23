package maderski.bluetoothautoplaymusic.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.bluetooth.btactions.BTDisconnectActions
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import org.koin.android.ext.android.inject

class DisconnectActivity : AppCompatActivity() {
    private val btDisconnectActions: BTDisconnectActions by inject()
    private val preferences: BAPMPreferences by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_disconnect)

        btDisconnectActions.actionsOnBTDisconnect()

        val sendToBackground = preferences.getSendToBackground()
        if (sendToBackground) {
            sendEverythingToBackground()
        }

        finishAffinity()
    }

    private fun sendEverythingToBackground() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
