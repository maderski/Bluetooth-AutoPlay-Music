package maderski.bluetoothautoplaymusic.ui.fragments

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.fragment_home.view.*
import maderski.bluetoothautoplaymusic.BuildConfig
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.analytics.constants.FeatureConstants
import maderski.bluetoothautoplaymusic.analytics.constants.SelectionConstants
import maderski.bluetoothautoplaymusic.bluetooth.models.BAPMDevice
import maderski.bluetoothautoplaymusic.bluetooth.BTConnectionManager
import maderski.bluetoothautoplaymusic.helpers.BluetoothDeviceHelper
import maderski.bluetoothautoplaymusic.helpers.LaunchHelper
import maderski.bluetoothautoplaymusic.helpers.PackageHelper
import maderski.bluetoothautoplaymusic.maps.MapApps.MAPS
import maderski.bluetoothautoplaymusic.maps.MapApps.WAZE
import maderski.bluetoothautoplaymusic.permission.PermissionManager
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import org.koin.android.ext.android.inject

class HomeFragment : androidx.fragment.app.Fragment() {
    private val preferences: BAPMPreferences by inject()
    private val launchHelper: LaunchHelper by inject()
    private val firebaseHelper: FirebaseHelper by inject()
    private val packageHelper: PackageHelper by inject()
    private val bluetoothDeviceHelper: BluetoothDeviceHelper by inject()
    private val permissionManager: PermissionManager by inject()

    private val radioButtonIndex: Int
        get() {
            val selectedMusicPlayer = preferences.getPkgSelectedMusicPlayer()
            return installedMediaPlayers.indexOf(selectedMusicPlayer)
        }

    private var installedMediaPlayers: Set<String> = setOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        mapsToggleButton(rootView)
        launchMusicPlayerToggleButton(rootView)
        keepONToggleButton(rootView)
        priorityToggleButton(rootView)
        volumeMAXToggleButton(rootView)
        unlockScreenToggleButton(rootView)

        return rootView
    }

    override fun onResume() {
        super.onResume()
        installedMediaPlayers = packageHelper.installedMediaPlayersSet()

        view?.let {
            setupUIElements(it)
        }

        checkIfWazeRemoved(requireActivity())
    }

    //Checks if WAZE was removed and if WAZE was set to the MapsChoice and if so, set MapsChoice in
    //SharedPrefs to MAPS
    private fun checkIfWazeRemoved(context: Context) {
        val mapAppChoice = preferences.getMapsChoice()
        if (mapAppChoice.equals(WAZE.packageName, ignoreCase = true)) {
            val isWazeOnPhone = launchHelper.isAbleToLaunch(WAZE.packageName)
            if (isWazeOnPhone.not()) {
                Log.d(TAG, "Checked")
                preferences.setMapsChoice(MAPS.packageName)
            } else {
                Log.d(TAG, "WAZE is installed")
            }
        }
    }

    //Setup the UI
    private fun setupUIElements(view: View) {
        val fragmentActivity = requireActivity()

        setFonts(view, fragmentActivity)
        radiobuttonCreator(view, fragmentActivity)
        checkboxCreator(view, fragmentActivity)
        setButtonPreferences(view, fragmentActivity)
        radioButtonListener(view, fragmentActivity)
        setMapsButtonText(view, fragmentActivity)
    }

    //Create Checkboxes
    fun checkboxCreator(view: View, context: Context) {
        var checkBox: CheckBox
        val textView: TextView

        val btDeviceCkBoxLL = view.findViewById<View>(R.id.checkBoxLL) as LinearLayout
        btDeviceCkBoxLL.removeAllViews()
        val savedBTDevices = preferences.getBAPMDevices().toMutableSet()
        val listOfBTDevices = bluetoothDeviceHelper.listOfBluetoothDevices()
        if (listOfBTDevices.isEmpty()) {
            textView = TextView(context)
            textView.setText(R.string.no_BT_found)
            btDeviceCkBoxLL.addView(textView)
        } else {
            listOfBTDevices.forEach { btDevice ->
                checkBox = CheckBox(context).apply {
                    text = btDevice.name
                    setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    typeface = Typeface.createFromAsset(context.assets, "fonts/TitilliumText400wt.otf")
                    isChecked = savedBTDevices.contains(btDevice)
                }
                btDeviceCkBoxLL.addView(checkBox)
                checkboxListener(checkBox, btDevice, savedBTDevices)
            }
        }
    }

    //Get Selected Checkboxes
    private fun checkboxListener(checkBox: CheckBox, bapmDevice: BAPMDevice, savedBTDevices: MutableSet<BAPMDevice>) {
        checkBox.setOnClickListener {
            if (checkBox.isChecked) {
                savedBTDevices.add(bapmDevice)
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "TRUE $bapmDevice")
                }
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "SAVED")
                }
            } else {
                savedBTDevices.remove(bapmDevice)
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "FALSE $bapmDevice")
                }
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "SAVED")
                }
            }
            preferences.setBAPMDevices(savedBTDevices)
            firebaseHelper.deviceAdd(SelectionConstants.BLUETOOTH_DEVICE, bapmDevice.name, checkBox.isChecked)
        }
    }

    //Get list of installed Mediaplayers and create Radiobuttons
    private fun radiobuttonCreator(view: View, context: Context) {
        val pm = context.packageManager
        val rdoMPGroup = view.findViewById<View>(R.id.rdoMusicPlayers) as RadioGroup
        rdoMPGroup.removeAllViews()

        for (packageName in installedMediaPlayers) {
            try {
                val appInfo = pm.getApplicationInfo(packageName, 0)
                val mediaPlayer = pm.getApplicationLabel(appInfo).toString()
                val color = ContextCompat.getColor(context, R.color.colorPrimary)
                val rdoButton = RadioButton(context)
                rdoButton.text = mediaPlayer
                rdoButton.setTextColor(color)
                rdoButton.typeface = Typeface.createFromAsset(context.assets, "fonts/TitilliumText400wt.otf")
                rdoMPGroup.addView(rdoButton)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //Get Selected Radiobutton
    private fun radioButtonListener(view: View, context: Context) {
        val group = view.findViewById<View>(R.id.rdoMusicPlayers) as RadioGroup
        group.setOnCheckedChangeListener { radioGroup, i ->
            val radioButton = radioGroup.findViewById<View>(i)
            val index = radioGroup.indexOfChild(radioButton)
            val packageName = installedMediaPlayers.toList()[index]

            preferences.setPkgSelectedMusicPlayer(packageName)

            // Firebase analytics
            val selectedMusicPlayer = preferences.getPkgSelectedMusicPlayer()
            val hasMusicPlayerChanged = selectedMusicPlayer.equals(packageName, ignoreCase = true).not()
            firebaseHelper.musicPlayerChoice(context, packageName, hasMusicPlayerChanged)

            Log.d(TAG, Integer.toString(index))
            Log.d(TAG, "Selected Music Player: $selectedMusicPlayer")
            Log.d(TAG, Integer.toString(radioGroup.checkedRadioButtonId))
        }
    }

    //Change the Maps button text to Maps or Waze depending on what Maps the user is launching
    private fun setMapsButtonText(view: View, context: Context) {
        var mapChoice = preferences.getMapsChoice()
        val packageManager = context.packageManager
        mapChoice = try {
            val appInfo = packageManager.getApplicationInfo(mapChoice, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            "Maps"
        }

        val textView = view.findViewById<View>(R.id.textView4) as TextView
        val launchMapChoice = "Launch $mapChoice"
        textView.text = launchMapChoice
    }

    //Set the button and radiobutton states
    private fun setButtonPreferences(view: View, context: Context) {
        var btnState: Boolean?
        var toggleButton: ToggleButton = view.findViewById<View>(R.id.MapsToggleButton) as ToggleButton

        btnState = preferences.getLaunchGoogleMaps()
        toggleButton.isChecked = btnState

        btnState = preferences.getKeepScreenON()
        toggleButton = view.findViewById<View>(R.id.KeepONToggleButton) as ToggleButton
        toggleButton.isChecked = btnState

        btnState = preferences.getPriorityMode()
        toggleButton = view.findViewById<View>(R.id.PriorityToggleButton) as ToggleButton
        toggleButton.isChecked = btnState

        btnState = preferences.getMaxVolume()
        toggleButton = view.findViewById<View>(R.id.VolumeMAXToggleButton) as ToggleButton
        toggleButton.isChecked = btnState

        btnState = preferences.getLaunchMusicPlayer()
        toggleButton = view.findViewById<View>(R.id.LaunchMusicPlayerToggleButton) as ToggleButton
        toggleButton.isChecked = btnState

        btnState = preferences.getUnlockScreen()
        toggleButton = view.findViewById<View>(R.id.UnlockToggleButton) as ToggleButton
        toggleButton.isChecked = btnState

        try {
            if (installedMediaPlayers.isNotEmpty()) {
                val rdoGroup = view.findViewById<View>(R.id.rdoMusicPlayers) as RadioGroup
                val index = radioButtonIndex
                val radioButton = rdoGroup.getChildAt(index) as RadioButton
                radioButton.isChecked = true
            } else {
                val musicPlayersLL = view.findViewById<View>(R.id.MusicPlayers) as LinearLayout
                val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL
                musicPlayersLL.layoutParams = layoutParams
                val textView = TextView(context)
                textView.gravity = Gravity.CENTER_HORIZONTAL
                textView.text = getString(R.string.no_music_players_msg)
                musicPlayersLL.addView(textView)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    //***Toggle button actions are below, basically set SharedPref value for specified button***
    fun mapsToggleButton(view: View) {
        val mapsToggleButton = view.findViewById<View>(R.id.MapsToggleButton) as Button
        mapsToggleButton.setOnClickListener { mapsToggleButtonView ->
            val on = (mapsToggleButtonView as ToggleButton).isChecked
            firebaseHelper.featureEnabled(FeatureConstants.LAUNCH_MAPS, on)
            if (on) {
                preferences.setLaunchGoogleMaps(true)
                Log.i(TAG, "MapButton is ON")
                Log.i(TAG, "Dismiss Keyguard is ON")
            } else {
                preferences.setLaunchGoogleMaps(false)
                Log.d(TAG, "MapButton is OFF")
            }
        }
    }

    fun keepONToggleButton(view: View) {
        val keepOnToggleButton = view.findViewById<View>(R.id.KeepONToggleButton) as Button
        keepOnToggleButton.setOnClickListener { keepOnToggleButtonView ->
            val on = (keepOnToggleButtonView as ToggleButton).isChecked
            firebaseHelper.featureEnabled(FeatureConstants.KEEP_SCREEN_ON, on)
            if (on) {
                preferences.setKeepScreenON(true)
                Log.d(TAG, "Keep Screen ON Button is ON")
            } else {
                preferences.setKeepScreenON(false)
                Log.d(TAG, "Keep Screen ON Button is OFF")
            }
        }
    }

    fun priorityToggleButton(view: View) {
        val priorityToggleButton = view.findViewById<View>(R.id.PriorityToggleButton) as Button
        priorityToggleButton.setOnClickListener { view ->
            val on = (view as ToggleButton).isChecked
            firebaseHelper.featureEnabled(FeatureConstants.PRIORITY_MODE, on)
            if (on) {
                permissionManager.checkDoNotDisturbPermission()
                preferences.setPriorityMode(true)
                Log.d(TAG, "Priority Button is ON")
            } else {
                preferences.setPriorityMode(false)
                Log.d(TAG, "Priority Button is OFF")
            }
        }
    }

    fun volumeMAXToggleButton(view: View) {
        val volumeMAXToggleButton = view.findViewById<View>(R.id.VolumeMAXToggleButton) as Button
        volumeMAXToggleButton.setOnClickListener { keepOnToggleButtonViewView ->
            val on = (keepOnToggleButtonViewView as ToggleButton).isChecked
            firebaseHelper.featureEnabled(FeatureConstants.MAX_VOLUME, on)
            if (on) {
                permissionManager.checkDoNotDisturbPermission()
                preferences.setMaxVolume(true)
                Log.d(TAG, "Max Volume Button is ON")
            } else {
                preferences.setMaxVolume(false)
                Log.d(TAG, "Max Volume Button is OFF")
            }
        }
    }

    fun launchMusicPlayerToggleButton(view: View) {
        val launchMusicPlayerToggleButton = view.findViewById<View>(R.id.LaunchMusicPlayerToggleButton) as Button
        launchMusicPlayerToggleButton.setOnClickListener { launchMusicPlayerToggleButtonView ->
            val on = (launchMusicPlayerToggleButtonView as ToggleButton).isChecked
            firebaseHelper.featureEnabled(FeatureConstants.LAUNCH_MUSIC_PLAYER, on)
            if (on) {
                preferences.setLaunchMusicPlayer(true)
                Log.d(TAG, "Launch Music Player Button is ON")
            } else {
                preferences.setLaunchMusicPlayer(false)
                Log.d(TAG, "Launch Music Player Button is OFF")
            }
        }
    }

    fun unlockScreenToggleButton(view: View) {
        val unlockScreenToggleButton = view.findViewById<View>(R.id.UnlockToggleButton) as Button
        unlockScreenToggleButton.setOnClickListener { unlockScreenToggleButtonView ->
            val on = (unlockScreenToggleButtonView as ToggleButton).isChecked
            firebaseHelper.featureEnabled(FeatureConstants.DISMISS_KEYGUARD, on)
            if (on) {
                preferences.setUnlockScreen(true)
                Log.i(TAG, "Dismiss KeyGuard Button is ON")
            } else {
                preferences.setUnlockScreen(false)
                Log.i(TAG, "Dismiss KeyGuard Button is OFF")
            }
        }
    }

    private fun setFonts(view: View, context: Context) {
        val typeface = Typeface.createFromAsset(context.assets, "fonts/TitilliumText400wt.otf")
        val typefaceBold = Typeface.createFromAsset(context.assets, "fonts/TitilliumText600wt.otf")

        var textView = view.findViewById<View>(R.id.textView) as TextView
        textView.typeface = typefaceBold

        textView = view.findViewById<View>(R.id.textView2) as TextView
        textView.typeface = typefaceBold

        textView = view.findViewById<View>(R.id.textView3) as TextView
        textView.typeface = typefaceBold

        textView = view.findViewById<View>(R.id.textView4) as TextView
        textView.typeface = typeface

        textView = view.findViewById<View>(R.id.textView5) as TextView
        textView.typeface = typeface

        textView = view.findViewById<View>(R.id.textView6) as TextView
        textView.typeface = typeface

        textView = view.findViewById<View>(R.id.textView7) as TextView
        textView.typeface = typeface

        textView = view.findViewById<View>(R.id.textView8) as TextView
        textView.typeface = typeface

        textView = view.findViewById<View>(R.id.textView9) as TextView
        textView.typeface = typeface

    }

    companion object {
        private const val TAG = "HomeFragment"

        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}
