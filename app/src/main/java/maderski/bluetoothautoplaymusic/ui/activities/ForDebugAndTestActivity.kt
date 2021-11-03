package maderski.bluetoothautoplaymusic.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_for_debug_and_test.*
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.bluetooth.BTConnectionManager
import org.koin.android.ext.android.inject

class ForDebugAndTestActivity : AppCompatActivity() {
    private val btConnectionManager: BTConnectionManager by inject()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_for_debug_and_test)

        bn_bluetooth_connected_test.setOnClickListener {
            btConnectionManager.startBTConnectService()
        }
        bn_bluetooth_disconnected_test.setOnClickListener {
            btConnectionManager.stopBTDisconnectService()
        }
    }
}