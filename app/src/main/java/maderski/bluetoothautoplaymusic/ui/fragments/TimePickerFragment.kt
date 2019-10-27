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
import androidx.fragment.app.DialogFragment

/**
 * Created by Jason on 9/17/16.
 */
class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {
    interface TimePickerDialogListener {
        fun onTimeSet(typeOfTimeSet: String, isEndTime: Boolean, view: TimePicker, hourOfDay: Int, minute: Int)
        fun onTimeCancel(typeOfTimeSet: String, isEndTime: Boolean)
    }

    private var isEndTime: Boolean = false
    private var previouslySetTime: Int = 0
    private var pickerTitle: String = ""
    private var typeOfTimeSet: String = ""

    private var dialogListener: TimePickerDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        arguments?.let {
            isEndTime = it.getBoolean(PICKER_IS_DIM)
            previouslySetTime = it.getInt(PICKER_PREVIOUSLY_SET_TIME)
            pickerTitle = it.getString(PICKER_TITLE) ?: ""
            typeOfTimeSet = it.getString(TYPE_OF_TIME_SET) ?: ""
            dialogListener = if (requireActivity() is TimePickerDialogListener) requireActivity() as TimePickerDialogListener? else null
        }

        //Create and set Title
        val tpfTitle = TextView(requireActivity())
        tpfTitle.text = pickerTitle
        tpfTitle.gravity = Gravity.CENTER_HORIZONTAL

        //Get minutes
        var tempToGetMinutes = previouslySetTime
        while (tempToGetMinutes > 60) {
            tempToGetMinutes -= 100
        }
        val minute = tempToGetMinutes
        //Get hour
        val hour = (previouslySetTime - tempToGetMinutes) / 100

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
            dialogListener?.onTimeCancel(typeOfTimeSet, isEndTime)
            Log.d(TAG, "Cancelled " + typeOfTimeSet + " time set...isEndTime: " + java.lang.Boolean.toString(isEndTime))
        }
        super.onCancel(dialog)

    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        // Do something with the time chosen by the user
        val setTime = hourOfDay * 100 + minute
        Log.d(TAG, "Set time: " + Integer.toString(setTime))
        Log.d(TAG, "Is Dim: " + java.lang.Boolean.toString(isEndTime))

        if (dialogListener != null)
            dialogListener?.onTimeSet(typeOfTimeSet, isEndTime, view, hourOfDay, minute)


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

        private const val PICKER_IS_DIM = "picker_isDim"
        private const val PICKER_PREVIOUSLY_SET_TIME = "picker_previouslySetTime"
        private const val PICKER_TITLE = "picker_title"
        private const val TYPE_OF_TIME_SET = "type_of_time_set"

        fun newInstance(@TypeOfTimeSet typeOfTimeSet: String, isEndTime: Boolean, previouslySetTime: Int, title: String): TimePickerFragment {
            val args = Bundle()
            args.putBoolean(PICKER_IS_DIM, isEndTime)
            args.putInt(PICKER_PREVIOUSLY_SET_TIME, previouslySetTime)
            args.putString(PICKER_TITLE, title)
            args.putString(TYPE_OF_TIME_SET, typeOfTimeSet)
            val fragment = TimePickerFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
