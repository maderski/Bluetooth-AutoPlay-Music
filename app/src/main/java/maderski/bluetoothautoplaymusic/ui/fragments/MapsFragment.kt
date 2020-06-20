package maderski.bluetoothautoplaymusic.ui.fragments

import android.content.Context
import android.content.pm.ApplicationInfo
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.helpers.LaunchHelper
import maderski.bluetoothautoplaymusic.helpers.TimeHelper
import maderski.bluetoothautoplaymusic.helpers.enums.MapApps.MAPS
import maderski.bluetoothautoplaymusic.helpers.enums.MapApps.WAZE
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import org.koin.android.ext.android.inject
import java.util.*

class MapsFragment : Fragment() {
    interface OnFragmentInteractionListener {
        fun onLocationNameSet(locationName: String)
    }

    private val preferences: BAPMPreferences by inject()
    private val launchHelper: LaunchHelper by inject()

    private val mMapChoicesAvailable = ArrayList<String>()
    private var mCanLaunchDirections = false

    private var mLocationNameEditText: EditText? = null
    private var fragmentInteractionListener: OnFragmentInteractionListener? = null

    private val mTextChangeHandler = Handler(Looper.getMainLooper())
    private val mTextChangeRunnable = Runnable {
        val enteredText = mLocationNameEditText?.text.toString()
        fragmentInteractionListener?.onLocationNameSet(enteredText)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            fragmentInteractionListener = context
        } else {
            throw RuntimeException("$context must OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        fragmentInteractionListener = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_maps, container, false)

        val typeface_bold = Typeface.createFromAsset(requireActivity().assets, "fonts/TitilliumText600wt.otf")
        var textView = rootView.findViewById<View>(R.id.map_options_text) as TextView
        textView.typeface = typeface_bold

        textView = rootView.findViewById<View>(R.id.daysToLaunchLabel) as TextView
        textView.typeface = typeface_bold

        textView = rootView.findViewById<View>(R.id.map_app_choice) as TextView
        textView.typeface = typeface_bold

        textView = rootView.findViewById<View>(R.id.morning_timespan_label) as TextView
        textView.typeface = typeface_bold

        textView = rootView.findViewById<View>(R.id.evening_timespan_label) as TextView
        textView.typeface = typeface_bold

        mMapChoicesAvailable.add(MAPS.packageName)
        val wazeInstalled = launchHelper.isAbleToLaunch(WAZE.packageName)
        if (wazeInstalled) {
            mMapChoicesAvailable.add(WAZE.packageName)
        }
        setupCloseWaze(rootView)
        setupDrivingModeMaps(rootView)
        setupLaunchWazeDirections(rootView)

        setupLaunchTimesSwitch(rootView)

        mapsRadiobuttonCreator(rootView, requireActivity())
        mapsRadioButtonListener(rootView)

        setMapChoice(rootView, activity)
        setCheckBoxes(rootView, R.id.ll_home_chk_boxes)
        setCheckBoxes(rootView, R.id.ll_work_days_chk_boxes)
        setCheckBoxes(rootView, R.id.ll_custom_days_chk_boxes)

        morningStartButton(rootView)
        morningEndButton(rootView)
        eveningStartButton(rootView)
        eveningEndButton(rootView)
        customStartButton(rootView)
        customEndButton(rootView)

        customLocationName(rootView)

        return rootView
    }

    fun setupLaunchWazeDirections(view: View) {

        mCanLaunchDirections = preferences.getCanLaunchDirections()

        val launchDirectionsSwitch = view.findViewById<View>(R.id.launch_waze_directions) as Switch
        val launchDirectionsDesc = view.findViewById<View>(R.id.launch_waze_directions_desc) as TextView

        val launchTimesSwitch = view.findViewById<View>(R.id.times_to_launch) as Switch

        val morningTimeSpanText = view.findViewById<View>(R.id.morning_timespan_label) as TextView
        val eveningTimeSpanText = view.findViewById<View>(R.id.evening_timespan_label) as TextView

        val homeCheckboxLabel = view.findViewById<View>(R.id.tv_home_location_label) as TextView
        val workCheckboxLabel = view.findViewById<View>(R.id.tv_work_location_label) as TextView

        if (mCanLaunchDirections) {
            morningTimeSpanText.setText(R.string.work_directions_label)
            eveningTimeSpanText.setText(R.string.home_directions_label)
        }

        launchDirectionsSwitch.isChecked = mCanLaunchDirections
        launchDirectionsSwitch.visibility = View.VISIBLE
        launchDirectionsDesc.visibility = View.VISIBLE
        launchDirectionsSwitch.setOnClickListener { view ->
            val on = (view as Switch).isChecked
            if (on) {
                preferences.setCanLaunchDirections(true)
                preferences.setUseTimesToLaunchMaps(true)
                launchTimesSwitch.isChecked = true
                morningTimeSpanText.setText(R.string.work_directions_label)
                eveningTimeSpanText.setText(R.string.home_directions_label)

                homeCheckboxLabel.text = "Home"
                workCheckboxLabel.text = "Work"
                Log.d(TAG, "LaunchDirectionsSwitch is ON")
            } else {
                preferences.setCanLaunchDirections(false)
                morningTimeSpanText.setText(R.string.morning_time_span_label)
                eveningTimeSpanText.setText(R.string.evening_time_span_label)

                homeCheckboxLabel.text = "Evening"
                workCheckboxLabel.text = "Morning"
                Log.d(TAG, "LaunchDirectionsSwitch is OFF")
            }
        }
    }

    fun setupDrivingModeMaps(view: View) {
        val mapChoice = preferences.getMapsChoice()
        val drivingModeSwitch = view.findViewById<View>(R.id.sw_driving_mode) as Switch
        val drivingModeDesc = view.findViewById<View>(R.id.tv_driving_mode_desc) as TextView
        val locationNameExplaination = view.findViewById<View>(R.id.tv_location_name_explaination) as TextView
        val locationNameEditText = view.findViewById<View>(R.id.et_custom_location_name) as EditText

        if (mapChoice == MAPS.packageName) {
            drivingModeSwitch.isChecked = preferences.getLaunchMapsDrivingMode()
            drivingModeSwitch.visibility = View.VISIBLE
            drivingModeDesc.visibility = View.VISIBLE
            drivingModeSwitch.setOnClickListener { view ->
                val on = (view as Switch).isChecked
                if (on) {
                    preferences.setLaunchMapsDrivingMode(true)
                    Log.d(TAG, "DrivingModeSwitch is ON")
                } else {
                    preferences.setLaunchMapsDrivingMode(false)
                    Log.d(TAG, "DrivingModeSwitch is OFF")
                }
            }

            locationNameExplaination.setText(R.string.enter_address_here)
            locationNameEditText.setHint(R.string.location_address)
        } else {
            drivingModeSwitch.visibility = View.GONE
            drivingModeDesc.visibility = View.GONE

            locationNameExplaination.setText(R.string.enter_favorite_from_waze)
            locationNameEditText.setHint(R.string.waze_favorite_name)
        }
    }

    fun setupCloseWaze(view: View) {
        val mapChoice = preferences.getMapsChoice()
        val closeWazeSwitch = view.findViewById<View>(R.id.close_waze) as Switch
        val closeWazeDesc = view.findViewById<View>(R.id.close_waze_desc) as TextView
        if (mapChoice == WAZE.packageName) {
            closeWazeSwitch.isChecked = preferences.getCloseWazeOnDisconnect()
            closeWazeSwitch.visibility = View.VISIBLE
            closeWazeDesc.visibility = View.VISIBLE
            closeWazeSwitch.setOnClickListener { closeWazeSwitchView ->
                val on = (closeWazeSwitchView as Switch).isChecked
                if (on) {
                    preferences.setCloseWazeOnDisconnect(true)
                    preferences.setSendToBackground(true)
                    Log.d(TAG, "CloseWazeSwitch is ON")
                } else {
                    preferences.setCloseWazeOnDisconnect(false)
                    Log.d(TAG, "CloseWazeSwitch is OFF")
                }
            }
        } else {
            closeWazeSwitch.visibility = View.GONE
            closeWazeDesc.visibility = View.GONE
        }
    }

    fun setupLaunchTimesSwitch(view: View) {
        val isEnabled = preferences.getUseTimesToLaunchMaps()

        val launchDirectionsSwitch = view.findViewById<View>(R.id.launch_waze_directions) as Switch
        val morningTimeSpanText = view.findViewById<View>(R.id.morning_timespan_label) as TextView
        val eveningTimeSpanText = view.findViewById<View>(R.id.evening_timespan_label) as TextView

        val launchTimesSwitch = view.findViewById<View>(R.id.times_to_launch) as Switch
        launchTimesSwitch.isChecked = isEnabled

        launchTimesSwitch.setOnClickListener { view ->
            val on = (view as Switch).isChecked
            if (on) {
                preferences.setUseTimesToLaunchMaps(true)
                Log.d(TAG, "LaunchTimesSwitch is ON")
            } else {
                preferences.setUseTimesToLaunchMaps(false)
                preferences.setCanLaunchDirections(false)

                launchDirectionsSwitch.isChecked = false
                morningTimeSpanText.text = "Morning Time Span"
                eveningTimeSpanText.text = "Evening Time Span"

                Log.d(TAG, "LaunchTimesSwitch is OFF")
            }
        }
    }

    private fun setMapChoice(view: View, context: Context?) {
        val mapChoice = preferences.getMapsChoice()
        val index = mMapChoicesAvailable.indexOf(mapChoice)
        val rdoGroup = view.findViewById<View>(R.id.rdo_group_map_app_choice) as RadioGroup
        val radioButton = rdoGroup.getChildAt(index) as RadioButton
        radioButton.isChecked = true
    }

    // Create Map app choice Radiobuttons
    private fun mapsRadiobuttonCreator(view: View, context: Context) {
        var rdoButton: RadioButton
        var appInfo: ApplicationInfo
        var mapAppName = "No Name"
        val pm = context.packageManager

        val rdoMPGroup = view.findViewById<View>(R.id.rdo_group_map_app_choice) as RadioGroup
        rdoMPGroup.removeAllViews()

        for (packageName in mMapChoicesAvailable) {
            try {
                appInfo = pm.getApplicationInfo(packageName, 0)
                mapAppName = pm.getApplicationLabel(appInfo).toString()
                if (mapAppName == "Maps") {
                    mapAppName = "Google Maps"
                }

            } catch (e: Exception) {
                Log.e(TAG, e.message)
            }

            rdoButton = RadioButton(context)
            rdoButton.text = mapAppName
            rdoButton.setTextColor(resources.getColor(R.color.colorPrimary))
            rdoButton.typeface = Typeface.createFromAsset(context.assets, "fonts/TitilliumText400wt.otf")
            rdoMPGroup.addView(rdoButton)
        }
    }

    private fun mapsRadioButtonListener(view: View) {
        val group = view.findViewById<View>(R.id.rdo_group_map_app_choice) as RadioGroup
        group.setOnCheckedChangeListener { radioGroup, i ->
            val radioButton = radioGroup.findViewById<View>(i)
            val index = radioGroup.indexOfChild(radioButton)
            val packageName = mMapChoicesAvailable[index]
            preferences.setMapsChoice(packageName)
            setupCloseWaze(view)
            setupDrivingModeMaps(view)
            setupLaunchWazeDirections(view)
        }
    }

    private fun setCheckBoxes(view: View, @IdRes linearLayoutId: Int) {
        var checkBox: CheckBox
        val entireWeek = arrayOf("1", "2", "3", "4", "5", "6", "7")
        val daysToLaunchSet: MutableSet<String>

        val daysToLaunchChkBoxLL = view.findViewById<View>(linearLayoutId) as LinearLayout
        daysToLaunchChkBoxLL.removeAllViews()

        when (linearLayoutId) {
            R.id.ll_home_chk_boxes -> {
                daysToLaunchSet = preferences.getHomeDaysToLaunchMaps()?.toMutableSet() ?: mutableSetOf()

                val checkboxLabelText = if (mCanLaunchDirections) "Home" else "Evening"
                val checkboxLabel = view.findViewById<View>(R.id.tv_home_location_label) as TextView
                checkboxLabel.text = checkboxLabelText
            }
            R.id.ll_work_days_chk_boxes -> {
                daysToLaunchSet = preferences.getWorkDaysToLaunchMaps()?.toMutableSet() ?: mutableSetOf()

                val checkboxLabelText = if (mCanLaunchDirections) "Work" else "Morning"
                val checkboxLabel = view.findViewById<View>(R.id.tv_work_location_label) as TextView
                checkboxLabel.text = checkboxLabelText
            }
            else -> daysToLaunchSet = preferences.getCustomDaysToLaunchMaps()?.toMutableSet() ?: mutableSetOf()
        }

        val params = RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        for (day in entireWeek) {
            checkBox = CheckBox(activity)
            checkBox.isChecked = daysToLaunchSet.contains(day)

            when (linearLayoutId) {
                R.id.ll_home_chk_boxes -> {
                    checkBox.text = getNameOfDay(day)
                    checkBox.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))
                    checkBox.typeface = Typeface.createFromAsset(requireActivity().assets, "fonts/TitilliumText400wt.otf")
                    homeCheckboxListener(checkBox, day)
                }
                R.id.ll_work_days_chk_boxes -> {
                    checkBox.layoutParams = params
                    workCheckboxListener(checkBox, day)
                }
                else -> {
                    checkBox.layoutParams = params
                    customCheckboxListener(checkBox, day)
                }
            }

            daysToLaunchChkBoxLL.addView(checkBox)
        }
    }

    private fun homeCheckboxListener(checkBox: CheckBox, dayNumber: String) {
        val fragmentActivity = requireActivity()

        checkBox.setOnClickListener {
            val daysToLaunch = preferences.getHomeDaysToLaunchMaps() as MutableSet<String>
            if (checkBox.isChecked) {
                daysToLaunch.add(dayNumber)
                preferences.setHomeDaysToLaunchMaps(daysToLaunch)
            } else {
                daysToLaunch.remove(dayNumber)
                preferences.setHomeDaysToLaunchMaps(daysToLaunch)
            }
        }
    }

    private fun workCheckboxListener(checkBox: CheckBox, dayNumber: String) {
        val fragmentActivity = requireActivity()

        checkBox.setOnClickListener {
            val daysToLaunch = preferences.getWorkDaysToLaunchMaps() as MutableSet<String>
            if (checkBox.isChecked) {
                daysToLaunch.add(dayNumber)
                preferences.setWorkDaysToLaunchMaps(daysToLaunch)
            } else {
                daysToLaunch.remove(dayNumber)
                preferences.setWorkDaysToLaunchMaps(daysToLaunch)
            }
        }
    }

    private fun customCheckboxListener(checkBox: CheckBox, dayNumber: String) {
        val fragmentActivity = requireActivity()

        checkBox.setOnClickListener {
            val daysToLaunch = preferences.getCustomDaysToLaunchMaps() as MutableSet<String>
            if (checkBox.isChecked) {
                daysToLaunch.add(dayNumber)
                preferences.setCustomDaysToLaunchMaps(daysToLaunch)
            } else {
                daysToLaunch.remove(dayNumber)
                preferences.setCustomDaysToLaunchMaps(daysToLaunch)
            }
        }
    }

    fun morningStartButton(view: View) {
        val setTime = TimeHelper.get12hrTime(preferences.getMorningStartTime())
        val timeDisplayed = view.findViewById<View>(R.id.morning_start_time_displayed) as TextView
        timeDisplayed.text = setTime

        val morningStartButton = view.findViewById<View>(R.id.morning_start_button) as Button
        morningStartButton.setOnClickListener {
            val newFragment = TimePickerFragment.newInstance(TimePickerFragment.MORNING_TIMESPAN, false, preferences.getMorningStartTime(), "Set Morning Start Time")
            newFragment.show(requireActivity().supportFragmentManager, "timePicker")
        }
    }

    fun morningEndButton(view: View) {
        val setTime = TimeHelper.get12hrTime(preferences.getMorningEndTime())
        val timeDisplayed = view.findViewById<View>(R.id.morning_end_time_displayed) as TextView
        timeDisplayed.text = setTime

        val morningEndButton = view.findViewById<View>(R.id.morning_end_button) as Button
        morningEndButton.setOnClickListener {
            val newFragment = TimePickerFragment.newInstance(TimePickerFragment.MORNING_TIMESPAN, true, preferences.getMorningEndTime(), "Set Morning End Time")
            newFragment.show(requireActivity().supportFragmentManager, "timePicker")
        }
    }

    fun eveningStartButton(view: View) {
        val setTime = TimeHelper.get12hrTime(preferences.getEveningStartTime())
        val timeDisplayed = view.findViewById<View>(R.id.evening_start_time_displayed) as TextView
        timeDisplayed.text = setTime

        val eveningStartButton = view.findViewById<View>(R.id.evening_start_button) as Button
        eveningStartButton.setOnClickListener {
            val newFragment = TimePickerFragment.newInstance(TimePickerFragment.EVENING_TIMESPAN, false, preferences.getEveningStartTime(), "Set Evening Start Time")
            newFragment.show(requireActivity().supportFragmentManager, "timePicker")
        }
    }

    fun eveningEndButton(view: View) {
        val setTime = TimeHelper.get12hrTime(preferences.getEveningEndTime())
        val timeDisplayed = view.findViewById<View>(R.id.evening_end_time_displayed) as TextView
        timeDisplayed.text = setTime

        val eveningEndButton = view.findViewById<View>(R.id.evening_end_button) as Button
        eveningEndButton.setOnClickListener {
            val newFragment = TimePickerFragment.newInstance(TimePickerFragment.EVENING_TIMESPAN, true, preferences.getEveningEndTime(), "Set Evening End Time")
            newFragment.show(requireActivity().supportFragmentManager, "timePicker")
        }
    }

    fun customStartButton(view: View) {
        val setTime = TimeHelper.get12hrTime(preferences.getCustomStartTime())
        val timeDisplayed = view.findViewById<View>(R.id.custom_start_time_displayed) as TextView
        timeDisplayed.text = setTime

        val eveningStartButton = view.findViewById<View>(R.id.custom_start_button) as Button
        eveningStartButton.setOnClickListener {
            val newFragment = TimePickerFragment.newInstance(TimePickerFragment.CUSTOM_TIMESPAN, false, preferences.getCustomStartTime(), "Set Custom Start Time")
            newFragment.show(requireActivity().supportFragmentManager, "timePicker")
        }
    }

    fun customEndButton(view: View) {
        val setTime = TimeHelper.get12hrTime(preferences.getCustomEndTime())
        val timeDisplayed = view.findViewById<View>(R.id.custom_end_time_displayed) as TextView
        timeDisplayed.text = setTime

        val eveningEndButton = view.findViewById<View>(R.id.custom_end_button) as Button
        eveningEndButton.setOnClickListener {
            val newFragment = TimePickerFragment.newInstance(TimePickerFragment.CUSTOM_TIMESPAN, true, preferences.getCustomEndTime(), "Set Custom End Time")
            newFragment.show(requireActivity().supportFragmentManager, "timePicker")
        }
    }

    fun customLocationName(view: View) {
        val locationName = preferences.getCustomLocationName()
        mLocationNameEditText = view.findViewById<View>(R.id.et_custom_location_name) as EditText

        if (locationName.isNotEmpty()) {
            mLocationNameEditText?.setText(locationName)
        }

        mLocationNameEditText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {

            }

            override fun afterTextChanged(editable: Editable) {
                mTextChangeHandler.removeCallbacks(mTextChangeRunnable)
                mTextChangeHandler.postDelayed(mTextChangeRunnable, 250)
            }
        })
    }

    private fun getNameOfDay(dayNumber: String): String {
        when (dayNumber) {
            "1" -> return "Sunday"
            "2" -> return "Monday"
            "3" -> return "Tuesday"
            "4" -> return "Wednesday"
            "5" -> return "Thursday"
            "6" -> return "Friday"
            "7" -> return "Saturday"
            else -> return "Unknown Day Number"
        }
    }

    companion object {
        private val TAG = "MapsFragment"

        fun newInstance(): MapsFragment {
            return MapsFragment()
        }
    }
}// Required empty public constructor
