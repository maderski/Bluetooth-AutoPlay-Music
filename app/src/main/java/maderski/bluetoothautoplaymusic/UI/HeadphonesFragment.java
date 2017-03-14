package maderski.bluetoothautoplaymusic.UI;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CompoundButtonCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import maderski.bluetoothautoplaymusic.Helpers.BluetoothDeviceHelper;
import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.R;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

public class HeadphonesFragment extends DialogFragment{

    private static final String TAG = "HeadphonesFragment";

    private HashSet<String> removedDevices = new HashSet<>();

    private OnFragmentInteractionListener mListener;

    public HeadphonesFragment() {
        // Required empty public constructor
    }

    public static HeadphonesFragment newInstance() {
        HeadphonesFragment fragment = new HeadphonesFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_headphones, container, false);
        checkboxCreator(rootView);

        AudioManager audioManager = (AudioManager)getActivity().getSystemService(Context.AUDIO_SERVICE);
        SeekBar volumeSeekBar = (SeekBar)rootView.findViewById(R.id.volume_seekBar);
        volumeSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeSeekBar.setProgress(BAPMPreferences.getHeadphonePreferredVolume(getActivity()));
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                BAPMPreferences.setHeadphonePreferredVolume(getActivity(), progress);
                Log.d(TAG, "Progress: " + Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        Button doneButton = (Button)rootView.findViewById(R.id.autoplay_done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.headphonesDoneClicked(removedDevices);
                dismiss();
            }
        });
        if(removedDevices != null)
            removedDevices.clear();
        return rootView;
    }

    private Set<String> getNonHeadphoneDevices(){
        Set<String> btDevices = BAPMPreferences.getBTDevices(getContext());
        Set<String> headphoneDevices = BAPMPreferences.getHeadphoneDevices(getContext());

        for(String headphoneDevice : headphoneDevices){
            if(btDevices.contains(headphoneDevice)){
                btDevices.remove(headphoneDevice);
            }
        }

        return btDevices;
    }

    //Create Checkboxes
    private void checkboxCreator(View view) {

        CheckBox checkBox;
        TextView textView;

        LinearLayout autoplayCkBoxLL = (LinearLayout) view.findViewById(R.id.autoplay_only_ll);
        autoplayCkBoxLL.removeAllViews();
        List<String> listOfBTDevices = BluetoothDeviceHelper.listOfBluetoothDevices();
        if (listOfBTDevices.contains("No Bluetooth Device found") ||
                listOfBTDevices.isEmpty()){
            textView = new TextView(getActivity());
            textView.setText(R.string.no_BT_found);
            autoplayCkBoxLL.addView(textView);
        }else{
            for (String BTDevice : listOfBTDevices) {
                int textColor = R.color.colorPrimary;
                checkBox = new CheckBox(getActivity());
                checkBox.setText(BTDevice);
                if(getNonHeadphoneDevices().contains(BTDevice)) {
                    textColor = R.color.lightGray;
                    int states[][] = {{android.R.attr.state_checked}};
                    int colors[] = {textColor, textColor};
                    CompoundButtonCompat.setButtonTintList(checkBox, new ColorStateList(states, colors));
                    checkBox.setClickable(false);
                    checkBox.setChecked(true);
                } else if(BAPMPreferences.getBTDevices(getActivity()) != null) {
                    checkBox.setChecked(BAPMPreferences.getHeadphoneDevices(getContext()).contains(BTDevice));
                }
                checkBox.setTextColor(ContextCompat.getColor(getContext(), textColor));
                checkBox.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/TitilliumText400wt.otf"));

                if(!getNonHeadphoneDevices().contains(BTDevice)) {
                    checkboxListener(view.getContext(), checkBox, BTDevice);
                }
                autoplayCkBoxLL.addView(checkBox);
            }
        }

    }

    //Get Selected Checkboxes
    private void checkboxListener(final Context context, CheckBox checkBox, String BTDevice) {
        final CheckBox cb = checkBox;
        final String BTD = BTDevice;

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashSet<String> savedHeadphoneDevices = new HashSet<>(BAPMPreferences.getHeadphoneDevices(context));
                if (cb.isChecked()) {
                    savedHeadphoneDevices.add(BTD);
                    if(removedDevices.contains(BTD)){
                        removedDevices.remove(BTD);
                    }
                    if (BuildConfig.DEBUG) {
                        Log.i(TAG, "TRUE" + " " + BTD);
                        Log.i(TAG, "SAVED");
                    }
                } else {
                    savedHeadphoneDevices.remove(BTD);
                    removedDevices.add(BTD);
                    if (BuildConfig.DEBUG)
                        Log.i(TAG, "FALSE" + " " + BTD);
                    if (BuildConfig.DEBUG)
                        Log.i(TAG, "SAVED");
                }
                mListener.setHeadphoneDevices(savedHeadphoneDevices);
                mListener.headDeviceSelection(BTD, cb.isChecked());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void setHeadphoneDevices(HashSet<String> headphoneDevices);
        void headphonesDoneClicked(HashSet<String> removedDevices);
        void headDeviceSelection(String deviceName, boolean addDevice);
    }
}
