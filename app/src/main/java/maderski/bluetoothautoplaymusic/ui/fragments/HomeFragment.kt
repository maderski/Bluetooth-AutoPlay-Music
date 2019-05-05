package maderski.bluetoothautoplaymusic.ui.fragments

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.widget.CompoundButtonCompat
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton

import java.util.ArrayList
import java.util.HashSet

import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.analytics.constants.FeatureConstants
import maderski.bluetoothautoplaymusic.analytics.constants.SelectionConstants
import maderski.bluetoothautoplaymusic.utils.BluetoothUtils
import maderski.bluetoothautoplaymusic.BuildConfig
import maderski.bluetoothautoplaymusic.helpers.LaunchAppHelper
import maderski.bluetoothautoplaymusic.helpers.PackageHelper
import maderski.bluetoothautoplaymusic.utils.PermissionUtils
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.helpers.PackageHelper.MapApps.*
import maderski.bluetoothautoplaymusic.helpers.PackageHelper.MediaPlayers.*
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences

class HomeFragment : androidx.fragment.app.Fragment() {

    private lateinit var mFirebaseHelper: FirebaseHelper

    private val packageHelper: PackageHelper = PackageHelper(requireContext())
    private val radioButtonIndex: Int
        get() {
            val selectedMusicPlayer = BAPMPreferences.getPkgSelectedMusicPlayer(requireActivity())
            return installedMediaPlayers.indexOf(selectedMusicPlayer)
        }

    private var installedMediaPlayers: Set<String> = mutableSetOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseHelper = FirebaseHelper(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_home, container, false)
        mapsToggleButton(rootView)
        launchMusicPlayerToggleButton(rootView)
        autoplayOnlyButton(rootView)
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
        val mapAppChoice = BAPMPreferences.getMapsChoice(requireActivity())
        if (mapAppChoice.equals(WAZE.packageName, ignoreCase = true)) {
            val launchAppHelper = LaunchAppHelper(requireActivity())
            val isWazeOnPhone = launchAppHelper.isAbleToLaunch(WAZE.packageName)
            if (isWazeOnPhone.not()) {
                Log.d(TAG, "Checked")
                BAPMPreferences.setMapsChoice(context, MAPS.packageName)
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
        val listOfBTDevices = BluetoothUtils.listOfBluetoothDevices(requireActivity())
        val noBTDeviceFoundMsg = resources.getString(R.string.no_BT_found)
        val isNoBTDevice = listOfBTDevices.contains(noBTDeviceFoundMsg) || listOfBTDevices.isEmpty()
        if (isNoBTDevice) {
            textView = TextView(context)
            textView.setText(R.string.no_BT_found)
            btDeviceCkBoxLL.addView(textView)
        } else {
            listOfBTDevices.forEach { btDevice ->
                var textColor = R.color.colorPrimary
                checkBox = CheckBox(context)
                checkBox.text = btDevice

                val isHeadphonesDevice = BAPMPreferences.getHeadphoneDevices(requireActivity()).contains(btDevice)
                if (isHeadphonesDevice) {
                    textColor = R.color.lightGray
                    val states = arrayOf(intArrayOf(android.R.attr.state_checked))
                    val colors = intArrayOf(textColor, textColor)
                    CompoundButtonCompat.setButtonTintList(checkBox, ColorStateList(states, colors))
                    checkBox.isClickable = false
                    checkBox.isChecked = true
                } else {
                    val isSelectedBTDevice = BAPMPreferences.getBTDevices(context).contains(btDevice)
                    checkBox.isChecked = isSelectedBTDevice
                    checkboxListener(checkBox, btDevice, context)
                }

                checkBox.setTextColor(ContextCompat.getColor(context, textColor))
                checkBox.typeface = Typeface.createFromAsset(context.assets, "fonts/TitilliumText400wt.otf")

                btDeviceCkBoxLL.addView(checkBox)
            }
        }
    }

    //Get Selected Checkboxes
    private fun checkboxListener(checkBox: CheckBox, BTDevice: String, context: Context) {

        val saveBTDevices = HashSet(BAPMPreferences.getBTDevices(context))

        checkBox.setOnClickListener {
            if (checkBox.isChecked) {
                saveBTDevices.add(BTDevice)
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "TRUE $BTDevice")
                }
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "SAVED")
                }
            } else {
                saveBTDevices.remove(BTDevice)
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "FALSE $BTDevice")
                }
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "SAVED")
                }
            }
            BAPMPreferences.setBTDevices(context, saveBTDevices)
            mFirebaseHelper.deviceAdd(SelectionConstants.BLUETOOTH_DEVICE, BTDevice, checkBox.isChecked)
        }
    }

    //Get list of installed Mediaplayers and create Radiobuttons
    private fun radiobuttonCreator(view: View, context: Context) {
        var rdoButton: RadioButton
        var appInfo: ApplicationInfo
        var mediaPlayer = "No Name"
        val pm = context.packageManager

        val rdoMPGroup = view.findViewById<View>(R.id.rdoMusicPlayers) as RadioGroup
        rdoMPGroup.removeAllViews()

        for (packageName in installedMediaPlayers) {

            try {
                appInfo = pm.getApplicationInfo(packageName, 0)
                mediaPlayer = pm.getApplicationLabel(appInfo).toString()

            } catch (e: Exception) {
                Log.e(TAG, e.message)
            }

            val color = ContextCompat.getColor(context, R.color.colorPrimary)

            rdoButton = RadioButton(context)
            rdoButton.text = mediaPlayer
            rdoButton.setTextColor(color)
            rdoButton.typeface = Typeface.createFromAsset(context.assets, "fonts/TitilliumText400wt.otf")
            rdoMPGroup.addView(rdoButton)
        }
    }

    //Get Selected Radiobutton
    private fun radioButtonListener(view: View, context: Context) {
        val group = view.findViewById<View>(R.id.rdoMusicPlayers) as RadioGroup
        group.setOnCheckedChangeListener { radioGroup, i ->
            val radioButton = radioGroup.findViewById<View>(i)
            val index = radioGroup.indexOfChild(radioButton)
            val packageName = installedMediaPlayers.toList()[index]

            BAPMPreferences.setPkgSelectedMusicPlayer(context, packageName)

            // Firebase analytics
            val selectedMusicPlayer = BAPMPreferences.getPkgSelectedMusicPlayer(context)
            val hasMusicPlayerChanged = selectedMusicPlayer.equals(packageName, ignoreCase = true).not()
            mFirebaseHelper.musicPlayerChoice(context, packageName, hasMusicPlayerChanged)

            setAppleMusicRequirements(view, context, packageName)

            Log.d(TAG, Integer.toString(index))
            Log.d(TAG, "Selected Music Player: $selectedMusicPlayer")
            Log.d(TAG, Integer.toString(radioGroup.checkedRadioButtonId))
        }
    }

    private fun setAppleMusicRequirements(view: View, context: Context, packageName: String) {
        // Set Launch App and Unlock screen to true since it is required by Apple Music to play
        if (packageName == APPLE_MUSIC.packageName) {
            val autoplayOnly = BAPMPreferences.getHeadphoneDevices(context)
            if (!autoplayOnly.isEmpty()) {
                BAPMPreferences.setHeadphoneDevices(context, HashSet())
                checkboxCreator(view, requireActivity())
                Toast.makeText(getContext(), "Autoplay ONLY not supported with Apple Music", Toast.LENGTH_LONG).show()
            }

            val launchMusicPlayerToggleButton = view.findViewById<View>(R.id.LaunchMusicPlayerToggleButton) as ToggleButton
            val unlockScreenToggleButton = view.findViewById<View>(R.id.UnlockToggleButton) as ToggleButton
            val launchMapsToggleButton = view.findViewById<View>(R.id.MapsToggleButton) as ToggleButton
            if (!BAPMPreferences.getLaunchMusicPlayer(context) || !BAPMPreferences.getUnlockScreen(context)
                    || BAPMPreferences.getLaunchGoogleMaps(context)) {

                if (BAPMPreferences.getLaunchGoogleMaps(context)) {
                    Toast.makeText(context, "Launching of Maps/Waze not supported with Apple music", Toast.LENGTH_LONG).show()
                }

                launchMusicPlayerToggleButton.isChecked = true
                unlockScreenToggleButton.isChecked = true
                launchMapsToggleButton.isChecked = false
                BAPMPreferences.setLaunchMusicPlayer(context, true)
                BAPMPreferences.setUnlockScreen(context, true)
                BAPMPreferences.setLaunchGoogleMaps(context, false)
            }
        }
    }

    //Change the Maps button text to Maps or Waze depending on what Maps the user is launching
    private fun setMapsButtonText(view: View, context: Context) {
        var mapChoice = BAPMPreferences.getMapsChoice(context)
        val packageManager = context.packageManager
        mapChoice = try {
            val appInfo = packageManager.getApplicationInfo(mapChoice, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            Log.e(TAG, e.message)
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

        btnState = BAPMPreferences.getLaunchGoogleMaps(context)
        toggleButton.isChecked = btnState

        btnState = BAPMPreferences.getKeepScreenON(context)
        toggleButton = view.findViewById<View>(R.id.KeepONToggleButton) as ToggleButton
        toggleButton.isChecked = btnState

        btnState = BAPMPreferences.getPriorityMode(context)
        toggleButton = view.findViewById<View>(R.id.PriorityToggleButton) as ToggleButton
        toggleButton.isChecked = btnState

        btnState = BAPMPreferences.getMaxVolume(context)
        toggleButton = view.findViewById<View>(R.id.VolumeMAXToggleButton) as ToggleButton
        toggleButton.isChecked = btnState

        btnState = BAPMPreferences.getLaunchMusicPlayer(context)
        toggleButton = view.findViewById<View>(R.id.LaunchMusicPlayerToggleButton) as ToggleButton
        toggleButton.isChecked = btnState

        btnState = BAPMPreferences.getUnlockScreen(context)
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
            val error = if (e.message == null) "RadioButton Error" else e.message
            Log.e(TAG, error)
        }

    }

    fun autoplayOnlyButton(view: View) {
        val autoplayOnlyButton = view.findViewById<View>(R.id.autoplay_only_button) as Button
        autoplayOnlyButton.setOnClickListener(View.OnClickListener {
            // Display message that Apple Music not supported by autoplay only
            if (BAPMPreferences.getPkgSelectedMusicPlayer(requireActivity()) == APPLE_MUSIC.packageName) {
                Toast.makeText(context, "Autoplay ONLY not supported with Apple Music", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }

            mFirebaseHelper.selectionMade(SelectionConstants.SET_AUTOPLAY_ONLY)
            val newFragment = HeadphonesFragment.newInstance()
            newFragment.show(requireActivity().supportFragmentManager, "autoplayOnlyFragment")
        })
    }

    //***Toggle button actions are below, basically set SharedPref value for specified button***
    fun mapsToggleButton(view: View) {
        val mapsToggleButton = view.findViewById<View>(R.id.MapsToggleButton) as Button
        mapsToggleButton.setOnClickListener { mapsToggleButtonView ->
            val on = (mapsToggleButtonView as ToggleButton).isChecked
            mFirebaseHelper.featureEnabled(FeatureConstants.LAUNCH_MAPS, on)
            if (on) {
                BAPMPreferences.setLaunchGoogleMaps(requireActivity(), true)
                Log.i(TAG, "MapButton is ON")
                Log.i(TAG, "Dismiss Keyguard is ON")
            } else {
                BAPMPreferences.setLaunchGoogleMaps(requireActivity(), false)
                Log.d(TAG, "MapButton is OFF")
            }
        }
    }

    fun keepONToggleButton(view: View) {
        val keepOnToggleButton = view.findViewById<View>(R.id.KeepONToggleButton) as Button
        keepOnToggleButton.setOnClickListener { keepOnToggleButtonView ->
            val on = (keepOnToggleButtonView as ToggleButton).isChecked
            mFirebaseHelper.featureEnabled(FeatureConstants.KEEP_SCREEN_ON, on)
            if (on) {
                BAPMPreferences.setKeepScreenON(requireActivity(), true)
                Log.d(TAG, "Keep Screen ON Button is ON")
            } else {
                BAPMPreferences.setKeepScreenON(requireActivity(), false)
                Log.d(TAG, "Keep Screen ON Button is OFF")
            }
        }
    }

    fun priorityToggleButton(view: View) {
        val priorityToggleButton = view.findViewById<View>(R.id.PriorityToggleButton) as Button
        priorityToggleButton.setOnClickListener { view ->
            val on = (view as ToggleButton).isChecked
            mFirebaseHelper.featureEnabled(FeatureConstants.PRIORITY_MODE, on)
            if (on) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PermissionUtils.checkDoNotDisturbPermission(requireActivity(), 0)
                }
                BAPMPreferences.setPriorityMode(requireActivity(), true)
                Log.d(TAG, "Priority Button is ON")
            } else {
                BAPMPreferences.setPriorityMode(requireActivity(), false)
                Log.d(TAG, "Priority Button is OFF")
            }
        }
    }

    fun volumeMAXToggleButton(view: View) {
        val volumeMAXToggleButton = view.findViewById<View>(R.id.VolumeMAXToggleButton) as Button
        volumeMAXToggleButton.setOnClickListener { keepOnToggleButtonViewView ->
            val on = (keepOnToggleButtonViewView as ToggleButton).isChecked
            mFirebaseHelper.featureEnabled(FeatureConstants.MAX_VOLUME, on)
            if (on) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    PermissionUtils.checkDoNotDisturbPermission(requireActivity(), 0)
                }
                BAPMPreferences.setMaxVolume(requireActivity(), true)
                Log.d(TAG, "Max Volume Button is ON")
            } else {
                BAPMPreferences.setMaxVolume(requireActivity(), false)
                Log.d(TAG, "Max Volume Button is OFF")
            }
        }
    }

    fun launchMusicPlayerToggleButton(view: View) {
        val launchMusicPlayerToggleButton = view.findViewById<View>(R.id.LaunchMusicPlayerToggleButton) as Button
        launchMusicPlayerToggleButton.setOnClickListener { launchMusicPlayerToggleButtonView ->
            val on = (launchMusicPlayerToggleButtonView as ToggleButton).isChecked
            mFirebaseHelper.featureEnabled(FeatureConstants.LAUNCH_MUSIC_PLAYER, on)
            if (on) {
                BAPMPreferences.setLaunchMusicPlayer(requireActivity(), true)
                Log.d(TAG, "Launch Music Player Button is ON")
            } else {
                BAPMPreferences.setLaunchMusicPlayer(requireActivity(), false)
                Log.d(TAG, "Launch Music Player Button is OFF")
            }
        }
    }

    fun unlockScreenToggleButton(view: View) {
        val unlockScreenToggleButton = view.findViewById<View>(R.id.UnlockToggleButton) as Button
        unlockScreenToggleButton.setOnClickListener { unlockScreenToggleButtonView ->
            val on = (unlockScreenToggleButtonView as ToggleButton).isChecked
            mFirebaseHelper.featureEnabled(FeatureConstants.DISMISS_KEYGUARD, on)
            if (on) {
                BAPMPreferences.setUnlockScreen(requireActivity(), true)
                Log.i(TAG, "Dismiss KeyGuard Button is ON")
            } else {
                BAPMPreferences.setUnlockScreen(requireActivity(), false)
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
