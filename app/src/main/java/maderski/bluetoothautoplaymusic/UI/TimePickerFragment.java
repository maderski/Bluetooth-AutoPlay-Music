package maderski.bluetoothautoplaymusic.UI;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.TimePicker;

import maderski.bluetoothautoplaymusic.BuildConfig;

/**
 * Created by Jason on 9/17/16.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {
    private static final String TAG = "TimePickerFragment";
    private boolean isDimFragment;
    private int previouslySetTime;
    private String pickerTitle;
    public TimePickerDialogListener dialogListener;

    public static TimePickerFragment newInstance(boolean isDim, int previouslySetTime, String title){
        Bundle args = new Bundle();
        args.putBoolean("picker_isDim", isDim);
        args.putInt("picker_previouslySetTime", previouslySetTime);
        args.putString("picker_title", title);
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        isDimFragment = getArguments().getBoolean("picker_isDim");
        previouslySetTime = getArguments().getInt("picker_previouslySetTime");
        pickerTitle = getArguments().getString("picker_title");
        dialogListener = getActivity() instanceof TimePickerDialogListener ? (TimePickerDialogListener) getActivity() : null;

        //Create and set Title
        TextView tpfTitle = new TextView(getActivity());
        tpfTitle.setText(pickerTitle);
        tpfTitle.setGravity(Gravity.CENTER_HORIZONTAL);

        //Get minutes
        int tempToGetMinutes = previouslySetTime;
        while(tempToGetMinutes > 60){
            tempToGetMinutes -= 100;
        }
        int minute = tempToGetMinutes;
        //Get hour
        int hour = (previouslySetTime - tempToGetMinutes)/100;

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
            dialogListener.onTimeCancel(isDimFragment);
            if(BuildConfig.DEBUG){
                Log.d(TAG, "Cancelled time set...isDim: " + Boolean.toString(isDimFragment));
            }
        }
        super.onCancel(dialog);
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        int setTime = (hourOfDay * 100) + minute;
        if(BuildConfig.DEBUG) {
            Log.i(TAG, "Set time: " + Integer.toString(setTime));
            Log.i(TAG, "Is Dim: " + Boolean.toString(isDimFragment));
        }

        if(dialogListener != null)
            dialogListener.onTimeSet(isDimFragment, view, hourOfDay, minute);


    }

    public interface TimePickerDialogListener {
        void onTimeSet(boolean isDim, TimePicker view, int hourOfDay, int minute);
        void onTimeCancel(boolean isDim);
    }
}
