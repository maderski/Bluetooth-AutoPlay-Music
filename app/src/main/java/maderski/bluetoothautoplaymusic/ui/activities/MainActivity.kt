package maderski.bluetoothautoplaymusic.ui.activities

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.squareup.otto.Subscribe
import maderski.bluetoothautoplaymusic.BuildConfig
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.analytics.constants.ActivityNameConstants
import maderski.bluetoothautoplaymusic.analytics.constants.SelectionConstants
import maderski.bluetoothautoplaymusic.bus.BusProvider
import maderski.bluetoothautoplaymusic.bus.events.A2DPSetSwitchEvent
import maderski.bluetoothautoplaymusic.bus.events.mapsevents.LocationNameSetEvent
import maderski.bluetoothautoplaymusic.helpers.TimeHelper
import maderski.bluetoothautoplaymusic.services.BAPMService
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import maderski.bluetoothautoplaymusic.ui.fragments.*
import maderski.bluetoothautoplaymusic.utils.PermissionUtils
import maderski.bluetoothautoplaymusic.utils.ServiceUtils
import java.util.*

class MainActivity : AppCompatActivity(),
        HeadphonesFragment.OnFragmentInteractionListener,
        TimePickerFragment.TimePickerDialogListener,
        WifiOffFragment.OnFragmentInteractionListener {

    private lateinit var mFirebaseHelper: FirebaseHelper

    // Show version of the BAPM App
    private val version: String
        get() {
            return try {
                val pkgInfo = packageManager.getPackageInfo(packageName, 0)
                pkgInfo.versionName
            } catch (e: Exception) {
                Log.e(TAG, e.message)
                "none"
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        mFirebaseHelper = FirebaseHelper(this)

        mFirebaseHelper.activityLaunched(ActivityNameConstants.MAIN)

        if (BAPMPreferences.getAutoBrightness(this)) {
            PermissionUtils.checkPermission(this, PermissionUtils.COARSE_LOCATION)
        }

        supportFragmentManager
                .beginTransaction()
                .add(R.id.container, HomeFragment.newInstance(), TAG_HOME_FRAGMENT)
                .commit()

        val bottomNavigationView = findViewById<View>(R.id.bottom_navigation) as BottomNavigationView
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            val itemId = item.itemId
            when (itemId) {
                R.id.menu_home -> {
                    val homeFragment = HomeFragment.newInstance()
                    handleNavigationSelection(homeFragment, TAG_HOME_FRAGMENT)
                    true
                }
                R.id.menu_maps -> {
                    val mapsFragment = MapsFragment.newInstance()
                    handleNavigationSelection(mapsFragment, TAG_MAPS_FRAGMENT)
                    true
                }
                R.id.menu_options -> {
                    val optionsFragment = OptionsFragment.newInstance()
                    handleNavigationSelection(optionsFragment, TAG_OPTIONS_FRAGMENT)
                    mFirebaseHelper.selectionMade(SelectionConstants.OPTIONS)
                    true
                }
                else -> false
            }
        }

        checkIfBAPMServiceRunning()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    //Display about when the three dots is clicked on
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        if (id == R.id.about_menu) {
            mFirebaseHelper.selectionMade(SelectionConstants.ABOUT)
            aboutSelected()
            return true
        } else if (id == R.id.link_menu) {
            mFirebaseHelper.selectionMade(SelectionConstants.RATE_ME)
            linkSelected()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        BusProvider.busInstance.register(this)
    }

    override fun onStop() {
        super.onStop()
        BusProvider.busInstance.unregister(this)
    }

    // Starts BAPMService if it is not running
    private fun checkIfBAPMServiceRunning() {
        val isServiceRunning = ServiceUtils.isServiceRunning(this, BAPMService::class.java)
        if (isServiceRunning.not()) {
            val serviceIntent = Intent(this, BAPMService::class.java)
            startService(serviceIntent)
        }
    }


    //Launches the AboutActivity when about is selected
    private fun aboutSelected() {
        val bottomNavBar = findViewById<View>(R.id.bottom_navigation)
        bottomNavBar.animate().alpha(0f).start()
        val view = findViewById<View>(R.id.toolbar)
        val snackbar = Snackbar.make(view, "Created by: Jason Maderski" + "\n" +
                "Version: " + version, Snackbar.LENGTH_LONG)
        snackbar.view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {}

            override fun onViewDetachedFromWindow(view: View) {
                bottomNavBar.animate().alpha(1f).start()
            }
        })
        snackbar.show()

    }

    private fun handleNavigationSelection(fragment: androidx.fragment.app.Fragment, fragmentTAG: String) {
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, fragment, fragmentTAG)
                .commit()
    }

    private fun linkSelected() {
        val alertDialog = AlertDialog.Builder(this@MainActivity).create()
        alertDialog.setTitle("Bluetooth Autoplay Music")
        alertDialog.setMessage("Google Play Store location")
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Launch Store"
        ) { dialog, which ->
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("market://details?id=maderski.bluetoothautoplaymusic")
            startActivity(intent)
        }
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel"
        ) { dialog, which -> dialog.dismiss() }
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Copy Link"
        ) { dialog, which ->
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("PlayStoreLink",
                    "https://play.google.com/store/apps/details?id=maderski.bluetoothautoplaymusic")
            clipboard.primaryClip = clip
            showClipboardToast()
        }
        alertDialog.show()
    }

    private fun showClipboardToast() {
        Toast.makeText(this, "Play Store link copied to clipboard",
                Toast.LENGTH_LONG).show()
    }

    override fun onTimeSet(typeOfTimeSet: String, isEndTime: Boolean, view: TimePicker, hourOfDay: Int, minute: Int) {
        val timeSet = hourOfDay * 100 + minute

        when (typeOfTimeSet) {
            TimePickerFragment.SCREEN_BRIGHTNESS_TIME -> if (isEndTime) {
                mFirebaseHelper.timeSetSelected(SelectionConstants.DIM_TIME, true)
                BAPMPreferences.setDimTime(this, timeSet)
                Log.d("Settings", "Dim brightness")
            } else {
                mFirebaseHelper.timeSetSelected(SelectionConstants.BRIGHT_TIME, true)
                BAPMPreferences.setBrightTime(this, timeSet)
                Log.d("Settings", "Bright brightness")
            }
            TimePickerFragment.MORNING_TIMESPAN -> {
                if (isEndTime) {
                    BAPMPreferences.setMorningEndTime(this, timeSet)

                    val setTime = TimeHelper.get12hrTime(BAPMPreferences.getMorningEndTime(this))
                    val timeDisplayed = findViewById<View>(R.id.morning_end_time_displayed) as TextView
                    timeDisplayed.text = setTime

                    mFirebaseHelper.timeSetSelected(SelectionConstants.MORNING_END_TIME, true)
                } else {
                    BAPMPreferences.setMorningStartTime(this, timeSet)

                    val setTime = TimeHelper.get12hrTime(BAPMPreferences.getMorningStartTime(this))
                    val timeDisplayed = findViewById<View>(R.id.morning_start_time_displayed) as TextView
                    timeDisplayed.text = setTime

                    mFirebaseHelper.timeSetSelected(SelectionConstants.MORNING_START_TIME, true)
                }
                Log.d("Map Options", typeOfTimeSet)
            }
            TimePickerFragment.EVENING_TIMESPAN -> {
                if (isEndTime) {
                    BAPMPreferences.setEveningEndTime(this, timeSet)

                    val setTime = TimeHelper.get12hrTime(BAPMPreferences.getEveningEndTime(this))
                    val timeDisplayed = findViewById<View>(R.id.evening_end_time_displayed) as TextView
                    timeDisplayed.text = setTime

                    mFirebaseHelper.timeSetSelected(SelectionConstants.EVENING_END_TIME, true)
                } else {
                    BAPMPreferences.setEveningStartTime(this, timeSet)

                    val setTime = TimeHelper.get12hrTime(BAPMPreferences.getEveningStartTime(this))
                    val timeDisplayed = findViewById<View>(R.id.evening_start_time_displayed) as TextView
                    timeDisplayed.text = setTime

                    mFirebaseHelper.timeSetSelected(SelectionConstants.EVENING_START_TIME, true)
                }
                Log.d("Map Options", typeOfTimeSet)
            }
            TimePickerFragment.CUSTOM_TIMESPAN -> {
                if (isEndTime) {
                    BAPMPreferences.setCustomEndTime(this, timeSet)

                    val setTime = TimeHelper.get12hrTime(BAPMPreferences.getCustomEndTime(this))
                    val timeDisplayed = findViewById<View>(R.id.custom_end_time_displayed) as TextView
                    timeDisplayed.text = setTime

                    mFirebaseHelper.timeSetSelected(SelectionConstants.CUSTOM_END_TIME, true)
                } else {
                    BAPMPreferences.setCustomStartTime(this, timeSet)

                    val setTime = TimeHelper.get12hrTime(BAPMPreferences.getCustomStartTime(this))
                    val timeDisplayed = findViewById<View>(R.id.custom_start_time_displayed) as TextView
                    timeDisplayed.text = setTime

                    mFirebaseHelper.timeSetSelected(SelectionConstants.CUSTOM_START_TIME, true)
                }
                Log.d("Map Options", typeOfTimeSet)
            }
        }
    }

    override fun onTimeCancel(typeOfTimeSet: String, isEndTime: Boolean) {
        when (typeOfTimeSet) {
            TimePickerFragment.SCREEN_BRIGHTNESS_TIME -> mFirebaseHelper.timeSetSelected(if (isEndTime) SelectionConstants.DIM_TIME else SelectionConstants.BRIGHT_TIME, false)
            TimePickerFragment.MORNING_TIMESPAN -> {}
            TimePickerFragment.EVENING_TIMESPAN -> {}
            TimePickerFragment.CUSTOM_TIMESPAN -> {}
        }
    }

    override fun setHeadphoneDevices(headphoneDevices: HashSet<String>) {
        BAPMPreferences.setHeadphoneDevices(applicationContext, headphoneDevices)
        if (BuildConfig.DEBUG) {
            for (deviceName in headphoneDevices) {
                Log.d(TAG, "device name: $deviceName")
            }
        }
    }

    override fun headphonesDoneClicked(removeDevices: HashSet<String>) {
        val headphoneDevices = BAPMPreferences.getHeadphoneDevices(this)
        val btDevices = BAPMPreferences.getBTDevices(this)

        for (deviceName in removeDevices) {
            if (headphoneDevices.contains(deviceName))
                headphoneDevices.remove(deviceName)
        }

        if (BuildConfig.DEBUG) {
            for (deviceName in headphoneDevices) {
                Log.d(TAG, "saveBTDevice: $deviceName")
            }
        }

        btDevices.addAll(headphoneDevices)
        BAPMPreferences.setBTDevices(this, btDevices)

        val homeFragment = supportFragmentManager.findFragmentByTag(TAG_HOME_FRAGMENT) as HomeFragment?
        if (homeFragment != null) {
            homeFragment.view?.let { homeFragmentView ->
                homeFragment.checkboxCreator(homeFragmentView, this)
            }
        }
    }

    override fun headDeviceSelection(deviceName: String, addDevice: Boolean) {
        mFirebaseHelper.deviceAdd(SelectionConstants.HEADPHONE_DEVICE, deviceName, addDevice)
    }

    override fun setWifiOffDevices(wifiOffDevices: HashSet<String>) {
        BAPMPreferences.setTurnWifiOffDevices(this, wifiOffDevices)
    }

    @Subscribe
    fun onUseHeadphonesA2DP(a2DPSetSwitchEvent: A2DPSetSwitchEvent) {
        BAPMPreferences.setUseA2dpHeadphones(this, a2DPSetSwitchEvent.isUsingA2DP)
    }

    @Subscribe
    fun onLocationNameSet(locationNameSetEvent: LocationNameSetEvent) {
        BAPMPreferences.setCustomLocationName(this, locationNameSetEvent.locationName)
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val TAG_HOME_FRAGMENT = "home_fragment"
        private const val TAG_MAPS_FRAGMENT = "maps_fragment"
        private const val TAG_OPTIONS_FRAGMENT = "options_fragment"
    }
}
