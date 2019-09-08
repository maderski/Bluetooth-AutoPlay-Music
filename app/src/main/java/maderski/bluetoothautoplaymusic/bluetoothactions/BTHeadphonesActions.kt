package maderski.bluetoothautoplaymusic.bluetoothactions

import android.content.Context
import android.os.Handler
import android.widget.Toast

import maderski.bluetoothautoplaymusic.BuildConfig
import maderski.bluetoothautoplaymusic.controls.RingerControl
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.controls.mediaplayer.MediaPlayerControlManager
import maderski.bluetoothautoplaymusic.services.BTStateChangedService
import maderski.bluetoothautoplaymusic.services.ServiceManager
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMDataPreferences
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.utils.serviceManager
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * Created by Jason on 6/3/17.
 */

class BTHeadphonesActions(private val mContext: Context): KoinComponent {
    private val preferences: BAPMPreferences by inject()
    private val dataPreferences: BAPMDataPreferences by inject()
    private val volumeControl: VolumeControl by inject()
    private val mediaPlayerControlManager: MediaPlayerControlManager by inject()
    private val serviceManager: ServiceManager by inject()

    fun connectActionsWithDelay() {
        val handler = Handler()
        val runnable = Runnable { connectActions() }
        handler.postDelayed(runnable, 4000)

        serviceManager.startService(BTStateChangedService::class.java, BTStateChangedService.TAG)
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
            Toast.makeText(mContext, "Music Playing", Toast.LENGTH_SHORT).show()
    }

    fun disconnectActions() {
        mediaPlayerControlManager.pause()
        volumeControl.setToOriginalVolume(RingerControl(mContext))

        dataPreferences.setIsHeadphonesDevice(false)
        if (BuildConfig.DEBUG)
            Toast.makeText(mContext, "Music Paused", Toast.LENGTH_SHORT).show()

        serviceManager.stopService(BTStateChangedService::class.java, BTStateChangedService.TAG)
    }

    companion object {
        private const val TAG = "BTHeadphonesActions"
    }
}
