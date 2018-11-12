package maderski.bluetoothautoplaymusic.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle

/**
 * Created by Jason on 8/28/16.
 */
class SplashActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
