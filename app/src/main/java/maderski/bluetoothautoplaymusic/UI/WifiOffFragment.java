package maderski.bluetoothautoplaymusic.UI;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CompoundButtonCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashSet;
import java.util.List;

import maderski.bluetoothautoplaymusic.BluetoothDeviceHelper;
import maderski.bluetoothautoplaymusic.BuildConfig;
import maderski.bluetoothautoplaymusic.R;
import maderski.bluetoothautoplaymusic.SharedPrefs.BAPMPreferences;

public class WifiOffFragment extends DialogFragment {


    private OnFragmentInteractionListener mListener;

    public WifiOffFragment() {
        // Required empty public constructor
    }

    public static WifiOffFragment newInstance() {
        WifiOffFragment fragment = new WifiOffFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_wifi_off, container, false);

        checkboxCreator(rootView);

        Button doneButton = (Button)rootView.findViewById(R.id.wifi_off_done);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return rootView;
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

    //Create Checkboxes
    private void checkboxCreator(View view) {

        CheckBox checkBox;
        TextView textView;

        LinearLayout wifiOffCkBoxLL = (LinearLayout) view.findViewById(R.id.wifi_off_ll);
        wifiOffCkBoxLL.removeAllViews();
        List<String> listOfBTDevices = BluetoothDeviceHelper.listOfBluetoothDevices();
        if (listOfBTDevices.contains("No Bluetooth Device found") ||
                listOfBTDevices.isEmpty()){
            textView = new TextView(getActivity());
            textView.setText(R.string.no_BT_found);
            wifiOffCkBoxLL.addView(textView);
        }else{
            for (String BTDevice : listOfBTDevices) {
                int textColor = R.color.colorPrimary;
                checkBox = new CheckBox(getActivity());
                checkBox.setText(BTDevice);
                checkBox.setTextColor(getResources().getColor(textColor));
                checkBox.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/TitilliumText400wt.otf"));
                checkBox.setChecked(BAPMPreferences.getTurnWifiOffDevices(view.getContext()).contains(BTDevice));
                checkboxListener(view.getContext(), checkBox, BTDevice);
                wifiOffCkBoxLL.addView(checkBox);
            }
        }

    }

    //Get Selected Checkboxes
    private void checkboxListener(final Context context, CheckBox checkBox, final String BTDevice) {
        final CheckBox cb = checkBox;

        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashSet<String> wifiOFFDevices = new HashSet<>(BAPMPreferences.getTurnWifiOffDevices(context));
                if (cb.isChecked()) {
                    wifiOFFDevices.add(BTDevice);
                } else {
                    wifiOFFDevices.remove(BTDevice);
                }
                mListener.setWifiOffDevices(wifiOFFDevices);
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void setWifiOffDevices(HashSet<String> wifiOffDevices);
    }
}
