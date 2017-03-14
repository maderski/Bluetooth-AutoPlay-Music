package maderski.bluetoothautoplaymusic.UI;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.StringDef;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.TimePicker;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Jason on 9/17/16.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "TimePickerFragment";

    @StringDef({
            TypeOfTimeSet.SCREEN_BRIGHTNESS_TIME,
            TypeOfTimeSet.MORNING_TIMESPAN,
            TypeOfTimeSet.EVENING_TIMESPAN
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface TypeOfTimeSet {
        String SCREEN_BRIGHTNESS_TIME = "screen_brightness_time";
        String MORNING_TIMESPAN = "morning_time_span";
        String EVENING_TIMESPAN = "evening_time_span";
    }

    private boolean mIsEndTime;
    private int mPreviouslySetTime;
    private String mPickerTitle;
    private String mTypeOfTimeSet;

    public TimePickerDialogListener dialogListener;

    public static TimePickerFragment newInstance(@TypeOfTimeSet String typeOfTimeSet, boolean isEndTime, int previouslySetTime, String title){
        Bundle args = new Bundle();
        args.putBoolean("picker_isDim", isEndTime);
        args.putInt("picker_previouslySetTime", previouslySetTime);
        args.putString("picker_title", title);
        args.putString("type_of_time_set", typeOfTimeSet);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mIsEndTime = getArguments().getBoolean("picker_isDim");
        mPreviouslySetTime = getArguments().getInt("picker_previouslySetTime");
        mPickerTitle = getArguments().getString("picker_title");
        mTypeOfTimeSet = getArguments().getString("type_of_time_set");
        dialogListener = getActivity() instanceof TimePickerDialogListener ? (TimePickerDialogListener) getActivity() : null;

        //Create and set Title
        TextView tpfTitle = new TextView(getActivity());
        tpfTitle.setText(mPickerTitle);
        tpfTitle.setGravity(Gravity.CENTER_HORIZONTAL);

        //Get minutes
        int tempToGetMinutes = mPreviouslySetTime;
        while(tempToGetMinutes > 60){
            tempToGetMinutes -= 100;
        }
        int minute = tempToGetMinutes;
        //Get hour
        int hour = (mPreviouslySetTime - tempToGetMinutes)/100;

        // Create a new instance of TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
        //Add title to picker
        timePickerDialog.setCustomTitle(tpfTitle);
        //Return TimePickerDialog
        return timePickerDialog;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if(dialogListener != null) {
            dialogListener.onTimeCancel(mTypeOfTimeSet, mIsEndTime);
            Log.d(TAG, "Cancelled " + mTypeOfTimeSet + " time set...isEndTime: " + Boolean.toString(mIsEndTime));
        }
        super.onCancel(dialog);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        int setTime = (hourOfDay * 100) + minute;
        Log.d(TAG, "Set time: " + Integer.toString(setTime));
        Log.d(TAG, "Is Dim: " + Boolean.toString(mIsEndTime));

        if(dialogListener != null)
            dialogListener.onTimeSet(mTypeOfTimeSet, mIsEndTime, view, hourOfDay, minute);


    }

    public interface TimePickerDialogListener {
        void onTimeSet(String typeOfTimeSet, boolean isEndTime, TimePicker view, int hourOfDay, int minute);
        void onTimeCancel(String typeOfTimeSet, boolean isEndTime);
    }
}
