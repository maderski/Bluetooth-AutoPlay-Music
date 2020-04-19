package maderski.bluetoothautoplaymusic.ui.fragments

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView

import java.util.HashSet

import maderski.bluetoothautoplaymusic.helpers.BluetoothDeviceHelper
import maderski.bluetoothautoplaymusic.R
import maderski.bluetoothautoplaymusic.sharedprefs.BAPMPreferences
import org.koin.android.ext.android.inject

class WifiOffFragment : androidx.fragment.app.DialogFragment() {
    private val preferences: BAPMPreferences by inject()
    private val bluetoothDeviceHelper: BluetoothDeviceHelper by inject()

    private var mListener: OnFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_wifi_off, container, false)

        checkboxCreator(rootView)

        val doneButton = rootView.findViewById<View>(R.id.wifi_off_done) as Button
        doneButton.setOnClickListener { dismiss() }

        return rootView
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    //Create Checkboxes
    private fun checkboxCreator(view: View) {

        var checkBox: CheckBox
        val textView: TextView

        val fragmentActivity = requireActivity()

        val wifiOffCkBoxLL = view.findViewById<View>(R.id.wifi_off_ll) as LinearLayout
        wifiOffCkBoxLL.removeAllViews()
        val listOfBTDevices = bluetoothDeviceHelper.listOfBluetoothDevices()
        if (listOfBTDevices.contains("No Bluetooth Device found") || listOfBTDevices.isEmpty()) {
            textView = TextView(fragmentActivity)
            textView.setText(R.string.no_BT_found)
            wifiOffCkBoxLL.addView(textView)
        } else {
            for (BTDevice in listOfBTDevices) {
                val textColor = R.color.colorPrimary
                checkBox = CheckBox(fragmentActivity)
                checkBox.text = BTDevice
                checkBox.setTextColor(resources.getColor(textColor))
                checkBox.typeface = Typeface.createFromAsset(fragmentActivity.assets, "fonts/TitilliumText400wt.otf")
                checkBox.isChecked = preferences.getTurnWifiOffDevices().contains(BTDevice)
                checkboxListener(fragmentActivity, checkBox, BTDevice)
                wifiOffCkBoxLL.addView(checkBox)
            }
        }

    }

    //Get Selected Checkboxes
    private fun checkboxListener(context: Context, checkBox: CheckBox, BTDevice: String) {

        checkBox.setOnClickListener {
            val wifiOFFDevices = HashSet(preferences.getTurnWifiOffDevices())
            if (checkBox.isChecked) {
                wifiOFFDevices.add(BTDevice)
            } else {
                wifiOFFDevices.remove(BTDevice)
            }

            mListener?.setWifiOffDevices(wifiOFFDevices)
        }
    }

    interface OnFragmentInteractionListener {
        fun setWifiOffDevices(wifiOffDevices: HashSet<String>)
    }

    companion object {
        fun newInstance(): WifiOffFragment {
            return WifiOffFragment()
        }
    }
}
