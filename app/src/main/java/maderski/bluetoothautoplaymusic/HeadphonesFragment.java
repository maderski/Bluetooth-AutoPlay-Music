package maderski.bluetoothautoplaymusic;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HeadphonesFragment extends DialogFragment{

    private static final String TAG = "HeadphonesFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SAVED_HEADPHONE_DEVICES = "savedHeadphoneDevices";

    private HashSet mSavedHeadphoneDevices;

    private OnFragmentInteractionListener mListener;

    public HeadphonesFragment() {
        // Required empty public constructor
    }

    public static HeadphonesFragment newInstance(HashSet savedHeadphoneDevices) {
        HeadphonesFragment fragment = new HeadphonesFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SAVED_HEADPHONE_DEVICES, savedHeadphoneDevices);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSavedHeadphoneDevices = (HashSet<String>) getArguments().getSerializable(ARG_SAVED_HEADPHONE_DEVICES);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_headphones, container, false);
        checkboxCreator(rootView);
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
                checkBox = new CheckBox(getActivity());
                checkBox.setText(BTDevice);
                checkBox.setTextColor(getResources().getColor(R.color.colorPrimary));
                checkBox.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/TitilliumText400wt.otf"));
                if(BAPMPreferences.getBTDevices(getActivity()) != null)
                    checkBox.setChecked(BAPMPreferences.getBTDevices(getActivity()).contains(BTDevice));
                checkboxListener(view.getContext(), checkBox, BTDevice);
                autoplayCkBoxLL.addView(checkBox);
            }
        }

    }

    //Get Selected Checkboxes
    private void checkboxListener(Context context, CheckBox checkBox, String BTDevice) {
        final CheckBox cb = checkBox;
        final String BTD = BTDevice;
        final Context ctx = context;

        mSavedHeadphoneDevices = new HashSet<String>(BAPMPreferences.getHeadphoneDevices(context));

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cb.isChecked()) {
                    mSavedHeadphoneDevices.add(BTD);
                    if (BuildConfig.DEBUG)
                        Log.i(TAG, "TRUE" + " " + BTD);
                    BAPMPreferences.setHeadphoneDevices(ctx, mSavedHeadphoneDevices);
                    if (BuildConfig.DEBUG)
                        Log.i(TAG, "SAVED");
                } else {
                    mSavedHeadphoneDevices.remove(BTD);
                    if (BuildConfig.DEBUG)
                        Log.i(TAG, "FALSE" + " " + BTD);
                    BAPMPreferences.setHeadphoneDevices(ctx, mSavedHeadphoneDevices);
                    if (BuildConfig.DEBUG)
                        Log.i(TAG, "SAVED");
                }

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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
