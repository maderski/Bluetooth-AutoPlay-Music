package maderski.bluetoothautoplaymusic.bluetooth.btactions

import android.content.Context
import android.os.Handler
import android.widget.Toast

import maderski.bluetoothautoplaymusic.BuildConfig
import maderski.bluetoothautoplaymusic.controls.RingerControl
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.controls.mediaplayer.MediaPlayerControlManager
import maderski.bluetoothautoplaymusic.services.manager.ServiceManager
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 6/3/17.
 */

class BTHeadphonesActions(private val context: Context): KoinComponent {
    private val preferences: BAPMPreferences by inject()
    private val dataPreferences: BAPMDataPreferences by inject()
    private val volumeControl: VolumeControl by inject()
    private val mediaPlayerControlManager: MediaPlayerControlManager by inject()
    private val serviceManager: ServiceManager by inject()
    private val ringerControl: RingerControl by inject()

    fun connectActionsWithDelay() {
        val handler = Handler()
        val runnable = Runnable { connectActions() }
        handler.postDelayed(runnable, 4000)
    }

    fun connectActions() {
        // Get headphone preferred volume
        val preferredVolume = preferences.getHeadphonePreferredVolume()
        // Set headphone preferred volume
        volumeControl.setSpecifiedVolume(preferredVolume)
        // Start checking if music is playing
        mediaPlayerControlManager.play()

        dataPreferences.setIsHeadphonesDevice(true)
        if (BuildConfig.DEBUG)
            Toast.makeText(context, "Music Playing", Toast.LENGTH_SHORT).show()
    }

    fun disconnectActions() {
        mediaPlayerControlManager.pause()
        volumeControl.setToOriginalVolume(ringerControl)

        dataPreferences.setIsHeadphonesDevice(false)
        if (BuildConfig.DEBUG)
            Toast.makeText(context, "Music Paused", Toast.LENGTH_SHORT).show()
    }
}
