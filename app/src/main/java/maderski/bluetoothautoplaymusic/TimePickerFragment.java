package maderski.bluetoothautoplaymusic;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Jason on 9/17/16.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private boolean isDimFragment;
    private int previouslySetTime;
    public TimePickerDialogListener dialogListener;

    public static TimePickerFragment newInstance(boolean isDim, int previouslySetTime){
        Bundle args = new Bundle();
        args.putBoolean("picker_isDim", isDim);
        args.putInt("picker_previouslySetTime", previouslySetTime);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // get the current time as the default values for the picker
        isDimFragment = getArguments().getBoolean("picker_isDim");
        previouslySetTime = getArguments().getInt("picker_previouslySetTime");
        dialogListener = getActivity() instanceof TimePickerDialogListener ? (TimePickerDialogListener) getActivity() : null;
        int tempToGetMinutes = previouslySetTime;
        while(tempToGetMinutes > 60){
            tempToGetMinutes -= 100;
        }
        int minute = tempToGetMinutes;
        int hour = (previouslySetTime - tempToGetMinutes)/100;

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        int setTime = (hourOfDay * 100) + minute;
        if(BuildConfig.DEBUG) {
            Log.i("TimePickerFragment", "Set time: " + Integer.toString(setTime));
            Log.i("TimePickerFragment", "Is Dim: " + Boolean.toString(isDimFragment));
        }

        if(dialogListener != null)
            dialogListener.onTimeSet(isDimFragment, view, hourOfDay, minute);


    }

    public interface TimePickerDialogListener {
        void onTimeSet(boolean isDim, TimePicker view, int hourOfDay, int minute);
    }
}
