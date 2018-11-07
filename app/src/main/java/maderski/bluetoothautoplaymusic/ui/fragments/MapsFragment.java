package maderski.bluetoothautoplaymusic.ui.fragments;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.IdRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import maderski.bluetoothautoplaymusic.helpers.TimeHelper;
import maderski.bluetoothautoplaymusic.helpers.LaunchAppHelper;
import maderski.bluetoothautoplaymusic.PackageTools;
import maderski.bluetoothautoplaymusic.R;
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences;
import maderski.bluetoothautoplaymusic.bus.BusProvider;
import maderski.bluetoothautoplaymusic.bus.events.mapsevents.LocationNameSetEvent;

public class MapsFragment extends Fragment {
    private static final String TAG = "MapsFragment";

    private List<String> mMapChoicesAvailable = new ArrayList<>();
    private boolean mCanLaunchDirections = false;

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

        Typeface typeface_bold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/TitilliumText600wt.otf");
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
        LaunchAppHelper launchAppHelper = new LaunchAppHelper();
        boolean wazeInstalled = launchAppHelper.checkPkgOnPhone(getActivity(), PackageTools.PackageName.WAZE);
        if(wazeInstalled){
            mMapChoicesAvailable.add(PackageTools.PackageName.WAZE);
        }
        setupCloseWaze(rootView);
        setupDrivingModeMaps(rootView);
        setupLaunchWazeDirections(rootView);

        setupLaunchTimesSwitch(rootView);

        mapsRadiobuttonCreator(rootView, getActivity());
        mapsRadioButtonListener(rootView, getActivity());

        setMapChoice(rootView, getActivity());
        setCheckBoxes(rootView, R.id.ll_home_chk_boxes);
        setCheckBoxes(rootView, R.id.ll_work_days_chk_boxes);
        setCheckBoxes(rootView, R.id.ll_custom_days_chk_boxes);

        morningStartButton(rootView);
        morningEndButton(rootView);
        eveningStartButton(rootView);
        eveningEndButton(rootView);
        customStartButton(rootView);
        customEndButton(rootView);

        customLocationName(rootView);

        return rootView;
    }

    public void setupLaunchWazeDirections(View view){

        mCanLaunchDirections = BAPMPreferences.INSTANCE.getCanLaunchDirections(getActivity());

        Switch launchDirectionsSwitch = (Switch)view.findViewById(R.id.launch_waze_directions);
        TextView launchDirectionsDesc = (TextView)view.findViewById(R.id.launch_waze_directions_desc);

        final Switch launchTimesSwitch = (Switch)view.findViewById(R.id.times_to_launch);

        final TextView morningTimeSpanText = (TextView)view.findViewById(R.id.morning_timespan_label);
        final TextView eveningTimeSpanText = (TextView)view.findViewById(R.id.evening_timespan_label);

        final TextView homeCheckboxLabel = (TextView) view.findViewById(R.id.tv_home_location_label);
        final TextView workCheckboxLabel = (TextView) view.findViewById(R.id.tv_work_location_label);

        if(mCanLaunchDirections){
            morningTimeSpanText.setText(R.string.work_directions_label);
            eveningTimeSpanText.setText(R.string.home_directions_label);
        }

        launchDirectionsSwitch.setChecked(mCanLaunchDirections);
        launchDirectionsSwitch.setVisibility(View.VISIBLE);
        launchDirectionsDesc.setVisibility(View.VISIBLE);
        launchDirectionsSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = ((Switch) view).isChecked();
                if (on) {
                    BAPMPreferences.INSTANCE.setCanLaunchDirections(getActivity(), true);
                    BAPMPreferences.INSTANCE.setUseTimesToLaunchMaps(getActivity(), true);
                    launchTimesSwitch.setChecked(true);
                    morningTimeSpanText.setText(R.string.work_directions_label);
                    eveningTimeSpanText.setText(R.string.home_directions_label);

                    homeCheckboxLabel.setText("Home");
                    workCheckboxLabel.setText("Work");
                    Log.d(TAG, "LaunchDirectionsSwitch is ON");
                } else {
                    BAPMPreferences.INSTANCE.setCanLaunchDirections(getContext(), false);
                    morningTimeSpanText.setText(R.string.morning_time_span_label);
                    eveningTimeSpanText.setText(R.string.evening_time_span_label);

                    homeCheckboxLabel.setText("Evening");
                    workCheckboxLabel.setText("Morning");
                    Log.d(TAG, "LaunchDirectionsSwitch is OFF");
                }
            }
        });
    }

    public void setupDrivingModeMaps(View view) {
        String mapChoice = BAPMPreferences.INSTANCE.getMapsChoice(getActivity());
        Switch drivingModeSwitch = (Switch)view.findViewById(R.id.sw_driving_mode);
        TextView drivingModeDesc = (TextView)view.findViewById(R.id.tv_driving_mode_desc);
        TextView locationNameExplaination = (TextView) view.findViewById(R.id.tv_location_name_explaination);
        EditText locationNameEditText = (EditText) view.findViewById(R.id.et_custom_location_name);

        if(mapChoice.equals(PackageTools.PackageName.MAPS)) {
            drivingModeSwitch.setChecked(BAPMPreferences.INSTANCE.getLaunchMapsDrivingMode(getActivity()));
            drivingModeSwitch.setVisibility(View.VISIBLE);
            drivingModeDesc.setVisibility(View.VISIBLE);
            drivingModeSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean on = ((Switch) view).isChecked();
                    if (on) {
                        BAPMPreferences.INSTANCE.setLaunchMapsDrivingMode(getActivity(), true);
                        Log.d(TAG, "DrivingModeSwitch is ON");
                    } else {
                        BAPMPreferences.INSTANCE.setLaunchMapsDrivingMode(getActivity(), false);
                        Log.d(TAG, "DrivingModeSwitch is OFF");
                    }
                }
            });

            locationNameExplaination.setText(R.string.enter_address_here);
            locationNameEditText.setHint(R.string.location_address);
        } else {
            drivingModeSwitch.setVisibility(View.GONE);
            drivingModeDesc.setVisibility(View.GONE);

            locationNameExplaination.setText(R.string.enter_favorite_from_waze);
            locationNameEditText.setHint(R.string.waze_favorite_name);
        }
    }

    public void setupCloseWaze(View view){
        String mapChoice = BAPMPreferences.INSTANCE.getMapsChoice(getActivity());
        Switch closeWazeSwitch = (Switch)view.findViewById(R.id.close_waze);
        TextView closeWazeDesc = (TextView)view.findViewById(R.id.close_waze_desc);
        if(mapChoice.equals(PackageTools.PackageName.WAZE)){
            closeWazeSwitch.setChecked(BAPMPreferences.INSTANCE.getCloseWazeOnDisconnect(getActivity()));
            closeWazeSwitch.setVisibility(View.VISIBLE);
            closeWazeDesc.setVisibility(View.VISIBLE);
            closeWazeSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean on = ((Switch) view).isChecked();
                    if (on) {
                        BAPMPreferences.INSTANCE.setCloseWazeOnDisconnect(getActivity(), true);
                        BAPMPreferences.INSTANCE.setSendToBackground(getActivity(), true);
                        Log.d(TAG, "CloseWazeSwitch is ON");
                    } else {
                        BAPMPreferences.INSTANCE.setCloseWazeOnDisconnect(getActivity(), false);
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
        boolean isEnabled = BAPMPreferences.INSTANCE.getUseTimesToLaunchMaps(getActivity());

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
                    BAPMPreferences.INSTANCE.setUseTimesToLaunchMaps(getActivity(), true);
                    Log.d(TAG, "LaunchTimesSwitch is ON");
                } else {
                    BAPMPreferences.INSTANCE.setUseTimesToLaunchMaps(getActivity(), false);
                    BAPMPreferences.INSTANCE.setCanLaunchDirections(getActivity(), false);

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
        String mapChoice = BAPMPreferences.INSTANCE.getMapsChoice(context);
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
                BAPMPreferences.INSTANCE.setMapsChoice(context, packageName);
                if(getView() != null){
                    setupCloseWaze(getView());
                    setupDrivingModeMaps(getView());
                    setupLaunchWazeDirections(getView());
                }
            }
        });
    }

    private void setCheckBoxes(View view, @IdRes int linearLayoutId){
        CheckBox checkBox;
        String[] entireWeek = {"1", "2", "3", "4", "5", "6", "7"};
        Set<String> daysToLaunchSet;

        LinearLayout daysToLaunchChkBoxLL = (LinearLayout) view.findViewById(linearLayoutId);
        daysToLaunchChkBoxLL.removeAllViews();

        if(linearLayoutId == R.id.ll_home_chk_boxes) {
            daysToLaunchSet = BAPMPreferences.INSTANCE.getHomeDaysToLaunchMaps(getActivity());

            String checkboxLabelText = mCanLaunchDirections ? "Home" : "Evening";
            TextView checkboxLabel = (TextView) view.findViewById(R.id.tv_home_location_label);
            checkboxLabel.setText(checkboxLabelText);
        } else if(linearLayoutId == R.id.ll_work_days_chk_boxes) {
            daysToLaunchSet = BAPMPreferences.INSTANCE.getWorkDaysToLaunchMaps(getActivity());

            String checkboxLabelText = mCanLaunchDirections ? "Work" : "Morning";
            TextView checkboxLabel = (TextView) view.findViewById(R.id.tv_work_location_label);
            checkboxLabel.setText(checkboxLabelText);
        } else {
            daysToLaunchSet = BAPMPreferences.INSTANCE.getCustomDaysToLaunchMaps(getActivity());
        }

        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        for(String day : entireWeek){
            checkBox = new CheckBox(getActivity());
            checkBox.setChecked(daysToLaunchSet.contains(day));

            if(linearLayoutId == R.id.ll_home_chk_boxes) {
                checkBox.setText(getNameOfDay(day));
                checkBox.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                checkBox.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/TitilliumText400wt.otf"));
                homeCheckboxListener(checkBox, day);
            } else if(linearLayoutId == R.id.ll_work_days_chk_boxes) {
                checkBox.setLayoutParams(params);
                workCheckboxListener(checkBox, day);
            } else {
                checkBox.setLayoutParams(params);
                customCheckboxListener(checkBox, day);
            }

            daysToLaunchChkBoxLL.addView(checkBox);
        }
    }

    private void homeCheckboxListener(CheckBox checkBox, String dayNumber){
        final Context ctx = getActivity();
        final CheckBox cb = checkBox;
        final String dn = dayNumber;

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> daysToLaunch = new HashSet<>(BAPMPreferences.INSTANCE.getHomeDaysToLaunchMaps(ctx));
                if(cb.isChecked()){
                    daysToLaunch.add(dn);
                    BAPMPreferences.INSTANCE.setHomeDaysToLaunchMaps(ctx, daysToLaunch);
                }else{
                    daysToLaunch.remove(dn);
                    BAPMPreferences.INSTANCE.setHomeDaysToLaunchMaps(ctx, daysToLaunch);
                }
            }
        });
    }

    private void workCheckboxListener(CheckBox checkBox, String dayNumber){
        final Context ctx = getActivity();
        final CheckBox cb = checkBox;
        final String dn = dayNumber;

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> daysToLaunch = new HashSet<>(BAPMPreferences.INSTANCE.getWorkDaysToLaunchMaps(ctx));
                if(cb.isChecked()){
                    daysToLaunch.add(dn);
                    BAPMPreferences.INSTANCE.setWorkDaysToLaunchMaps(ctx, daysToLaunch);
                }else{
                    daysToLaunch.remove(dn);
                    BAPMPreferences.INSTANCE.setWorkDaysToLaunchMaps(ctx, daysToLaunch);
                }
            }
        });
    }

    private void customCheckboxListener(CheckBox checkBox, String dayNumber){
        final Context ctx = getActivity();
        final CheckBox cb = checkBox;
        final String dn = dayNumber;

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> daysToLaunch = new HashSet<>(BAPMPreferences.INSTANCE.getCustomDaysToLaunchMaps(ctx));
                if(cb.isChecked()){
                    daysToLaunch.add(dn);
                    BAPMPreferences.INSTANCE.setCustomDaysToLaunchMaps(ctx, daysToLaunch);
                }else{
                    daysToLaunch.remove(dn);
                    BAPMPreferences.INSTANCE.setCustomDaysToLaunchMaps(ctx, daysToLaunch);
                }
            }
        });
    }

    public void morningStartButton(View view){
        String setTime = TimeHelper.Companion.get12hrTime(BAPMPreferences.INSTANCE.getMorningStartTime(getActivity()));
        TextView timeDisplayed = (TextView)view.findViewById(R.id.morning_start_time_displayed);
        timeDisplayed.setText(setTime);

        Button morningStartButton = (Button)view.findViewById(R.id.morning_start_button);
        morningStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerFragment.newInstance(TimePickerFragment.TypeOfTimeSet.MORNING_TIMESPAN, false, BAPMPreferences.INSTANCE.getMorningStartTime(getActivity()), "Set Morning Start Time");
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });
    }

    public void morningEndButton(View view){
        String setTime = TimeHelper.Companion.get12hrTime(BAPMPreferences.INSTANCE.getMorningEndTime(getActivity()));
        TextView timeDisplayed = (TextView)view.findViewById(R.id.morning_end_time_displayed);
        timeDisplayed.setText(setTime);

        Button morningEndButton = (Button)view.findViewById(R.id.morning_end_button);
        morningEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerFragment.newInstance(TimePickerFragment.TypeOfTimeSet.MORNING_TIMESPAN, true, BAPMPreferences.INSTANCE.getMorningEndTime(getActivity()), "Set Morning End Time");
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });
    }

    public void eveningStartButton(View view){
        String setTime = TimeHelper.Companion.get12hrTime(BAPMPreferences.INSTANCE.getEveningStartTime(getActivity()));
        TextView timeDisplayed = (TextView)view.findViewById(R.id.evening_start_time_displayed);
        timeDisplayed.setText(setTime);

        Button eveningStartButton = (Button)view.findViewById(R.id.evening_start_button);
        eveningStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerFragment.newInstance(TimePickerFragment.TypeOfTimeSet.EVENING_TIMESPAN, false, BAPMPreferences.INSTANCE.getEveningStartTime(getActivity()), "Set Evening Start Time");
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });
    }

    public void eveningEndButton(View view){
        String setTime = TimeHelper.Companion.get12hrTime(BAPMPreferences.INSTANCE.getEveningEndTime(getActivity()));
        TextView timeDisplayed = (TextView)view.findViewById(R.id.evening_end_time_displayed);
        timeDisplayed.setText(setTime);

        Button eveningEndButton = (Button)view.findViewById(R.id.evening_end_button);
        eveningEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerFragment.newInstance(TimePickerFragment.TypeOfTimeSet.EVENING_TIMESPAN, true, BAPMPreferences.INSTANCE.getEveningEndTime(getActivity()), "Set Evening End Time");
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });
    }

    public void customStartButton(View view){
        String setTime = TimeHelper.Companion.get12hrTime(BAPMPreferences.INSTANCE.getCustomStartTime(getActivity()));
        TextView timeDisplayed = (TextView)view.findViewById(R.id.custom_start_time_displayed);
        timeDisplayed.setText(setTime);

        Button eveningStartButton = (Button)view.findViewById(R.id.custom_start_button);
        eveningStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerFragment.newInstance(TimePickerFragment.TypeOfTimeSet.CUSTOM_TIMESPAN, false, BAPMPreferences.INSTANCE.getCustomStartTime(getActivity()), "Set Custom Start Time");
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });
    }

    public void customEndButton(View view){
        String setTime = TimeHelper.Companion.get12hrTime(BAPMPreferences.INSTANCE.getCustomEndTime(getActivity()));
        TextView timeDisplayed = (TextView)view.findViewById(R.id.custom_end_time_displayed);
        timeDisplayed.setText(setTime);

        Button eveningEndButton = (Button)view.findViewById(R.id.custom_end_button);
        eveningEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = TimePickerFragment.newInstance(TimePickerFragment.TypeOfTimeSet.CUSTOM_TIMESPAN, true, BAPMPreferences.INSTANCE.getCustomEndTime(getActivity()), "Set Custom End Time");
                newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });
    }

    private EditText mLocationNameEditText;
    private Handler mTextChangeHandler = new Handler(Looper.getMainLooper());
    private Runnable mTextChangeRunnable = new Runnable() {
        @Override
        public void run() {
            String enteredText = mLocationNameEditText.getText().toString();
            BusProvider.getBusInstance().post(new LocationNameSetEvent(enteredText));
        }
    };
    public void customLocationName(View view) {
        String locationName = BAPMPreferences.INSTANCE.getCustomLocationName(getActivity());
        mLocationNameEditText = (EditText) view.findViewById(R.id.et_custom_location_name);

        if(!locationName.isEmpty()) {
            mLocationNameEditText.setText(locationName);
        }

        mLocationNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mTextChangeRunnable != null) {
                    mTextChangeHandler.removeCallbacks(mTextChangeRunnable);
                }
                mTextChangeHandler.postDelayed(mTextChangeRunnable, 250);
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
