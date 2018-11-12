package maderski.bluetoothautoplaymusic.ui.fragments

import android.Manifest
import android.content.Context
import android.graphics.Typeface
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView

import maderski.bluetoothautoplaymusic.analytics.FirebaseHelper
import maderski.bluetoothautoplaymusic.analytics.constants.OptionConstants
import maderski.bluetoothautoplaymusic.analytics.constants.SelectionConstants
import maderski.bluetoothautoplaymusic.helpers.PermissionHelper
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences

class OptionsFragment : Fragment() {

    private var mFirebaseHelper: FirebaseHelper = FirebaseHelper(requireActivity())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        showNotificationSwitch(rootView)
        wifiOffUseTimeSpansSwitch(rootView)
        brightBrightnessButton(rootView)
        dimBrightnessButton(rootView)
        usePriorityModeSwitch(rootView)

        setFonts(rootView, fragmentActivity)
        setButtonPreferences(rootView, fragmentActivity)
        setMaxVolumeSeekBar(rootView)
        setupRestoreOriginalVolumeCheckBox(rootView)

        wifiOffDeviceButton(rootView)

        setWifiUseTimeSpansCaption(rootView)

        return rootView
    }

    private fun setWifiUseTimeSpansCaption(view: View) {
        val captionText: String = if (BAPMPreferences.getCanLaunchDirections(requireActivity())) {
            view.resources.getString(R.string.wifi_use_time_spans_caption_work_home)
        } else {
            view.resources.getString(R.string.wifi_use_time_spans_caption_morning_evening)
        }
        val wifiUseTimeSpanCaption = view.findViewById<View>(R.id.wifi_use_time_spans_desc) as TextView

        wifiUseTimeSpanCaption.text = captionText
    }

    private fun setButtonPreferences(view: View, context: Context) {
        var btnState: Boolean?
        var settingSwitch: Switch

        btnState = BAPMPreferences.getAutoPlayMusic(context)
        settingSwitch = view.findViewById<View>(R.id.auto_play) as Switch
        settingSwitch.isChecked = btnState

        btnState = BAPMPreferences.getPowerConnected(context)
        settingSwitch = view.findViewById<View>(R.id.power_connected) as Switch
        settingSwitch.isChecked = btnState

        btnState = BAPMPreferences.getSendToBackground(context)
        settingSwitch = view.findViewById<View>(R.id.send_to_background) as Switch
        settingSwitch.isChecked = btnState

        btnState = BAPMPreferences.getWaitTillOffPhone(context)
        settingSwitch = view.findViewById<View>(R.id.wait_till_off_phone) as Switch
        settingSwitch.isChecked = btnState

        btnState = BAPMPreferences.getShowNotification(context)
        settingSwitch = view.findViewById<View>(R.id.show_notification) as Switch
        settingSwitch.isChecked = btnState

        btnState = BAPMPreferences.getWifiUseMapTimeSpans(context)
        settingSwitch = view.findViewById<View>(R.id.wifi_use_time_spans) as Switch
        settingSwitch.isChecked = btnState

        if (!PermissionHelper.isPermissionGranted(context, PermissionHelper.COARSE_LOCATION))
            BAPMPreferences.setAutoBrightness(context, false)
        btnState = BAPMPreferences.getAutoBrightness(context)
        settingSwitch = view.findViewById<View>(R.id.auto_brightness) as Switch
        settingSwitch.isChecked = btnState

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            btnState = BAPMPreferences.getUsePriorityMode(context)
            settingSwitch = view.findViewById<View>(R.id.sw_priority_mode) as Switch
            val switch_explaination = view.findViewById<View>(R.id.tv_priority_mode_explaination) as TextView

            settingSwitch.visibility = View.VISIBLE
            settingSwitch.isChecked = btnState

            switch_explaination.visibility = View.VISIBLE
        }

    }

    fun wifiOffUseTimeSpansSwitch(view: View) {
        val wifiUseTimeSpansSwitch = view.findViewById<View>(R.id.wifi_use_time_spans) as Switch
        wifiUseTimeSpansSwitch.setOnClickListener { wifiUseTimeSpansSwitchView ->
            val on = (wifiUseTimeSpansSwitchView as Switch).isChecked
            mFirebaseHelper.featureEnabled(OptionConstants.WIFI_OFF_USE_TIME_SPANS, on)
            if (on) {
                BAPMPreferences.setWifiUseMapTimeSpans(requireActivity(), true)
                Log.d(TAG, "WIFI OFF Use Time Spans Switch is ON")
            } else {
                BAPMPreferences.setWifiUseMapTimeSpans(requireActivity(), false)
                Log.d(TAG, "WIFI OFF Use Time Spans Switch is OFF")
            }
        }
    }

    fun autoPlaySwitch(view: View) {
        val autoPlaySwitch = view.findViewById<View>(R.id.auto_play) as Switch
        autoPlaySwitch.setOnClickListener { autoPlaySwitchView ->
            val on = (autoPlaySwitchView as Switch).isChecked
            mFirebaseHelper.featureEnabled(OptionConstants.PLAY_MUSIC, on)
            if (on) {
                BAPMPreferences.setAutoplayMusic(requireActivity(), true)
                Log.d(TAG, "AutoPlaySwitch is ON")
            } else {
                BAPMPreferences.setAutoplayMusic(requireActivity(), false)
                Log.d(TAG, "AutoPlaySwitch is OFF")
            }
        }
    }

    fun powerConnectedSwitch(view: View) {
        val powerConnectedSwitch = view.findViewById<View>(R.id.power_connected) as Switch
        powerConnectedSwitch.setOnClickListener { powerConnectedSwitchView ->
            val on = (powerConnectedSwitchView as Switch).isChecked
            mFirebaseHelper.featureEnabled(OptionConstants.POWER_REQUIRED, on)
            if (on) {
                BAPMPreferences.setPowerConnected(requireActivity(), true)
                Log.d(TAG, "PowerConnected Switch is ON")
            } else {
                BAPMPreferences.setPowerConnected(requireActivity(), false)
                Log.d(TAG, "PowerConnected Switch is OFF")
            }
        }
    }

    fun sendToBackgroundSwitch(view: View) {
        val sendToBackgroundSwitch = view.findViewById<View>(R.id.send_to_background) as Switch
        sendToBackgroundSwitch.setOnClickListener { sendToBackgroundSwitchView ->
            val on = (sendToBackgroundSwitchView as Switch).isChecked
            mFirebaseHelper.featureEnabled(OptionConstants.GO_HOME, on)
            if (on) {
                BAPMPreferences.setSendToBackground(requireActivity(), true)
                Log.d(TAG, "SendToBackground Switch is ON")
            } else {
                BAPMPreferences.setSendToBackground(requireActivity(), false)
                Log.d(TAG, "SendToBackground Switch is OFF")
            }
        }
    }

    fun waitTillOffPhoneSwitch(view: View) {
        val waitTillOffPhoneSwitch = view.findViewById<View>(R.id.wait_till_off_phone) as Switch
        waitTillOffPhoneSwitch.setOnClickListener { waitTillOffPhoneSwitchView ->
            val on = (waitTillOffPhoneSwitchView as Switch).isChecked
            mFirebaseHelper.featureEnabled(OptionConstants.CALL_COMPLETED, on)
            if (on) {
                BAPMPreferences.setWaitTillOffPhone(requireActivity(), true)
                Log.d(TAG, "WaitTillOffPhone Switch is ON")
            } else {
                BAPMPreferences.setWaitTillOffPhone(requireActivity(), false)
                Log.d(TAG, "WaitTillOffPhone Switch is OFF")
            }
        }
    }

    fun showNotificationSwitch(view: View) {
        val autoBrightnessSwitch = view.findViewById<View>(R.id.show_notification) as Switch
        autoBrightnessSwitch.setOnClickListener { showNotificationSwitchView ->
            val on = (showNotificationSwitchView as Switch).isChecked
            mFirebaseHelper.featureEnabled(OptionConstants.SHOW_NOTIFICATION, on)
            if (on) {
                BAPMPreferences.setShowNotification(requireActivity(), true)
                Log.d(TAG, "Show Notification Switch is ON")
            } else {
                BAPMPreferences.setShowNotification(requireActivity(), false)
                Log.d(TAG, "Show Notification Switch is OFF")
            }
        }
    }

    fun autoBrightnessSwitch(view: View) {
        val autoBrightnessSwitch = view.findViewById<View>(R.id.auto_brightness) as Switch
        autoBrightnessSwitch.setOnClickListener { autoBrightnessSwitchView ->
            val on = (autoBrightnessSwitchView as Switch).isChecked
            mFirebaseHelper.featureEnabled(OptionConstants.AUTO_BRIGHTNESS, on)
            if (on) {
                PermissionHelper.checkPermission(requireActivity(), PermissionHelper.COARSE_LOCATION)

                BAPMPreferences.setAutoBrightness(requireActivity(), true)

                Log.d(TAG, "AutoBrightness Switch is ON")
            } else {
                BAPMPreferences.setAutoBrightness(requireActivity(), false)
                Log.d(TAG, "AutoBrightness Switch is OFF")
            }
        }
    }

    fun usePriorityModeSwitch(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val priorityModeSwitch = view.findViewById<View>(R.id.sw_priority_mode) as Switch
            priorityModeSwitch.setOnClickListener { priorityModeSwitchView ->
                val on = (priorityModeSwitchView as Switch).isChecked
                mFirebaseHelper.featureEnabled(OptionConstants.PRIORITY_MODE, on)
                if (on) {
                    val permission = Manifest.permission.ACCESS_NOTIFICATION_POLICY
                    PermissionHelper.checkPermission(requireActivity(), permission)

                    BAPMPreferences.setUsePriorityMode(requireActivity(), true)

                    Log.d(TAG, "AutoBrightness Switch is ON")
                } else {
                    BAPMPreferences.setUsePriorityMode(requireActivity(), false)
                    Log.d(TAG, "AutoBrightness Switch is OFF")
                }
            }
        }
    }

    fun dimBrightnessButton(view: View) {
        val dimBrightnessButton = view.findViewById<View>(R.id.dimtimebutton) as Button
        dimBrightnessButton.setOnClickListener {
            val newFragment = TimePickerFragment.newInstance(TimePickerFragment.SCREEN_BRIGHTNESS_TIME, true, BAPMPreferences.getDimTime(requireActivity()), "Set Dim Time")
            newFragment.show(requireActivity().supportFragmentManager, "timePicker")
        }
    }

    fun brightBrightnessButton(view: View) {
        val brightBrightnessButton = view.findViewById<View>(R.id.brighttimebutton) as Button
        brightBrightnessButton.setOnClickListener {
            val newFragment = TimePickerFragment.newInstance(TimePickerFragment.SCREEN_BRIGHTNESS_TIME, false, BAPMPreferences.getBrightTime(requireActivity()), "Set Bright Time")
            newFragment.show(requireActivity().supportFragmentManager, "timePicker")
        }
    }

    fun setMaxVolumeSeekBar(view: View) {
        val audioManager = requireActivity().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val volumeSeekBar = view.findViewById<View>(R.id.max_volume_seekBar) as SeekBar

        volumeSeekBar.max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        volumeSeekBar.progress = BAPMPreferences.getUserSetMaxVolume(requireActivity())
        volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                BAPMPreferences.setUserSetMaxVolume(requireActivity().applicationContext, progress)
                Log.d(TAG, "User set MAX volume: " + Integer.toString(BAPMPreferences.getUserSetMaxVolume(requireActivity().applicationContext)))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
    }

    fun setupRestoreOriginalVolumeCheckBox(view: View) {
        val context = view.context
        val isEnabled = BAPMPreferences.getRestoreNotificationVolume(context)
        val restoreVolumeCheckBox = view.findViewById<View>(R.id.cb_restore_original_volume) as CheckBox

        restoreVolumeCheckBox.isChecked = isEnabled

        restoreVolumeCheckBox.setOnCheckedChangeListener { buttonView, isChecked -> BAPMPreferences.setRestoreNotificationVolume(context, isChecked) }
    }

    fun wifiOffDeviceButton(view: View) {
        val wifiOffDeviceButton = view.findViewById<View>(R.id.wifi_off_button) as Button
        wifiOffDeviceButton.setOnClickListener {
            mFirebaseHelper!!.selectionMade(SelectionConstants.SET_WIFI_OFF_DEVICE)
            val newFragment = WifiOffFragment.newInstance()
            newFragment.show(requireActivity().supportFragmentManager, "wifiOffFragment")
        }
    }

    private fun setFonts(view: View, context: Context) {
        val typeface_bold = Typeface.createFromAsset(context.assets, "fonts/TitilliumText600wt.otf")

        var textView = view.findViewById<View>(R.id.settingsText) as TextView
        textView.typeface = typeface_bold

        textView = view.findViewById<View>(R.id.auto_play) as TextView
        textView.typeface = typeface_bold

        textView = view.findViewById<View>(R.id.power_connected) as TextView
        textView.typeface = typeface_bold

        textView = view.findViewById<View>(R.id.send_to_background) as TextView
        textView.typeface = typeface_bold

        textView = view.findViewById<View>(R.id.wait_till_off_phone) as TextView
        textView.typeface = typeface_bold

        textView = view.findViewById<View>(R.id.auto_brightness) as TextView
        textView.typeface = typeface_bold

        textView = view.findViewById<View>(R.id.manualBrtLabel) as TextView
        textView.typeface = typeface_bold

        textView = view.findViewById<View>(R.id.userSetMaxVolumeLabel) as TextView
        textView.typeface = typeface_bold

        textView = view.findViewById<View>(R.id.show_notification) as TextView
        textView.typeface = typeface_bold

        textView = view.findViewById<View>(R.id.wifi_use_time_spans) as TextView
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
