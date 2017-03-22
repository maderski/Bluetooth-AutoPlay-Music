package maderski.bluetoothautoplaymusic.UI;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import maderski.bluetoothautoplaymusic.Helpers.TimeFormatHelper;
import maderski.bluetoothautoplaymusic.LaunchApp;
import maderski.bluetoothautoplaymusic.PackageTools;
import maderski.bluetoothautoplaymusic.R;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

public class MapsFragment extends Fragment {
    private static final String TAG = "MapsFragment";

    private List<String> mMapChoicesAvailable = new ArrayList<>();

    public MapsFragment() {
        // Required empty public constructor
    }

    public static MapsFragment newInstance() {
        MapsFragment fragment = new MapsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);

        Typeface typeface_bold = Typeface.createFromAsset(getContext().getAssets(), "fonts/TitilliumText600wt.otf");
        TextView textView = (TextView)rootView.findViewById(R.id.map_options_text);
        textView.setTypeface(typeface_bold);

        textView = (TextView)rootView.findViewById(R.id.daysToLaunchLabel);
        textView.setTypeface(typeface_bold);

        textView = (TextView)rootView.findViewById(R.id.map_app_choice);
        textView.setTypeface(typeface_bold);

        textView = (TextView)rootView.findViewById(R.id.morning_timespan_label);
        textView.setTypeface(typeface_bold);

        textView = (TextView)rootView.findViewById(R.id.evening_timespan_label);
        textView.setTypeface(typeface_bold);

        mMapChoicesAvailable.add(PackageTools.PackageName.MAPS);
        LaunchApp launchApp = new LaunchApp();
        boolean wazeInstalled = launchApp.checkPkgOnPhone(getContext(), PackageTools.PackageName.WAZE);
        if(wazeInstalled){
            mMapChoicesAvailable.add(PackageTools.PackageName.WAZE);
        }
        setupCloseWaze(rootView);
        setupLaunchWazeDirections(rootView);

        setupLaunchTimesSwitch(rootView);
        mapsRadiobuttonCreator(rootView, getContext());
        mapsRadioButtonListener(rootView, getContext());
        setMapChoice(rootView, getContext());
        setCheckBoxes(rootView);
        morningStartButton(rootView);
        morningEndButton(rootView);
        eveningStartButton(rootView);
        eveningEndButton(rootView);

        return rootView;
    }

    public void setupLaunchWazeDirections(View view){
        String mapChoice = BAPMPreferences.getMapsChoice(getContext());
        boolean canLaunchDirections = BAPMPreferences.getCanLaunchDirections(getContext());

        Switch launchDirectionsSwitch = (Switch)view.findViewById(R.id.launch_waze_directions);
        TextView launchDirectionsDesc = (TextView)view.findViewById(R.id.launch_waze_directions_desc);

        final Switch launchTimesSwitch = (Switch)view.findViewById(R.id.times_to_launch);

        final TextView morningTimeSpanText = (TextView)view.findViewById(R.id.morning_timespan_label);
        final TextView eveningTimeSpanText = (TextView)view.findViewById(R.id.evening_timespan_label);

        if(mapChoice.equals(PackageTools.PackageName.WAZE)){
            if(canLaunchDirections){
                morningTimeSpanText.setText(R.string.work_directions_label);
                eveningTimeSpanText.setText(R.string.home_directions_label);
            }

            launchDirectionsSwitch.setChecked(canLaunchDirections);
            launchDirectionsSwitch.setVisibility(View.VISIBLE);
            launchDirectionsDesc.setVisibility(View.VISIBLE);
            launchDirectionsSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean on = ((Switch) view).isChecked();
                    if (on) {
                        BAPMPreferences.setCanLaunchDirections(getContext(), true);
                        BAPMPreferences.setUseTimesToLaunchMaps(getContext(), true);
                        launchTimesSwitch.setChecked(true);
                        morningTimeSpanText.setText(R.string.work_directions_label);
                        eveningTimeSpanText.setText(R.string.home_directions_label);
                        Log.d(TAG, "LaunchDirectionsSwitch is ON");
                    } else {
                        BAPMPreferences.setCanLaunchDirections(getContext(), false);
                        morningTimeSpanText.setText("Morning Time Span");
                        eveningTimeSpanText.setText("Evening Time Span");
                        Log.d(TAG, "LaunchDirectionsSwitch is OFF");
                    }
                }
            });
        } else {
            launchDirectionsSwitch.setVisibility(View.GONE);
            launchDirectionsDesc.setVisibility(View.GONE);
            morningTimeSpanText.setText("Morning Time Span");
            eveningTimeSpanText.setText("Evening Time Span");
        }
    }

    public void setupCloseWaze(View view){
        String mapChoice = BAPMPreferences.getMapsChoice(getContext());
        Switch closeWazeSwitch = (Switch)view.findViewById(R.id.close_waze);
        TextView closeWazeDesc = (TextView)view.findViewById(R.id.close_waze_desc);
        if(mapChoice.equals(PackageTools.PackageName.WAZE)){
            closeWazeSwitch.setChecked(BAPMPreferences.getCloseWazeOnDisconnect(getContext()));
            closeWazeSwitch.setVisibility(View.VISIBLE);
            closeWazeDesc.setVisibility(View.VISIBLE);
            closeWazeSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean on = ((Switch) view).isChecked();
                    if (on) {
                        BAPMPreferences.setCloseWazeOnDisconnect(getContext(), true);
                        Log.d(TAG, "CloseWazeSwitch is ON");
                    } else {
                        BAPMPreferences.setCloseWazeOnDisconnect(getContext(), false);
                        Log.d(TAG, "CloseWazeSwitch is OFF");
                    }
                }
            });
        } else {
            closeWazeSwitch.setVisibility(View.GONE);
            closeWazeDesc.setVisibility(View.GONE);
        }
    }

    public void setupLaunchTimesSwitch(View view){
        boolean isEnabled = BAPMPreferences.getUseTimesToLaunchMaps(getContext());

        final Switch launchDirectionsSwitch = (Switch)view.findViewById(R.id.launch_waze_directions);
        final TextView morningTimeSpanText = (TextView)view.findViewById(R.id.morning_timespan_label);
        final TextView eveningTimeSpanText = (TextView)view.findViewById(R.id.evening_timespan_label);

        Switch launchTimesSwitch = (Switch)view.findViewById(R.id.times_to_launch);
        launchTimesSwitch.setChecked(isEnabled);

        launchTimesSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((Switch)view).isChecked();
                if(on) {
                    BAPMPreferences.setUseTimesToLaunchMaps(getContext(), true);
                    Log.d(TAG, "LaunchTimesSwitch is ON");
                } else {
                    BAPMPreferences.setUseTimesToLaunchMaps(getContext(), false);
                    BAPMPreferences.setCanLaunchDirections(getContext(), false);

                    launchDirectionsSwitch.setChecked(false);
                    morningTimeSpanText.setText("Morning Time Span");
                    eveningTimeSpanText.setText("Evening Time Span");

                    Log.d(TAG, "LaunchTimesSwitch is OFF");
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void setMapChoice(View view, Context context){
        String mapChoice = BAPMPreferences.getMapsChoice(context);
        int index = mMapChoicesAvailable.indexOf(mapChoice);
        RadioGroup rdoGroup = (RadioGroup) view.findViewById(R.id.rdo_group_map_app_choice);
        RadioButton radioButton = (RadioButton) rdoGroup.getChildAt(index);
        radioButton.setChecked(true);
    }

    // Create Map app choice Radiobuttons
    private void mapsRadiobuttonCreator(View view, Context context){
        RadioButton rdoButton;
        ApplicationInfo appInfo;
        String mapAppName = "No Name";
        PackageManager pm = context.getPackageManager();

        RadioGroup rdoMPGroup = (RadioGroup) view.findViewById(R.id.rdo_group_map_app_choice);
        rdoMPGroup.removeAllViews();

        for(String packageName : mMapChoicesAvailable) {
            try {
                appInfo = pm.getApplicationInfo(packageName, 0);
                mapAppName = pm.getApplicationLabel(appInfo).toString();
                if(mapAppName.equals("Maps")){
                    mapAppName = "Google Maps";
                }

            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }

            rdoButton = new RadioButton(context);
            rdoButton.setText(mapAppName);
            rdoButton.setTextColor(getResources().getColor(R.color.colorPrimary));
            rdoButton.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/TitilliumText400wt.otf"));
            rdoMPGroup.addView(rdoButton);
        }
    }

    private void mapsRadioButtonListener(View view, final Context context){
        RadioGroup group = (RadioGroup) view.findViewById(R.id.rdo_group_map_app_choice);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                View radioButton = radioGroup.findViewById(i);
                int index = radioGroup.indexOfChild(radioButton);
                String packageName = mMapChoicesAvailable.get(index);
                BAPMPreferences.setMapsChoice(context, packageName);
                if(getView() != null){
                    setupCloseWaze(getView());
                    setupLaunchWazeDirections(getView());
                }
            }
        });
    }

    private void setCheckBoxes(View view){
        CheckBox checkBox;
        String[] entireWeek = {"1", "2", "3", "4", "5", "6", "7"};
        Set<String> daysToLaunchSet = BAPMPreferences.getDaysToLaunchMaps(getActivity());

        LinearLayout daysToLaunchChkBoxLL = (LinearLayout) view.findViewById(R.id.daysChkBoxLL);
        daysToLaunchChkBoxLL.removeAllViews();

        for(String day : entireWeek){
            checkBox = new CheckBox(getActivity());
            checkBox.setText(getNameOfDay(day));
            checkBox.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            checkBox.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/TitilliumText400wt.otf"));
            checkBox.setChecked(daysToLaunchSet.contains(day));
            checkboxListener(checkBox, day);
            daysToLaunchChkBoxLL.addView(checkBox);
        }
    }

    private void checkboxListener(CheckBox checkBox, String dayNumber){
        final Context ctx = getActivity();
        final CheckBox cb = checkBox;
        final String dn = dayNumber;

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> daysToLaunch = new HashSet<>(BAPMPreferences.getDaysToLaunchMaps(ctx));
                if(cb.isChecked()){
                    daysToLaunch.add(dn);
                    BAPMPreferences.setDaysToLaunchMaps(ctx, daysToLaunch);
                }else{
                    daysToLaunch.remove(dn);
                    BAPMPreferences.setDaysToLaunchMaps(ctx, daysToLaunch);
                }
            }
        });
    }

    public void morningStartButton(View view){
        String setTime = TimeFormatHelper.get12hrTime(BAPMPreferences.getMorningStartTime(getContext()));
        TextView timeDisplayed = (TextView)view.findViewById(R.id.morning_start_time_displayed);
        timeDisplayed.setText(setTime);

        Button morningStartButton = (Button)view.findViewById(R.id.morning_start_button);
        morningStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerFragment.newInstance(TimePickerFragment.TypeOfTimeSet.MORNING_TIMESPAN, false, BAPMPreferences.getMorningStartTime(getActivity()), "Set Morning Start Time");
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });
    }

    public void morningEndButton(View view){
        String setTime = TimeFormatHelper.get12hrTime(BAPMPreferences.getMorningEndTime(getContext()));
        TextView timeDisplayed = (TextView)view.findViewById(R.id.morning_end_time_displayed);
        timeDisplayed.setText(setTime);

        Button morningEndButton = (Button)view.findViewById(R.id.morning_end_button);
        morningEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerFragment.newInstance(TimePickerFragment.TypeOfTimeSet.MORNING_TIMESPAN, true, BAPMPreferences.getMorningEndTime(getActivity()), "Set Morning End Time");
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });
    }

    public void eveningStartButton(View view){
        String setTime = TimeFormatHelper.get12hrTime(BAPMPreferences.getEveningStartTime(getContext()));
        TextView timeDisplayed = (TextView)view.findViewById(R.id.evening_start_time_displayed);
        timeDisplayed.setText(setTime);

        Button eveningStartButton = (Button)view.findViewById(R.id.evening_start_button);
        eveningStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerFragment.newInstance(TimePickerFragment.TypeOfTimeSet.EVENING_TIMESPAN, false, BAPMPreferences.getEveningStartTime(getActivity()), "Set Evening Start Time");
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });
    }

    public void eveningEndButton(View view){
        String setTime = TimeFormatHelper.get12hrTime(BAPMPreferences.getEveningEndTime(getContext()));
        TextView timeDisplayed = (TextView)view.findViewById(R.id.evening_end_time_displayed);
        timeDisplayed.setText(setTime);

        Button eveningEndButton = (Button)view.findViewById(R.id.evening_end_button);
        eveningEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerFragment.newInstance(TimePickerFragment.TypeOfTimeSet.EVENING_TIMESPAN, true, BAPMPreferences.getEveningEndTime(getActivity()), "Set Evening End Time");
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });
    }

    private String getNameOfDay(String dayNumber){
        switch(dayNumber){
            case "1":
                return "Sunday";
            case "2":
                return "Monday";
            case "3":
                return "Tuesday";
            case "4":
                return "Wednesday";
            case "5":
                return "Thursday";
            case "6":
                return "Friday";
            case "7":
                return "Saturday";
            default:
                return "Unknown Day Number";
        }
    }
}
