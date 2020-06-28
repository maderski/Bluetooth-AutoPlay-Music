package maderski.bluetoothautoplaymusic.ui.fragments

import android.Manifest
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.analytics.constants.OptionConstants
import maderski.bluetoothautoplaymusic.controls.VolumeControl
import maderski.bluetoothautoplaymusic.permission.PermissionManager
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import org.koin.android.ext.android.inject

class OptionsFragment : androidx.fragment.app.Fragment() {
    private val preferences: BAPMPreferences by inject()
    private val volumeControl: VolumeControl by inject()
    private val permissionManager: PermissionManager by inject()

    private lateinit var mFirebaseHelper: FirebaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mFirebaseHelper = FirebaseHelper(requireActivity())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentActivity = requireActivity()

        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_options, container, false)

        autoPlaySwitch(rootView)
        autoBrightnessSwitch(rootView)
        powerConnectedSwitch(rootView)
        sendToBackgroundSwitch(rootView)
        waitTillOffPhoneSwitch(rootView)
        brightBrightnessButton(rootView)
        dimBrightnessButton(rootView)
        usePriorityModeSwitch(rootView)

        setFonts(rootView, fragmentActivity)
        setButtonPreferences(rootView, fragmentActivity)
        setMaxVolumeSeekBar(rootView)
        setupRestoreOriginalVolumeCheckBox(rootView)

        return rootView
    }

    private fun setButtonPreferences(view: View, context: Context) {
        var btnState: Boolean?
        var settingSwitch: Switch

        btnState = preferences.getAutoPlayMusic()
        settingSwitch = view.findViewById(R.id.auto_play)
        settingSwitch.isChecked = btnState

        btnState = preferences.getPowerConnected()
        settingSwitch = view.findViewById(R.id.power_connected)
        settingSwitch.isChecked = btnState

        btnState = preferences.getSendToBackground()
        settingSwitch = view.findViewById(R.id.send_to_background)
        settingSwitch.isChecked = btnState

        btnState = preferences.getWaitTillOffPhone()
        settingSwitch = view.findViewById(R.id.wait_till_off_phone)
        settingSwitch.isChecked = btnState

        if (!permissionManager.isLocationPermissionGranted())
            preferences.setAutoBrightness(false)
        btnState = preferences.getAutoBrightness()
        settingSwitch = view.findViewById(R.id.auto_brightness)
        settingSwitch.isChecked = btnState

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btnState = preferences.getUsePriorityMode()
            settingSwitch = view.findViewById(R.id.sw_priority_mode)
            val switchExplaination = view.findViewById<TextView>(R.id.tv_priority_mode_explaination)

            settingSwitch.visibility = View.VISIBLE
            settingSwitch.isChecked = btnState

            switchExplaination.visibility = View.VISIBLE
        }

    }

    fun autoPlaySwitch(view: View) {
        val autoPlaySwitch = view.findViewById<Switch>(R.id.auto_play)
        autoPlaySwitch.setOnClickListener { autoPlaySwitchView ->
            val on = (autoPlaySwitchView as Switch).isChecked
            mFirebaseHelper.featureEnabled(OptionConstants.PLAY_MUSIC, on)
            if (on) {
                preferences.setAutoplayMusic(true)
                Log.d(TAG, "AutoPlaySwitch is ON")
            } else {
                preferences.setAutoplayMusic(false)
                Log.d(TAG, "AutoPlaySwitch is OFF")
            }
        }
    }

    fun powerConnectedSwitch(view: View) {
        val powerConnectedSwitch = view.findViewById<Switch>(R.id.power_connected)
        powerConnectedSwitch.setOnClickListener { powerConnectedSwitchView ->
            val on = (powerConnectedSwitchView as Switch).isChecked
            mFirebaseHelper.featureEnabled(OptionConstants.POWER_REQUIRED, on)
            if (on) {
                preferences.setPowerConnected(true)
                Log.d(TAG, "PowerConnected Switch is ON")
            } else {
                preferences.setPowerConnected(false)
                Log.d(TAG, "PowerConnected Switch is OFF")
            }
        }
    }

    fun sendToBackgroundSwitch(view: View) {
        val sendToBackgroundSwitch = view.findViewById<View>(R.id.send_to_background)
        sendToBackgroundSwitch.setOnClickListener { sendToBackgroundSwitchView ->
            val on = (sendToBackgroundSwitchView as Switch).isChecked
            mFirebaseHelper.featureEnabled(OptionConstants.GO_HOME, on)
            if (on) {
                preferences.setSendToBackground(true)
                Log.d(TAG, "SendToBackground Switch is ON")
            } else {
                preferences.setSendToBackground(false)
                Log.d(TAG, "SendToBackground Switch is OFF")
            }
        }
    }

    fun waitTillOffPhoneSwitch(view: View) {
        val waitTillOffPhoneSwitch = view.findViewById<View>(R.id.wait_till_off_phone)
        waitTillOffPhoneSwitch.setOnClickListener { waitTillOffPhoneSwitchView ->
            val on = (waitTillOffPhoneSwitchView as Switch).isChecked
            mFirebaseHelper.featureEnabled(OptionConstants.CALL_COMPLETED, on)
            if (on) {
                preferences.setWaitTillOffPhone(true)
                Log.d(TAG, "WaitTillOffPhone Switch is ON")
            } else {
                preferences.setWaitTillOffPhone(false)
                Log.d(TAG, "WaitTillOffPhone Switch is OFF")
            }
        }
    }

    fun autoBrightnessSwitch(view: View) {
        val autoBrightnessSwitch = view.findViewById<View>(R.id.auto_brightness)
        autoBrightnessSwitch.setOnClickListener { autoBrightnessSwitchView ->
            val on = (autoBrightnessSwitchView as Switch).isChecked
            mFirebaseHelper.featureEnabled(OptionConstants.AUTO_BRIGHTNESS, on)
            if (on) {
                permissionManager.checkLocationPermission(requireActivity())

                preferences.setAutoBrightness(true)

                Log.d(TAG, "AutoBrightness Switch is ON")
            } else {
                preferences.setAutoBrightness(false)
                Log.d(TAG, "AutoBrightness Switch is OFF")
            }
        }
    }

    fun usePriorityModeSwitch(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val priorityModeSwitch = view.findViewById<View>(R.id.sw_priority_mode)
            priorityModeSwitch.setOnClickListener { priorityModeSwitchView ->
                val on = (priorityModeSwitchView as Switch).isChecked
                mFirebaseHelper.featureEnabled(OptionConstants.PRIORITY_MODE, on)
                if (on) {
                    permissionManager.checkAccessNotificationPolicyPermission(requireActivity())

                    preferences.setUsePriorityMode(true)

                    Log.d(TAG, "AutoBrightness Switch is ON")
                } else {
                    preferences.setUsePriorityMode(false)
                    Log.d(TAG, "AutoBrightness Switch is OFF")
                }
            }
        }
    }

    fun dimBrightnessButton(view: View) {
        val dimBrightnessButton = view.findViewById<Button>(R.id.dimtimebutton)
        dimBrightnessButton.setOnClickListener {
            val newFragment = TimePickerFragment.newInstance(TimePickerFragment.SCREEN_BRIGHTNESS_TIME, true, preferences.getDimTime(), "Set Dim Time")
            newFragment.show(requireActivity().supportFragmentManager, "timePicker")
        }
    }

    fun brightBrightnessButton(view: View) {
        val brightBrightnessButton = view.findViewById<Button>(R.id.brighttimebutton)
        brightBrightnessButton.setOnClickListener {
            val newFragment = TimePickerFragment.newInstance(TimePickerFragment.SCREEN_BRIGHTNESS_TIME, false, preferences.getBrightTime(), "Set Bright Time")
            newFragment.show(requireActivity().supportFragmentManager, "timePicker")
        }
    }

    fun setMaxVolumeSeekBar(view: View) {
        val deviceMaxVolume = volumeControl.getDeviceMaxVolume()
        val volumeSeekBar = view.findViewById<SeekBar>(R.id.max_volume_seekBar)

        volumeSeekBar.max = volumeControl.getDeviceMaxVolume()
        volumeSeekBar.progress = preferences.getUserSetMaxVolume(deviceMaxVolume)
        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                preferences.setUserSetMaxVolume(progress)
                Log.d(TAG, "User set MAX volume: " + Integer.toString(preferences.getUserSetMaxVolume(deviceMaxVolume)))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
    }

    fun setupRestoreOriginalVolumeCheckBox(view: View) {
        val isEnabled = preferences.getRestoreNotificationVolume()
        val restoreVolumeCheckBox = view.findViewById<CheckBox>(R.id.cb_restore_original_volume)

        restoreVolumeCheckBox.isChecked = isEnabled

        restoreVolumeCheckBox.setOnCheckedChangeListener { buttonView, isChecked -> preferences.setRestoreNotificationVolume(isChecked) }
    }

    private fun setFonts(view: View, context: Context) {
        val typeface_bold = Typeface.createFromAsset(context.assets, "fonts/TitilliumText600wt.otf")

        var textView = view.findViewById<TextView>(R.id.settingsText)
        textView.typeface = typeface_bold

        textView = view.findViewById(R.id.auto_play)
        textView.typeface = typeface_bold

        textView = view.findViewById(R.id.power_connected)
        textView.typeface = typeface_bold

        textView = view.findViewById(R.id.send_to_background)
        textView.typeface = typeface_bold

        textView = view.findViewById(R.id.wait_till_off_phone)
        textView.typeface = typeface_bold

        textView = view.findViewById(R.id.auto_brightness)
        textView.typeface = typeface_bold

        textView = view.findViewById(R.id.manualBrtLabel)
        textView.typeface = typeface_bold

        textView = view.findViewById(R.id.userSetMaxVolumeLabel)
        textView.typeface = typeface_bold
    }

    companion object {
        private val TAG = "OptionsFragment"

        fun newInstance(): OptionsFragment {
            val fragment = OptionsFragment()
            val args = Bundle()

            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
