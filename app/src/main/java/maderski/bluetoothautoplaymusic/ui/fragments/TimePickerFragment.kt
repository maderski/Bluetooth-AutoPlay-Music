package maderski.bluetoothautoplaymusic.ui.fragments

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.Gravity
import android.widget.TextView
import android.widget.TimePicker
import androidx.annotation.StringDef

/**
 * Created by Jason on 9/17/16.
 */
class TimePickerFragment : androidx.fragment.app.DialogFragment(), TimePickerDialog.OnTimeSetListener {
    interface TimePickerDialogListener {
        fun onTimeSet(typeOfTimeSet: String, isEndTime: Boolean, view: TimePicker, hourOfDay: Int, minute: Int)
        fun onTimeCancel(typeOfTimeSet: String, isEndTime: Boolean)
    }

    private var mIsEndTime: Boolean = false
    private var mPreviouslySetTime: Int = 0
    private var mPickerTitle: String = ""
    private var mTypeOfTimeSet: String = ""

    var dialogListener: TimePickerDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        arguments?.let {
            mIsEndTime = it.getBoolean("picker_isDim")
            mPreviouslySetTime = it.getInt("picker_previouslySetTime")
            mPickerTitle = it.getString("picker_title") ?: ""
            mTypeOfTimeSet = it.getString("type_of_time_set") ?: ""
            dialogListener = if (requireActivity() is TimePickerDialogListener) requireActivity() as TimePickerDialogListener? else null
        }

        //Create and set Title
        val tpfTitle = TextView(requireActivity())
        tpfTitle.text = mPickerTitle
        tpfTitle.gravity = Gravity.CENTER_HORIZONTAL

        //Get minutes
        var tempToGetMinutes = mPreviouslySetTime
        while (tempToGetMinutes > 60) {
            tempToGetMinutes -= 100
        }
        val minute = tempToGetMinutes
        //Get hour
        val hour = (mPreviouslySetTime - tempToGetMinutes) / 100

        // Create a new instance of TimePickerDialog
        val timePickerDialog = TimePickerDialog(activity, this, hour, minute,
                DateFormat.is24HourFormat(activity))
        //Add title to picker
        timePickerDialog.setCustomTitle(tpfTitle)
        //Return TimePickerDialog
        return timePickerDialog
    }

    override fun onCancel(dialog: DialogInterface) {
        if (dialogListener != null) {
            dialogListener?.onTimeCancel(mTypeOfTimeSet, mIsEndTime)
            Log.d(TAG, "Cancelled " + mTypeOfTimeSet + " time set...isEndTime: " + java.lang.Boolean.toString(mIsEndTime))
        }
        super.onCancel(dialog)

    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user
        val setTime = hourOfDay * 100 + minute
        Log.d(TAG, "Set time: " + Integer.toString(setTime))
        Log.d(TAG, "Is Dim: " + java.lang.Boolean.toString(mIsEndTime))

        if (dialogListener != null)
            dialogListener?.onTimeSet(mTypeOfTimeSet, mIsEndTime, view, hourOfDay, minute)


    }

    companion object {
        private const val TAG = "TimePickerFragment"

        @StringDef(
                SCREEN_BRIGHTNESS_TIME,
                MORNING_TIMESPAN,
                EVENING_TIMESPAN,
                CUSTOM_TIMESPAN
        )
        @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
        annotation class TypeOfTimeSet

        const val SCREEN_BRIGHTNESS_TIME = "screen_brightness_time"
        const val MORNING_TIMESPAN = "morning_time_span"
        const val EVENING_TIMESPAN = "evening_time_span"
        const val CUSTOM_TIMESPAN = "custom_time_span"

        fun newInstance(@TypeOfTimeSet typeOfTimeSet: String, isEndTime: Boolean, previouslySetTime: Int, title: String): TimePickerFragment {
            val args = Bundle()
            args.putBoolean("picker_isDim", isEndTime)
            args.putInt("picker_previouslySetTime", previouslySetTime)
            args.putString("picker_title", title)
            args.putString("type_of_time_set", typeOfTimeSet)
            val fragment = TimePickerFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
